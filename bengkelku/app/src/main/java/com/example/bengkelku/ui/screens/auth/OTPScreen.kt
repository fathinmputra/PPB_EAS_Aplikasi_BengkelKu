package com.example.bengkelku.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelku.ui.theme.*
import com.example.bengkelku.data.repository.RepositoryManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreen(
    phone: String,
    onNavigateBack: () -> Unit,
    onNavigateToVehicleSetup: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositoryManager = remember { RepositoryManager.getInstance(context) }

    var otpValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var resendCooldown by remember { mutableStateOf(0) }

    // Auto-fill OTP for quick testing
    LaunchedEffect(Unit) {
        otpValue = "123456"
    }

    // Resend cooldown timer
    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            kotlinx.coroutines.delay(1000)
            resendCooldown--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Primary, PrimaryVariant)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Verifikasi OTP",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Masukkan kode yang dikirim ke nomor Anda",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // OTP Verification Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "OTP",
                            tint = Primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Kode Verifikasi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kode OTP telah dikirim ke",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = formatPhoneNumber(phone),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // OTP Input Field
                    OutlinedTextField(
                        value = otpValue,
                        onValueChange = { newValue ->
                            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                                otpValue = newValue
                                errorMessage = ""
                            }
                        },
                        label = { Text("Kode OTP") },
                        placeholder = { Text("Masukkan 6 digit kode") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    // Success Message
                    if (showSuccessMessage) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Verifikasi berhasil! Mengalihkan...",
                            color = Success,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

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

                    // Verify Button
                    Button(
                        onClick = {
                            when {
                                otpValue.length < 6 -> {
                                    errorMessage = "Kode OTP harus 6 digit"
                                }
                                else -> {
                                    scope.launch {
                                        isLoading = true
                                        errorMessage = ""

                                        val result = repositoryManager.userRepository.verifyOTP(phone, otpValue)

                                        result.fold(
                                            onSuccess = { user ->
                                                showSuccessMessage = true
                                                kotlinx.coroutines.delay(1000)

                                                onNavigateToVehicleSetup()
                                            },
                                            onFailure = { exception ->
                                                errorMessage = when (exception.message) {
                                                    "User tidak ditemukan" -> "Kode OTP salah atau user tidak ditemukan"
                                                    else -> "Verifikasi gagal. Coba lagi."
                                                }
                                                isLoading = false
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        enabled = !isLoading && otpValue.length == 6
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Verifikasi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resend Section
                    if (resendCooldown > 0) {
                        Text(
                            text = "Kirim ulang dalam ${resendCooldown}s",
                            color = OnSurfaceVariant,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            text = "Kirim Ulang Kode",
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                // Reset form and start cooldown
                                otpValue = ""
                                errorMessage = ""
                                resendCooldown = 60
                                // Auto-fill for demo
                                otpValue = "123456"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Change Phone Number
                    Text(
                        text = "Ubah Nomor HP",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            onNavigateBack()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💡 Tips",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                    Text(
                        text = "Kode OTP berlaku selama 5 menit. Periksa SMS atau WhatsApp Anda.",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Helper function untuk format nomor HP
fun formatPhoneNumber(phone: String): String {
    return if (phone.length >= 4) {
        val start = phone.take(4)
        val end = phone.takeLast(4)
        val middle = "*".repeat(phone.length - 8)
        "$start$middle$end"
    } else {
        phone
    }
}