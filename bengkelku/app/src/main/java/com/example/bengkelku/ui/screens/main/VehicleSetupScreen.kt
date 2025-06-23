package com.example.bengkelku.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelku.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSetupScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit = {} // Added this parameter
) {
    val scope = rememberCoroutineScope()
    var vehicleBrand by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    var vehicleYear by remember { mutableStateOf("") }
    var selectedVehicleType by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var canSkip by remember { mutableStateOf(true) }
    var showCompletionDialog by remember { mutableStateOf(false) } // Added this state

    val scrollState = rememberScrollState()
    val vehicleTypes = listOf(
        "Mobil" to "🚗",
        "Motor" to "🏍"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Primary,
                        PrimaryVariant
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🚗",
                        fontSize = 40.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tambah Kendaraan",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Daftarkan kendaraan Anda untuk mulai menggunakan layanan bengkel",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Vehicle Setup Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Data Kendaraan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Vehicle Type Selection
                    Text(
                        text = "Jenis Kendaraan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        vehicleTypes.forEach { (type, emoji) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .selectable(
                                        selected = selectedVehicleType == type,
                                        onClick = {
                                            selectedVehicleType = type
                                            errorMessage = ""
                                        }
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedVehicleType == type)
                                        Primary.copy(alpha = 0.1f) else SurfaceVariant
                                ),
                                border = if (selectedVehicleType == type)
                                    androidx.compose.foundation.BorderStroke(2.dp, Primary) else null,
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (selectedVehicleType == type) 6.dp else 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 36.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = type,
                                        fontSize = 16.sp,
                                        fontWeight = if (selectedVehicleType == type)
                                            FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedVehicleType == type)
                                            Primary else OnSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Vehicle Brand Field
                    OutlinedTextField(
                        value = vehicleBrand,
                        onValueChange = {
                            vehicleBrand = it
                            errorMessage = ""
                        },
                        label = { Text("Merk Kendaraan") },
                        placeholder = { Text("Toyota, Honda, Yamaha, dll") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Brand Icon",
                                tint = Primary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vehicle Model Field
                    OutlinedTextField(
                        value = vehicleModel,
                        onValueChange = {
                            vehicleModel = it
                            errorMessage = ""
                        },
                        label = { Text("Model Kendaraan") },
                        placeholder = { Text("Avanza, Beat, Vixion, dll") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Model Icon",
                                tint = Primary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Plate Number Field
                    OutlinedTextField(
                        value = plateNumber,
                        onValueChange = {
                            plateNumber = it.uppercase()
                            errorMessage = ""
                        },
                        label = { Text("Nomor Plat") },
                        placeholder = { Text("B 1234 XYZ") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Plate Icon",
                                tint = Primary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vehicle Year Field
                    OutlinedTextField(
                        value = vehicleYear,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                vehicleYear = it
                                errorMessage = ""
                            }
                        },
                        label = { Text("Tahun Kendaraan") },
                        placeholder = { Text("2020") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Year Icon",
                                tint = Primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        ),
                        singleLine = true
                    )

                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = Error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button - UPDATED LOGIC
                    Button(
                        onClick = {
                            when {
                                selectedVehicleType.isEmpty() -> {
                                    errorMessage = "Pilih jenis kendaraan"
                                }
                                vehicleBrand.isEmpty() -> {
                                    errorMessage = "Merk kendaraan tidak boleh kosong"
                                }
                                vehicleModel.isEmpty() -> {
                                    errorMessage = "Model kendaraan tidak boleh kosong"
                                }
                                plateNumber.isEmpty() -> {
                                    errorMessage = "Nomor plat tidak boleh kosong"
                                }
                                vehicleYear.isEmpty() -> {
                                    errorMessage = "Tahun kendaraan tidak boleh kosong"
                                }
                                vehicleYear.toIntOrNull()?.let { it < 1980 || it > 2024 } == true -> {
                                    errorMessage = "Tahun kendaraan tidak valid"
                                }
                                else -> {
                                    isLoading = true
                                    scope.launch {
                                        // Simulate save delay
                                        delay(1500)
                                        isLoading = false

                                        // Show completion dialog instead of going to dashboard
                                        showCompletionDialog = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Simpan Kendaraan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // Skip Option (if enabled)
                    if (canSkip) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = {
                                // For demo: Skip also goes to completion dialog
                                showCompletionDialog = true
                            }
                        ) {
                            Text(
                                text = "Lewati untuk sekarang",
                                color = OnSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // COMPLETION DIALOG - ADDED
    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✅",
                            fontSize = 40.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pendaftaran Selesai!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Akun Anda telah berhasil dibuat. Silakan login menggunakan akun yang baru saja didaftarkan.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompletionDialog = false
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Login Sekarang",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            titleContentColor = OnSurface,
            textContentColor = OnSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VehicleSetupScreenPreview() {
    BengkelkuTheme {
        VehicleSetupScreen(
            onNavigateToDashboard = {},
            onNavigateToLogin = {}
        )
    }
}