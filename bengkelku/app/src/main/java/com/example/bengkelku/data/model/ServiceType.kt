package com.example.bengkelku.data.model

data class ServiceType(
    val serviceId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Int = 0,
    val pointsReward: Int = 0,
    val estimatedTime: String = "",
    val isActive: Boolean = true,
    val vehicleType: String = "" // "motor", "mobil", "both"
) {
    // Constructor untuk Firebase
    constructor() : this("", "", "", 0, 0, "", true, "")

    // Helper function untuk format harga
    fun getFormattedPrice(): String {
        return "Rp ${String.format(java.util.Locale.getDefault(), "%,d", price)}"
    }
}