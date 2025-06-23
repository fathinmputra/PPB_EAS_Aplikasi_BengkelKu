package com.example.bengkelku.utils

object Constants {

    // ========== USER DATA ==========
    const val USER_ID = "user_fathin_001"
    const val USER_NAME = "Fathin Muhashibi Putra"
    const val USER_PHONE = "08123456789"
    const val USER_EMAIL = "fathin@email.com"

    // ========== OTP ==========
    const val VALID_OTP_CODE = "123456"

    // ========== TIME SLOTS ==========
    val TIME_SLOTS = listOf(
        "08:00-09:00",
        "09:00-10:00",
        "10:00-11:00",
        "11:00-12:00",
        "13:00-14:00",
        "14:00-15:00",
        "15:00-16:00",
        "16:00-17:00"
    )

    // ========== REALISTIC BOOKING NOTES ==========
    val REALISTIC_NOTES = listOf(
        "Oli sudah mulai kehitaman",
        "Bunyi mesin agak kasar",
        "Service rutin 3 bulan",
        "Persiapan mudik lebaran",
        "Ada bunyi aneh di bagian mesin",
        "Rem agak blong, perlu dicek",
        "Rantai kendor, perlu disetel",
        "Lampu depan redup",
        "Klakson tidak bunyi",
        "Service berkala",
        "Persiapan traveling jauh",
        "Check up menyeluruh"
    )

    // ========== VEHICLE BRANDS ==========
    val MOTOR_BRANDS = listOf(
        "Honda", "Yamaha", "Suzuki", "Kawasaki", "TVS"
    )

    val MOBIL_BRANDS = listOf(
        "Toyota", "Honda", "Daihatsu", "Suzuki", "Mitsubishi", "Nissan"
    )

    // ========== APP SETTINGS ==========
    const val SUCCESS_RATE = 0.95f // 95% success rate
    const val MIN_LOADING_TIME = 800L // Minimum loading time in ms
    const val MAX_LOADING_TIME = 2000L // Maximum loading time in ms

    // ========== POINTS SYSTEM ==========
    const val POINTS_PER_RUPIAH = 0.001 // 1 point per 1000 rupiah
    const val MIN_POINTS_REDEEM = 100

    // ========== DATE FORMATS ==========
    const val DATE_FORMAT_INPUT = "yyyy-MM-dd"
    const val DATE_FORMAT_DISPLAY = "dd MMMM yyyy"
    const val DATE_FORMAT_SHORT = "dd MMM"

    // ========== VALIDATION ==========
    const val MIN_PHONE_LENGTH = 10
    const val MAX_PHONE_LENGTH = 15
    const val MIN_PASSWORD_LENGTH = 8
    const val OTP_LENGTH = 6

    // ========== APP INFO ==========
    const val APP_VERSION = "1.0.0"
    const val APP_COPYRIGHT = "© 2024 BengkelKu"

    // ========== ERROR MESSAGES ==========
    object ErrorMessages {
        const val NETWORK_ERROR = "Koneksi internet bermasalah"
        const val GENERAL_ERROR = "Terjadi kesalahan, coba lagi"
        const val VALIDATION_ERROR = "Data yang dimasukkan tidak valid"
        const val NO_DATA_ERROR = "Tidak ada data tersedia"
    }

    // ========== SUCCESS MESSAGES ==========
    object SuccessMessages {
        const val BOOKING_SUCCESS = "Booking berhasil dibuat!"
        const val VEHICLE_ADDED = "Kendaraan berhasil ditambahkan!"
        const val VEHICLE_UPDATED = "Kendaraan berhasil diperbarui!"
        const val VEHICLE_DELETED = "Kendaraan berhasil dihapus!"
        const val PROFILE_UPDATED = "Profile berhasil diperbarui!"
    }
}