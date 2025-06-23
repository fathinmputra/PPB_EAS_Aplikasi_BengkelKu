package com.example.bengkelku.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelku.data.repository.RepositoryManager
import com.example.bengkelku.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.clip

data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String = "",
    val showArrow: Boolean = true,
    val isDestructive: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVehicleManagement: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositoryManager = remember { RepositoryManager.getInstance(context) }

    // Get data from repositories
    val currentUser by repositoryManager.userRepository.currentUser
    val vehicles by repositoryManager.vehicleRepository.vehicles
    val isLoading by repositoryManager.userRepository.isLoading

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Dynamic vehicle count
    val vehicleCount = vehicles.size
    val userDisplayName = currentUser?.name ?: "User"
    val userEmail = currentUser?.email ?: "user@email.com"
    val userPhone = currentUser?.phone ?: "08123456789"
    val userPoints = currentUser?.totalPoints ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
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
                // Profile Header
                ProfileHeader(
                    userName = userDisplayName,
                    isLoading = isLoading
                )
            }
            item {
                // Membership Info
                MembershipCard(totalPoints = userPoints)
            }
            item {
                // Quick Stats
                QuickStatsCard(vehicleCount = vehicleCount)
            }
            item {
                // Account Settings
                MenuSection(
                    title = "Akun",
                    items = listOf(
                        MenuItem(
                            icon = Icons.Default.Person,
                            title = "Edit Profile",
                            subtitle = "Ubah data personal"
                        ),
                        MenuItem(
                            icon = Icons.Default.Phone,
                            title = "Nomor Telepon",
                            subtitle = userPhone
                        ),
                        MenuItem(
                            icon = Icons.Default.Email,
                            title = "Email",
                            subtitle = userEmail
                        ),
                        MenuItem(
                            icon = Icons.Default.Lock,
                            title = "Ubah Password",
                            subtitle = "Keamanan akun"
                        )
                    )
                )
            }
            item {
                // Vehicle Settings
                MenuSection(
                    title = "Kendaraan",
                    items = listOf(
                        MenuItem(
                            icon = Icons.Default.DirectionsCar,
                            title = "Kelola Kendaraan",
                            subtitle = "$vehicleCount kendaraan terdaftar"
                        ),
                        MenuItem(
                            icon = Icons.Default.Add,
                            title = "Tambah Kendaraan",
                            subtitle = "Daftarkan kendaraan baru"
                        )
                    ),
                    onItemClick = { menuItem ->
                        if (menuItem.title == "Kelola Kendaraan" ||
                            menuItem.title == "Tambah Kendaraan") {
                            onNavigateToVehicleManagement()
                        }
                    }
                )
            }
            item {
                // Notification Settings
                MenuSection(
                    title = "Notifikasi",
                    items = listOf(
                        MenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Pengaturan Notifikasi",
                            subtitle = "Atur reminder service"
                        ),
                        MenuItem(
                            icon = Icons.Default.Schedule,
                            title = "Reminder Service",
                            subtitle = "Setiap 3 bulan"
                        )
                    )
                )
            }
            item {
                // Support & Info
                MenuSection(
                    title = "Bantuan & Info",
                    items = listOf(
                        MenuItem(
                            icon = Icons.Default.Help,
                            title = "Pusat Bantuan",
                            subtitle = "FAQ dan panduan"
                        ),
                        MenuItem(
                            icon = Icons.Default.ContactSupport,
                            title = "Hubungi Kami",
                            subtitle = "Customer service"
                        ),
                        MenuItem(
                            icon = Icons.Default.Info,
                            title = "Tentang Aplikasi",
                            subtitle = "Versi 1.0.0"
                        ),
                        MenuItem(
                            icon = Icons.Default.Star,
                            title = "Beri Rating",
                            subtitle = "Rate di Play Store"
                        )
                    )
                )
            }
            item {
                // Logout Section
                MenuSection(
                    title = "Lainnya",
                    items = listOf(
                        MenuItem(
                            icon = Icons.Default.ExitToApp,
                            title = "Keluar",
                            subtitle = "Logout dari akun",
                            showArrow = false,
                            isDestructive = true
                        )
                    ),
                    onItemClick = { menuItem ->
                        if (menuItem.title == "Keluar") {
                            showLogoutDialog = true
                        }
                    }
                )
            }
            item {
                // App Version Footer
                AppVersionFooter()
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                scope.launch {
                    showLogoutDialog = false
                    repositoryManager.userRepository.logout()
                    onLogout()
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(
    userName: String,
    isLoading: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Primary, PrimaryVariant)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Member sejak Des 2023",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Success.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "✨ VIP Member",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { /* TODO: Edit profile */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MembershipCard(totalPoints: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Warning.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Membership",
                        tint = Warning,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Poin Rewards",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        text = "$totalPoints poin tersedia",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
                Text(
                    text = "$totalPoints",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Warning
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: Tukar poin */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Warning
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Tukar Poin",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tukar Poin Rewards",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun QuickStatsCard(vehicleCount: Int = 2) {
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
                text = "Statistik Singkat",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStatItem(
                    icon = Icons.Default.Build,
                    value = "4",
                    label = "Total Service",
                    color = Primary
                )
                QuickStatItem(
                    icon = Icons.Default.DirectionsCar,
                    value = "$vehicleCount",
                    label = "Kendaraan",
                    color = Success
                )
                QuickStatItem(
                    icon = Icons.Default.Schedule,
                    value = "3",
                    label = "Bulan Terakhir",
                    color = Warning
                )
            }
        }
    }
}

@Composable
fun QuickStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MenuSection(
    title: String,
    items: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit = {}
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
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            items.forEachIndexed { index, item ->
                MenuItemRow(
                    item = item,
                    onClick = { onItemClick(item) }
                )
                if (index < items.size - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = CardBorder
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemRow(
    item: MenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (item.isDestructive)
                        Error.copy(alpha = 0.1f)
                    else
                        Primary.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (item.isDestructive) Error else Primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (item.isDestructive) Error else OnSurface
            )
            if (item.subtitle.isNotEmpty()) {
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
            }
        }
        if (item.showArrow) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Arrow",
                tint = OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AppVersionFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BengkelKu",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Text(
            text = "Versi 1.0.0",
            fontSize = 12.sp,
            color = OnSurfaceVariant
        )
        Text(
            text = "© 2024 BengkelKu. All rights reserved.",
            fontSize = 10.sp,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                    Text(
                        text = "⚠️",
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Konfirmasi Logout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = "Apakah Anda yakin ingin keluar dari akun? Anda harus login kembali untuk menggunakan aplikasi.",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Logout Button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Ya, Keluar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // Cancel Button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = OnSurfaceVariant
                    ),
                    border = BorderStroke(1.5.dp, OnSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Batal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurfaceVariant
                    )
                }
            }
        },
        dismissButton = null,
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
fun ProfileScreenPreview() {
    BengkelkuTheme {
        ProfileScreen(
            onNavigateBack = {},
            onNavigateToVehicleManagement = {},
            onLogout = {}
        )
    }
}