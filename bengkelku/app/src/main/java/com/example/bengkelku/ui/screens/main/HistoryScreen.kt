package com.example.bengkelku.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bengkelku.data.model.BookingStatus
import com.example.bengkelku.data.repository.RepositoryManager
import com.example.bengkelku.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositoryManager = remember { RepositoryManager.getInstance(context) }

    // Get data from repositories
    val enrichedBookings = remember {
        mutableStateOf(repositoryManager.bookingRepository.getEnrichedBookings())
    }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Update data when screen recomposes or refresh triggered
    LaunchedEffect(refreshTrigger) {
        enrichedBookings.value = repositoryManager.bookingRepository.getEnrichedBookings()
    }

    // Also refresh when screen becomes visible
    LaunchedEffect(Unit) {
        enrichedBookings.value = repositoryManager.bookingRepository.getEnrichedBookings()
    }

    val filterOptions = listOf("Semua", "Selesai", "Dikonfirmasi", "Menunggu", "Dikerjakan")

    val filteredItems = remember(selectedFilter, enrichedBookings.value) {
        when (selectedFilter) {
            "Selesai" -> enrichedBookings.value.filter {
                it.booking.status == BookingStatus.COMPLETED
            }
            "Dikonfirmasi" -> enrichedBookings.value.filter {
                it.booking.status == BookingStatus.CONFIRMED
            }
            "Menunggu" -> enrichedBookings.value.filter {
                it.booking.status == BookingStatus.PENDING
            }
            "Dikerjakan" -> enrichedBookings.value.filter {
                it.booking.status == BookingStatus.IN_PROGRESS
            }
            else -> enrichedBookings.value
        }
    }

    // Calculate stats from real data
    val bookingStats = remember(enrichedBookings.value) {
        repositoryManager.bookingRepository.getBookingStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Service",
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
                // Header Stats Card
                HistoryStatsCard(
                    totalServices = bookingStats.completedBookings,
                    totalPoints = bookingStats.totalPointsEarned,
                    totalSpent = bookingStats.totalSpent
                )
            }

            item {
                // Filter Section
                FilterSection(
                    filterOptions = filterOptions,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            if (filteredItems.isEmpty()) {
                item {
                    EmptyHistoryCard(selectedFilter)
                }
            } else {
                items(filteredItems) { enrichedBooking ->
                    HistoryItemCard(
                        enrichedBooking = enrichedBooking,
                        onStatusUpdate = { bookingId, newStatus ->
                            scope.launch {
                                repositoryManager.bookingRepository.updateBookingStatus(
                                    bookingId,
                                    newStatus
                                )
                                // Trigger refresh
                                refreshTrigger++
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryStatsCard(
    totalServices: Int,
    totalPoints: Int,
    totalSpent: Int
) {
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
                        .background(Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Stats",
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Statistik Service Anda",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = "Riwayat lengkap aktivitas service",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Build,
                    value = "$totalServices",
                    label = "Service",
                    color = Primary
                )
                StatItem(
                    icon = Icons.Default.Stars,
                    value = "$totalPoints",
                    label = "Total Poin",
                    color = Success
                )
                StatItem(
                    icon = Icons.Default.AttachMoney,
                    value = "Rp ${String.format("%,d", totalSpent)}",
                    label = "Total Biaya",
                    color = Warning,
                    isSmallText = true
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    isSmallText: Boolean = false
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
            fontSize = if (isSmallText) 12.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
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
fun FilterSection(
    filterOptions: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
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
                text = "Filter Riwayat",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterChip(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { onFilterSelected(filter) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary else SurfaceVariant
        ),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
            1.dp, CardBorder
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else OnSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun HistoryItemCard(
    enrichedBooking: com.example.bengkelku.data.repository.EnrichedBooking,
    onStatusUpdate: (String, BookingStatus) -> Unit = { _, _ -> }
) {
    val booking = enrichedBooking.booking
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with service name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(getServiceIconBackgroundColor(enrichedBooking.serviceName)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getServiceIcon(enrichedBooking.serviceName),
                            contentDescription = enrichedBooking.serviceName,
                            tint = getServiceIconColor(enrichedBooking.serviceName),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = enrichedBooking.serviceName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                        Text(
                            text = "${enrichedBooking.vehicleName} - ${enrichedBooking.vehiclePlateNumber}",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(
                        status = booking.status,
                        onClick = { showStatusDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.CalendarToday,
                    text = booking.getFormattedDate(),
                    color = Primary
                )
                InfoItem(
                    icon = Icons.Default.AccessTime,
                    text = booking.timeSlot,
                    color = Secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price and points
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Biaya",
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "Rp ${String.format("%,d", booking.totalPrice)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                if (booking.pointsEarned > 0) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Success.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "+${booking.pointsEarned} poin",
                            fontSize = 10.sp,
                            color = Success,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Notes if available
            if (booking.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Info.copy(alpha = 0.05f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = "Notes",
                            tint = Info,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = booking.notes,
                            fontSize = 11.sp,
                            color = OnSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Status Update Dialog
    if (showStatusDialog) {
        StatusUpdateDialog(
            currentStatus = booking.status,
            onDismiss = { showStatusDialog = false },
            onStatusUpdate = { newStatus ->
                onStatusUpdate(booking.bookingId, newStatus)
                showStatusDialog = false
            }
        )
    }
}

@Composable
fun StatusChip(
    status: BookingStatus,
    onClick: (() -> Unit)? = null
) {
    val (backgroundColor, textColor, text) = when (status) {
        BookingStatus.COMPLETED -> Triple(
            Success.copy(alpha = 0.1f),
            Success,
            "Selesai"
        )
        BookingStatus.CONFIRMED -> Triple(
            Primary.copy(alpha = 0.1f),
            Primary,
            "Dikonfirmasi"
        )
        BookingStatus.PENDING -> Triple(
            Warning.copy(alpha = 0.1f),
            Warning,
            "Menunggu"
        )
        BookingStatus.IN_PROGRESS -> Triple(
            Info.copy(alpha = 0.1f),
            Info,
            "Dikerjakan"
        )
        BookingStatus.CANCELLED -> Triple(
            Error.copy(alpha = 0.1f),
            Error,
            "Dibatalkan"
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusUpdateDialog(
    currentStatus: BookingStatus,
    onDismiss: () -> Unit,
    onStatusUpdate: (BookingStatus) -> Unit
) {
    val availableStatuses = listOf(
        BookingStatus.PENDING,
        BookingStatus.CONFIRMED,
        BookingStatus.IN_PROGRESS,
        BookingStatus.COMPLETED,
        BookingStatus.CANCELLED
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Status Booking",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Pilih status baru untuk booking ini:",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                availableStatuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusUpdate(status) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentStatus == status,
                            onClick = { onStatusUpdate(status) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusChip(status = status)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Primary)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun InfoItem(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun EmptyHistoryCard(selectedFilter: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📋",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tidak ada riwayat",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = when (selectedFilter) {
                    "Selesai" -> "Belum ada service yang selesai"
                    "Dikonfirmasi" -> "Belum ada booking yang dikonfirmasi"
                    "Menunggu" -> "Tidak ada booking yang menunggu"
                    "Dikerjakan" -> "Tidak ada booking yang sedang dikerjakan"
                    else -> "Belum ada service yang dilakukan"
                },
                fontSize = 12.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Helper functions
fun getServiceIcon(serviceName: String): ImageVector {
    return when (serviceName.lowercase()) {
        "ganti oli" -> Icons.Default.Opacity
        "service rutin" -> Icons.Default.Build
        "tune up" -> Icons.Default.Settings
        "ganti ban" -> Icons.Default.TripOrigin
        "cuci motor", "cuci mobil" -> Icons.Default.LocalCarWash
        else -> Icons.Default.Build
    }
}

fun getServiceIconColor(serviceName: String): Color {
    return when (serviceName.lowercase()) {
        "ganti oli" -> Primary
        "service rutin" -> Success
        "tune up" -> Warning
        "ganti ban" -> Info
        "cuci motor", "cuci mobil" -> Secondary
        else -> OnSurfaceVariant
    }
}

fun getServiceIconBackgroundColor(serviceName: String): Color {
    return getServiceIconColor(serviceName).copy(alpha = 0.1f)
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    BengkelkuTheme {
        HistoryScreen(
            onNavigateBack = {}
        )
    }
}