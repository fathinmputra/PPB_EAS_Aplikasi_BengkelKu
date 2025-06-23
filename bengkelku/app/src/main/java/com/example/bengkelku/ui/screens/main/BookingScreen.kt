package com.example.bengkelku.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelku.data.model.Booking
import com.example.bengkelku.data.repository.RepositoryManager
import com.example.bengkelku.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp

data class ServiceOption(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val points: Int,
    val duration: String
)

data class VehicleOption(
    val id: String,
    val name: String,
    val plateNumber: String,
    val type: String
)

data class TimeSlot(
    val id: String,
    val time: String,
    val isAvailable: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositoryManager = remember { RepositoryManager.getInstance(context) }

    // Get data from repositories - using .value instead of collectAsState()
    val vehicles by repositoryManager.vehicleRepository.vehicles
    val serviceTypes by repositoryManager.bookingRepository.serviceTypes
    val isLoading by repositoryManager.bookingRepository.isLoading

    var selectedVehicle by remember { mutableStateOf<VehicleOption?>(null) }
    var selectedService by remember { mutableStateOf<ServiceOption?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTimeSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var notes by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Convert repository data to UI models
    val vehicleOptions = remember(vehicles) {
        vehicles.map { vehicle ->
            VehicleOption(
                id = vehicle.vehicleId,
                name = "${vehicle.brand} ${vehicle.model}",
                plateNumber = vehicle.plateNumber,
                type = vehicle.type
            )
        }
    }

    val serviceOptions = remember(serviceTypes) {
        serviceTypes.map { serviceType ->
            ServiceOption(
                id = serviceType.serviceId,
                name = serviceType.name,
                description = serviceType.description,
                price = serviceType.price,
                points = serviceType.pointsReward,
                duration = serviceType.estimatedTime
            )
        }
    }

    val timeSlots = remember {
        listOf(
            TimeSlot("1", "08:00 - 09:00"),
            TimeSlot("2", "09:00 - 10:00"),
            TimeSlot("3", "10:00 - 11:00"),
            TimeSlot("4", "11:00 - 12:00"),
            TimeSlot("5", "13:00 - 14:00"),
            TimeSlot("6", "14:00 - 15:00"),
            TimeSlot("7", "15:00 - 16:00"),
            TimeSlot("8", "16:00 - 17:00")
        )
    }

    // Check if we have vehicles for booking
    if (vehicleOptions.isEmpty()) {
        // Show no vehicles state
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Booking Service",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(text = "🚗", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum Ada Kendaraan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tambahkan kendaraan terlebih dahulu di menu Profile untuk melakukan booking service",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Kembali", color = Color.White)
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking Service",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header Info
                BookingHeaderCard()
            }
            item {
                // Vehicle Selection
                VehicleSelectionSection(
                    vehicles = vehicleOptions,
                    selectedVehicle = selectedVehicle,
                    onVehicleSelected = { selectedVehicle = it }
                )
            }
            item {
                // Service Selection
                ServiceSelectionSection(
                    services = serviceOptions,
                    selectedService = selectedService,
                    onServiceSelected = { selectedService = it }
                )
            }
            item {
                // Date Selection
                DateSelectionSection(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }
            item {
                // Time Slot Selection
                TimeSlotSelectionSection(
                    timeSlots = timeSlots,
                    selectedTimeSlot = selectedTimeSlot,
                    onTimeSlotSelected = { selectedTimeSlot = it }
                )
            }
            item {
                // Notes Section
                NotesSection(
                    notes = notes,
                    onNotesChanged = { notes = it }
                )
            }
            item {
                // Booking Summary
                if (selectedVehicle != null && selectedService != null) {
                    BookingSummaryCard(
                        vehicle = selectedVehicle!!,
                        service = selectedService!!,
                        date = selectedDate,
                        timeSlot = selectedTimeSlot?.time ?: ""
                    )
                }
            }
            item {
                // Booking Button
                BookingButton(
                    isEnabled = selectedVehicle != null &&
                            selectedService != null &&
                            selectedDate.isNotEmpty() &&
                            selectedTimeSlot != null,
                    isLoading = isLoading,
                    onClick = {
                        scope.launch {
                            val newBooking = Booking(
                                vehicleId = selectedVehicle!!.id,
                                serviceTypeId = selectedService!!.id,
                                bookingDate = selectedDate,
                                timeSlot = selectedTimeSlot!!.time,
                                notes = notes,
                                totalPrice = selectedService!!.price
                            )

                            val result = repositoryManager.bookingRepository.createBooking(newBooking)
                            result.fold(
                                onSuccess = {
                                    showSuccessDialog = true
                                },
                                onFailure = {
                                    // Handle error - could show error dialog
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        BookingSuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                onNavigateToDashboard()
            },
            onNavigateToHistory = {
                showSuccessDialog = false
                onNavigateToHistory()
            }
        )
    }
}

@Composable
fun BookingHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Booking",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pilih jenis service yang dibutuhkan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
            }
            Text(
                text = "Isi form di bawah untuk menjadwalkan service kendaraan Anda",
                fontSize = 12.sp,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun VehicleSelectionSection(
    vehicles: List<VehicleOption>,
    selectedVehicle: VehicleOption?,
    onVehicleSelected: (VehicleOption) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pilih Kendaraan",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            vehicles.forEach { vehicle ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedVehicle?.id == vehicle.id,
                            onClick = { onVehicleSelected(vehicle) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedVehicle?.id == vehicle.id,
                        onClick = { onVehicleSelected(vehicle) },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${vehicle.name} - ${vehicle.plateNumber}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                        Text(
                            text = vehicle.type,
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceSelectionSection(
    services: List<ServiceOption>,
    selectedService: ServiceOption?,
    onServiceSelected: (ServiceOption) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pilih Jenis Service",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            services.forEach { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedService?.id == service.id)
                            Primary.copy(alpha = 0.1f) else SurfaceVariant
                    ),
                    border = if (selectedService?.id == service.id)
                        androidx.compose.foundation.BorderStroke(2.dp, Primary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedService?.id == service.id,
                                onClick = { onServiceSelected(service) }
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedService?.id == service.id,
                            onClick = { onServiceSelected(service) },
                            colors = RadioButtonDefaults.colors(selectedColor = Primary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = service.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                            Text(
                                text = service.description,
                                fontSize = 12.sp,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = "Durasi: ${service.duration}",
                                fontSize = 11.sp,
                                color = OnSurfaceVariant
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Rp ${String.format("%,d", service.price)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Text(
                                text = "+${service.points} poin",
                                fontSize = 11.sp,
                                color = Success
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionSection(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Date picker state
    val calendar = Calendar.getInstance()
    val tomorrow = calendar.apply {
        add(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = tomorrow,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                return utcTimeMillis >= today + (24 * 60 * 60 * 1000) // From tomorrow
            }
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pilih Tanggal Service",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = if (selectedDate.isNotEmpty()) {
                    formatDisplayDate(selectedDate)
                } else {
                    ""
                },
                onValueChange = { },
                label = { Text("Tanggal Service") },
                placeholder = { Text("Pilih tanggal") },
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            tint = Primary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )
        }
    }

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val selectedDateString = formatter.format(Date(millis))
                            onDateSelected(selectedDateString)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Batal", color = OnSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Primary,
                    todayDateBorderColor = Primary
                )
            )
        }
    }
}

@Composable
fun TimeSlotSelectionSection(
    timeSlots: List<TimeSlot>,
    selectedTimeSlot: TimeSlot?,
    onTimeSlotSelected: (TimeSlot) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pilih Jam Service",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Time slots grid
            val chunkedTimeSlots = timeSlots.chunked(2)
            chunkedTimeSlots.forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSlots.forEach { timeSlot ->
                        TimeSlotChip(
                            timeSlot = timeSlot,
                            isSelected = selectedTimeSlot?.id == timeSlot.id,
                            onClick = { onTimeSlotSelected(timeSlot) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if odd number of slots in row
                    if (rowSlots.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TimeSlotChip(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(enabled = timeSlot.isAvailable) {
                if (timeSlot.isAvailable) onClick()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Primary
                !timeSlot.isAvailable -> OnSurfaceVariant.copy(alpha = 0.3f)
                else -> SurfaceVariant
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeSlot.time,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isSelected -> Color.White
                    !timeSlot.isAvailable -> OnSurfaceVariant.copy(alpha = 0.5f)
                    else -> OnSurface
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSection(
    notes: String,
    onNotesChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Catatan (Opsional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChanged,
                label = { Text("Catatan tambahan") },
                placeholder = { Text("Keluhan atau permintaan khusus...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary,
                    cursorColor = Primary
                )
            )
        }
    }
}

@Composable
fun BookingSummaryCard(
    vehicle: VehicleOption,
    service: ServiceOption,
    date: String,
    timeSlot: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ringkasan Booking",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow("Kendaraan", "${vehicle.name} - ${vehicle.plateNumber}")
            SummaryRow("Service", service.name)
            if (date.isNotEmpty()) {
                SummaryRow("Tanggal", formatDisplayDate(date))
            }
            if (timeSlot.isNotEmpty()) {
                SummaryRow("Jam", timeSlot)
            }
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Primary.copy(alpha = 0.3f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Biaya",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Text(
                    text = "Rp ${String.format("%,d", service.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Poin yang didapat",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "+${service.points} poin",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Success
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = OnSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurface
        )
    }
}

@Composable
fun BookingButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            disabledContainerColor = OnSurfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = "Konfirmasi Booking",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun BookingSuccessDialog(
    onDismiss: () -> Unit,
    onNavigateToHistory: () -> Unit // ✅ ADD: New parameter for history navigation
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon with better styling
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Success.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Success,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Booking Berhasil!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Booking service Anda telah berhasil dijadwalkan. Silakan datang ke bengkel sesuai waktu yang dipilih.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Data booking bisa dilihat di menu History.",
                    fontSize = 13.sp,
                    color = Primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary Button - Dashboard
                Button(
                    onClick = onDismiss, // ✅ Navigate to Dashboard
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Ke Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // Secondary Button - History
                OutlinedButton(
                    onClick = onNavigateToHistory, // ✅ FIXED: Navigate to History
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary
                    ),
                    border = BorderStroke(1.5.dp, Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Lihat History",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary
                    )
                }
            }
        },
        dismissButton = null,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .wrapContentHeight(),
        titleContentColor = OnSurface,
        textContentColor = OnSurfaceVariant
    )
}

// Fungsi helper untuk format tanggal tampilan
fun formatDisplayDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString // Fallback ke format asli jika parsing gagal
    }
}

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    BengkelkuTheme {
        BookingScreen(
            onNavigateBack = {},
            onNavigateToDashboard = {},
            onNavigateToHistory = {}
        )
    }
}