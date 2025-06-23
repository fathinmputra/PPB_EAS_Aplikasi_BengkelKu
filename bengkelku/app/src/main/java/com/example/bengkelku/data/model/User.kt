package com.example.bengkelku.data.model

data class User(
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val totalPoints: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) {
    // Constructor untuk Firebase
    constructor() : this("", "", "", "", 0, 0L, true)
}