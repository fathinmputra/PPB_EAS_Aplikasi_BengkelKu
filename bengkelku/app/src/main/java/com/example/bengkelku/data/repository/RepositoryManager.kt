package com.example.bengkelku.data.repository

import android.content.Context
import com.example.bengkelku.data.local.PreferenceManager
import com.example.bengkelku.data.local.MockDataGenerator

class RepositoryManager private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: RepositoryManager? = null

        fun getInstance(context: Context): RepositoryManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RepositoryManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefsManager = PreferenceManager(context)

    // Repository instances
    val userRepository: UserRepository by lazy {
        UserRepository.getInstance(prefsManager)
    }

    val vehicleRepository: VehicleRepository by lazy {
        VehicleRepository.getInstance(prefsManager)
    }

    val bookingRepository: BookingRepository by lazy {
        BookingRepository.getInstance(prefsManager, vehicleRepository, userRepository)
    }

    // ========== HELPER METHODS ==========
    fun refreshAllData() {
        userRepository.refreshUserData()
        vehicleRepository.refreshVehicles()
        bookingRepository.refreshData()
    }

    fun isDataInitialized(): Boolean {
        return prefsManager.hasData()
    }

    suspend fun clearAllData(): Result<Unit> {
        return try {
            userRepository.logout()
            prefsManager.clearAllData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun ensureDemoDataAvailable() {
        MockDataGenerator.ensureDemoDataAvailable(prefsManager)
    }

    fun initializeDemoData() {
        MockDataGenerator.initializeAllDemoData(prefsManager)
    }
}