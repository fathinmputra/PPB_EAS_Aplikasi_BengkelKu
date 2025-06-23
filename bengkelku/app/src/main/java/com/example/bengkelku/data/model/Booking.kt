package com.example.bengkelku.data.model

data class Booking(
    val bookingId: String = "",
    val userId: String = "",
    val vehicleId: String = "",
    val serviceTypeId: String = "",
    val bookingDate: String = "", // Format: "yyyy-MM-dd"
    val timeSlot: String = "", // Format: "09:00-10:00"
    val status: BookingStatus = BookingStatus.PENDING,
    val notes: String = "",
    val totalPrice: Int = 0,
    val pointsEarned: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0L
) {
    // Constructor untuk Firebase
    constructor() : this("", "", "", "", "", "", BookingStatus.PENDING, "", 0, 0, 0L, 0L)

    // Helper function untuk format tanggal
    fun getFormattedDate(): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date = sdf.parse(bookingDate)
            val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            if (date != null) {
                formatter.format(date)
            } else {
                bookingDate
            }
        } catch (e: Exception) {
            bookingDate
        }
    }

    // Helper function untuk status color
    fun getStatusColor(): androidx.compose.ui.graphics.Color {
        return when (status) {
            BookingStatus.PENDING -> androidx.compose.ui.graphics.Color(0xFFFF9800)
            BookingStatus.CONFIRMED -> androidx.compose.ui.graphics.Color(0xFF2196F3)
            BookingStatus.IN_PROGRESS -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
            BookingStatus.COMPLETED -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
            BookingStatus.CANCELLED -> androidx.compose.ui.graphics.Color(0xFFF44336)
        }
    }
}

enum class BookingStatus(val displayName: String) {
    PENDING("Menunggu Konfirmasi"),
    CONFIRMED("Dikonfirmasi"),
    IN_PROGRESS("Sedang Dikerjakan"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan")
}