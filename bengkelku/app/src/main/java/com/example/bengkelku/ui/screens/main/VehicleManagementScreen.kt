package com.example.bengkelku.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelku.data.model.Vehicle
import com.example.bengkelku.data.repository.RepositoryManager
import com.example.bengkelku.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleManagementScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositoryManager = remember { RepositoryManager.getInstance(context) }

    // Get data from repositories
    val vehicles by repositoryManager.vehicleRepository.vehicles
    val isLoading by repositoryManager.vehicleRepository.isLoading

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    // Clear messages after some time
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            kotlinx.coroutines.delay(3000)
            errorMessage = ""
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage.isNotEmpty()) {
            kotlinx.coroutines.delay(3000)
            successMessage = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kelola Kendaraan",
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
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Vehicle",
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

        // Show messages
        if (errorMessage.isNotEmpty() || successMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                if (errorMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Error,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                }
                if (successMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = successMessage,
                            color = Success,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (vehicles.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyVehicleState(
                    onAddClick = { showAddDialog = true },
                    isLoading = isLoading
                )
            }
        } else {
            // Vehicle List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    VehicleHeader(vehicleCount = vehicles.size)
                }

                items(vehicles) { vehicle ->
                    VehicleItemCard(
                        vehicle = vehicle,
                        onEditClick = {
                            selectedVehicle = vehicle
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            selectedVehicle = vehicle
                            showDeleteDialog = true
                        },
                        isLoading = isLoading
                    )
                }

                item {
                    AddVehicleCard(
                        onClick = { showAddDialog = true },
                        isLoading = isLoading
                    )
                }
            }
        }
    }

    // Add Vehicle Dialog
    if (showAddDialog) {
        VehicleFormDialog(
            title = "Tambah Kendaraan",
            vehicle = null,
            onDismiss = { showAddDialog = false },
            onSave = { newVehicle ->
                scope.launch {
                    val result = repositoryManager.vehicleRepository.addVehicle(newVehicle)
                    result.fold(
                        onSuccess = {
                            successMessage = "Kendaraan berhasil ditambahkan!"
                            showAddDialog = false
                        },
                        onFailure = { exception ->
                            errorMessage = exception.message ?: "Gagal menambahkan kendaraan"
                        }
                    )
                }
            }
        )
    }

    // Edit Vehicle Dialog
    if (showEditDialog && selectedVehicle != null) {
        VehicleFormDialog(
            title = "Edit Kendaraan",
            vehicle = selectedVehicle,
            onDismiss = {
                showEditDialog = false
                selectedVehicle = null
            },
            onSave = { updatedVehicle ->
                scope.launch {
                    val result = repositoryManager.vehicleRepository.updateVehicle(updatedVehicle)
                    result.fold(
                        onSuccess = {
                            successMessage = "Kendaraan berhasil diperbarui!"
                            showEditDialog = false
                            selectedVehicle = null
                        },
                        onFailure = { exception ->
                            errorMessage = exception.message ?: "Gagal memperbarui kendaraan"
                        }
                    )
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedVehicle != null) {
        DeleteConfirmationDialog(
            vehicleName = "${selectedVehicle!!.brand} ${selectedVehicle!!.model}",
            onDismiss = {
                showDeleteDialog = false
                selectedVehicle = null
            },
            onConfirm = {
                scope.launch {
                    val result = repositoryManager.vehicleRepository.deleteVehicle(selectedVehicle!!.vehicleId)
                    result.fold(
                        onSuccess = {
                            successMessage = "Kendaraan berhasil dihapus!"
                            showDeleteDialog = false
                            selectedVehicle = null
                        },
                        onFailure = { exception ->
                            errorMessage = exception.message ?: "Gagal menghapus kendaraan"
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun VehicleHeader(vehicleCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Vehicles",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Kendaraan Terdaftar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Text(
                    text = "$vehicleCount kendaraan",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VehicleItemCard(
    vehicle: Vehicle,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isLoading: Boolean = false
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            if (vehicle.type == "Motor")
                                Success.copy(alpha = 0.1f)
                            else
                                Warning.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (vehicle.type == "Motor") "🏍" else "🚗",
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        text = vehicle.plateNumber,
                        fontSize = 14.sp,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${vehicle.type} • ${vehicle.year}",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                }
                Row {
                    IconButton(
                        onClick = onEditClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = if (isLoading) OnSurfaceVariant.copy(alpha = 0.5f) else Primary
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = if (isLoading) OnSurfaceVariant.copy(alpha = 0.5f) else Error
                        )
                    }
                }
            }

            if (vehicle.lastServiceDate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Info.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Last Service",
                            tint = Info,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Service terakhir: ${vehicle.lastServiceDate}",
                            fontSize = 11.sp,
                            color = OnSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddVehicleCard(
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Primary.copy(alpha = 0.05f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            Primary.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Primary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Vehicle",
                        tint = Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tambah Kendaraan",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
            Text(
                text = "Daftarkan kendaraan baru",
                fontSize = 11.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyVehicleState(
    onAddClick: () -> Unit,
    isLoading: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🚗",
            fontSize = 72.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Belum Ada Kendaraan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Daftarkan kendaraan pertama Anda untuk mulai menggunakan layanan service",
            fontSize = 14.sp,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tambah Kendaraan",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleFormDialog(
    title: String,
    vehicle: Vehicle?,
    onDismiss: () -> Unit,
    onSave: (Vehicle) -> Unit
) {
    var brand by remember { mutableStateOf(vehicle?.brand ?: "") }
    var model by remember { mutableStateOf(vehicle?.model ?: "") }
    var plateNumber by remember { mutableStateOf(vehicle?.plateNumber ?: "") }
    var year by remember { mutableStateOf(vehicle?.year?.toString() ?: "") }
    var type by remember { mutableStateOf(vehicle?.type ?: "Motor") }
    var errorMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val typeOptions = listOf("Motor", "Mobil")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Brand Field
                OutlinedTextField(
                    value = brand,
                    onValueChange = {
                        brand = it
                        errorMessage = ""
                    },
                    label = { Text("Merk") },
                    placeholder = { Text("Honda, Yamaha, Toyota, dll") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSubmitting
                )

                // Model Field
                OutlinedTextField(
                    value = model,
                    onValueChange = {
                        model = it
                        errorMessage = ""
                    },
                    label = { Text("Model") },
                    placeholder = { Text("Beat, Vario, Avanza, dll") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSubmitting
                )

                // Plate Number Field
                OutlinedTextField(
                    value = plateNumber,
                    onValueChange = {
                        plateNumber = it.uppercase()
                        errorMessage = ""
                    },
                    label = { Text("Nomor Plat") },
                    placeholder = { Text("B 1234 XYZ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSubmitting
                )

                // Year Field
                OutlinedTextField(
                    value = year,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 4) {
                            year = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("Tahun") },
                    placeholder = { Text("2020") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSubmitting
                )

                // Type Selection
                Column {
                    Text(
                        text = "Jenis Kendaraan",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        typeOptions.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = type == option,
                                    onClick = { if (!isSubmitting) type = option },
                                    enabled = !isSubmitting
                                )
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable(enabled = !isSubmitting) {
                                        type = option
                                    }
                                )
                            }
                        }
                    }
                }

                // Error Message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        brand.isEmpty() -> errorMessage = "Merk tidak boleh kosong"
                        model.isEmpty() -> errorMessage = "Model tidak boleh kosong"
                        plateNumber.isEmpty() -> errorMessage = "Nomor plat tidak boleh kosong"
                        year.isEmpty() -> errorMessage = "Tahun tidak boleh kosong"
                        year.toIntOrNull() == null -> errorMessage = "Tahun harus berupa angka"
                        year.toInt() < 1990 || year.toInt() > 2025 -> {
                            errorMessage = "Tahun harus antara 1990-2025"
                        }
                        else -> {
                            isSubmitting = true
                            val newVehicle = Vehicle(
                                vehicleId = vehicle?.vehicleId ?: "",
                                userId = vehicle?.userId ?: "",
                                brand = brand,
                                model = model,
                                plateNumber = plateNumber,
                                year = year.toInt(),
                                type = type,
                                lastServiceDate = vehicle?.lastServiceDate ?: ""
                            )
                            onSave(newVehicle)
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text("Simpan", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Batal", color = Primary)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun DeleteConfirmationDialog(
    vehicleName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var isDeleting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Error.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Error,
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Hapus Kendaraan",
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
                    text = "Apakah Anda yakin ingin menghapus kendaraan",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "\"$vehicleName\"?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
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
                // Danger Button - Delete
                Button(
                    onClick = {
                        isDeleting = true
                        onConfirm()
                    },
                    enabled = !isDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error,
                        disabledContainerColor = Error.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isDeleting) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Menghapus...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    } else {
                        Text(
                            text = "Ya, Hapus",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
                
                // Cancel Button - Outlined
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = OnSurfaceVariant,
                        disabledContentColor = OnSurfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(
                        1.5.dp, 
                        if (isDeleting) OnSurfaceVariant.copy(alpha = 0.3f) else OnSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Batal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDeleting) OnSurfaceVariant.copy(alpha = 0.5f) else OnSurfaceVariant
                    )
                }
            }
        },
        dismissButton = null, // Remove default dismiss button since we have custom layout
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        titleContentColor = OnSurface,
        textContentColor = OnSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .wrapContentHeight()
    )
}

@Preview(showBackground = true)
@Composable
fun VehicleManagementScreenPreview() {
    BengkelkuTheme {
        VehicleManagementScreen(
            onNavigateBack = {}
        )
    }
}