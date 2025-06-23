package com.example.bengkelku.data.model

data class Vehicle(
    val vehicleId: String = "",
    val userId: String = "",
    val brand: String = "",
    val model: String = "",
    val plateNumber: String = "",
    val year: Int = 0,
    val type: String = "", // "motor" atau "mobil"
    val lastServiceDate: String = "", // Format: "yyyy-MM-dd"
    val createdAt: Long = System.currentTimeMillis()
) {
    // Constructor untuk Firebase
    constructor() : this("", "", "", "", "", 0, "", "", 0L)

    // Helper function untuk cek apakah perlu service
    fun needsService(): Boolean {
        if (lastServiceDate.isEmpty()) return false

        try {
            // Pakai SimpleDateFormat yang support API 24+
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val lastService = sdf.parse(lastServiceDate)
            val now = java.util.Date()

            if (lastService != null) {
                val diffInMillis = now.time - lastService.time
                val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
                return diffInDays >= 90 // 3 bulan = 90 hari
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    // Helper function untuk display name
    fun getDisplayName(): String {
        return "$brand $model"
    }
}