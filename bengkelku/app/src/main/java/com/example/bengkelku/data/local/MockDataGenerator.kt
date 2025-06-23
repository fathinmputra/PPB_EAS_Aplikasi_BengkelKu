package com.example.bengkelku.data.local

import com.example.bengkelku.data.model.*
import java.util.concurrent.TimeUnit

object MockDataGenerator {

    // ========== USER DATA ==========
    fun generateUser(): User = User(
        userId = "user_fathin_001",
        name = "Fathin Muhashibi Putra",
        phone = "08123456789",
        email = "fathin@email.com",
        totalPoints = 275, // Points from completed services
        createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365), // 1 year ago
        isActive = true
    )

    // ========== VEHICLE DATA ==========
    fun generateVehicles(): List<Vehicle> = listOf(
        Vehicle(
            vehicleId = "vehicle_001",
            userId = "user_fathin_001",
            brand = "Honda",
            model = "Beat",
            plateNumber = "B 1234 XYZ",
            year = 2020,
            type = "Motor",
            lastServiceDate = "2024-12-15", // Recent service
            createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(200)
        ),
        Vehicle(
            vehicleId = "vehicle_002",
            userId = "user_fathin_001",
            brand = "Yamaha",
            model = "Mio",
            plateNumber = "B 5678 ABC",
            year = 2019,
            type = "Motor",
            lastServiceDate = "2024-11-20", // Older service
            createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(300)
        )
    )

    // ========== BOOKING HISTORY ==========
    fun generateBookings(): List<Booking> = listOf(
        // Recent completed booking - Ganti Oli
        Booking(
            bookingId = "BK001",
            userId = "user_fathin_001",
            vehicleId = "vehicle_001", // Honda Beat
            serviceTypeId = "1", // Ganti Oli
            bookingDate = "2024-12-15",
            timeSlot = "09:00-10:00",
            status = BookingStatus.COMPLETED,
            notes = "Oli sudah mulai kehitaman, ganti dengan oli synthetic",
            totalPrice = 50000,
            pointsEarned = 50,
            createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(8), // 8 days ago
            completedAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) // 7 days ago
        ),

        // Second completed - Service Rutin
        Booking(
            bookingId = "BK002",
            userId = "user_fathin_001",
            vehicleId = "vehicle_002", // Yamaha Mio
            serviceTypeId = "2", // Service Rutin
            bookingDate = "2024-11-20",
            timeSlot = "14:00-15:00",
            status = BookingStatus.COMPLETED,
            notes = "Service rutin bulanan, kondisi mesin baik",
            totalPrice = 75000,
            pointsEarned = 75,
            createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(33), // 33 days ago
            completedAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(32)
        ),

        // Third completed - Tune Up
        Booking(
            bookingId = "BK003",
            userId = "user_fathin_001",
            vehicleId = "vehicle_001", // Honda Beat
            serviceTypeId = "3", // Tune Up
            bookingDate = "2024-10-25",
            timeSlot = "10:00-11:30",
            status = BookingStatus.COMPLETED,
            notes = "Busi baru, karburator dibersihkan, performa meningkat",
            totalPrice = 150000,
            pointsEarned = 150,
            createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(59), // 59 days ago
            completedAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(58)
        ),

        // Fourth completed - Cuci Motor
        Booking(
            bookingId = "BK004",
            userId = "user_fathin_001",
            vehicleId = "vehicle_002", // Yamaha Mio
            serviceTypeId = "5", // Cuci Motor
            bookingDate = "2024-10-10",
            timeSlot = "15:00-16:00",
            status = BookingStatus.COMPLETED,
            notes = "Cuci dan poles untuk acara keluarga",
            totalPrice = 25000,
            pointsEarned = 0, // No points for basic wash
            createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(74),
            completedAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(74)
        ),

        // One pending booking
        Booking(
            bookingId = "BK005",
            userId = "user_fathin_001",
            vehicleId = "vehicle_001", // Honda Beat
            serviceTypeId = "2", // Service Rutin
            bookingDate = "2024-12-28", // Future date
            timeSlot = "09:00-10:00",
            status = BookingStatus.PENDING,
            notes = "Service rutin akhir tahun",
            totalPrice = 75000,
            pointsEarned = 0, // Will earn when completed
            createdAt = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2), // 2 hours ago
            completedAt = 0L
        ),

        // One confirmed booking
        Booking(
            bookingId = "BK006",
            userId = "user_fathin_001",
            vehicleId = "vehicle_002", // Yamaha Mio
            serviceTypeId = "1", // Ganti Oli
            bookingDate = "2024-12-30", // Future date
            timeSlot = "11:00-12:00",
            status = BookingStatus.CONFIRMED,
            notes = "Persiapan tahun baru",
            totalPrice = 50000,
            pointsEarned = 0, // Will earn when completed
            createdAt = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), // Yesterday
            completedAt = 0L
        )
    )

    // ========== SERVICE TYPES ==========
    fun generateServiceTypes(): List<ServiceType> = listOf(
        ServiceType(
            serviceId = "1",
            name = "Ganti Oli",
            description = "Ganti oli mesin dan filter oli berkualitas",
            price = 50000,
            pointsReward = 50,
            estimatedTime = "30 menit",
            vehicleType = "both",
            isActive = true
        ),
        ServiceType(
            serviceId = "2",
            name = "Service Rutin",
            description = "Pemeriksaan rutin kondisi kendaraan menyeluruh",
            price = 75000,
            pointsReward = 75,
            estimatedTime = "45 menit",
            vehicleType = "both",
            isActive = true
        ),
        ServiceType(
            serviceId = "3",
            name = "Tune Up",
            description = "Tune up lengkap mesin untuk performa optimal",
            price = 150000,
            pointsReward = 150,
            estimatedTime = "90 menit",
            vehicleType = "both",
            isActive = true
        ),
        ServiceType(
            serviceId = "4",
            name = "Ganti Ban",
            description = "Ganti ban kendaraan dengan ban berkualitas",
            price = 200000,
            pointsReward = 200,
            estimatedTime = "60 menit",
            vehicleType = "both",
            isActive = true
        ),
        ServiceType(
            serviceId = "5",
            name = "Cuci Motor",
            description = "Cuci dan poles kendaraan hingga mengkilap",
            price = 25000,
            pointsReward = 25,
            estimatedTime = "20 menit",
            vehicleType = "motor",
            isActive = true
        ),
        ServiceType(
            serviceId = "6",
            name = "Cuci Mobil",
            description = "Cuci dan poles mobil lengkap dengan vacuum",
            price = 50000,
            pointsReward = 50,
            estimatedTime = "40 menit",
            vehicleType = "mobil",
            isActive = true
        )
    )

    // ========== HELPER DATA ==========
    fun getRealisticNotes(): List<String> = listOf(
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

    fun getAvailableTimeSlots(): List<String> = listOf(
        "08:00-09:00",
        "09:00-10:00",
        "10:00-11:00",
        "11:00-12:00",
        "13:00-14:00",
        "14:00-15:00",
        "15:00-16:00",
        "16:00-17:00"
    )

    // ========== INITIALIZE ALL DATA ==========
    fun initializeAllDemoData(prefsManager: PreferenceManager) {
        if (prefsManager.isFirstLaunch()) {
            // Save all initial data
            prefsManager.saveCurrentUser(generateUser())
            prefsManager.saveVehicles(generateVehicles())
            prefsManager.saveBookings(generateBookings())
            prefsManager.saveServiceTypes(generateServiceTypes())

            // Calculate and save total points from completed bookings
            val totalPoints = generateBookings()
                .filter { it.status == BookingStatus.COMPLETED }
                .sumOf { it.pointsEarned }
            prefsManager.updateUserPoints(totalPoints)

            // Mark as initialized
            prefsManager.initializeDemoData()
        }
    }

    fun ensureDemoDataAvailable(prefsManager: PreferenceManager) {
        // Always ensure default user exists
        if (prefsManager.getCurrentUser() == null) {
            prefsManager.saveCurrentUser(generateUser())
        }

        // Always ensure service types exist
        if (prefsManager.getServiceTypes().isEmpty()) {
            prefsManager.saveServiceTypes(generateServiceTypes())
        }

        // Only initialize vehicles and bookings if first launch
        if (prefsManager.isFirstLaunch()) {
            prefsManager.saveVehicles(generateVehicles())
            prefsManager.saveBookings(generateBookings())

            // Calculate and save total points from completed bookings
            val totalPoints = generateBookings()
                .filter { it.status == BookingStatus.COMPLETED }
                .sumOf { it.pointsEarned }
            prefsManager.updateUserPoints(totalPoints)

            // Mark as initialized
            prefsManager.initializeDemoData()
        }
    }
}