package com.example.bengkelku.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.bengkelku.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bengkelku_demo", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        // Keys for SharedPreferences
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_VEHICLES = "vehicles"
        private const val KEY_BOOKINGS = "bookings"
        private const val KEY_SERVICE_TYPES = "service_types"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_USER_POINTS = "user_points"
    }

    // ========== USER OPERATIONS ==========

    fun getCurrentUser(): User? {
        val userJson = prefs.getString(KEY_CURRENT_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun updateUserPoints(points: Int) {
        prefs.edit().putInt(KEY_USER_POINTS, points).apply()
        // Also update user object
        getCurrentUser()?.let { user ->
            val updatedUser = user.copy(totalPoints = points)
            saveCurrentUser(updatedUser)
        }
    }

    fun getUserPoints(): Int {
        return prefs.getInt(KEY_USER_POINTS, 275) // Default demo points
    }

    // ========== VEHICLE OPERATIONS ==========
    fun saveVehicles(vehicles: List<Vehicle>) {
        val vehiclesJson = gson.toJson(vehicles)
        prefs.edit().putString(KEY_VEHICLES, vehiclesJson).apply()
    }

    fun getVehicles(): List<Vehicle> {
        val vehiclesJson = prefs.getString(KEY_VEHICLES, null)
        return if (vehiclesJson != null) {
            try {
                val type = object : TypeToken<List<Vehicle>>() {}.type
                gson.fromJson(vehiclesJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()
    }

    // ========== BOOKING OPERATIONS ==========
    fun saveBookings(bookings: List<Booking>) {
        val bookingsJson = gson.toJson(bookings)
        prefs.edit().putString(KEY_BOOKINGS, bookingsJson).apply()
    }

    fun getBookings(): List<Booking> {
        val bookingsJson = prefs.getString(KEY_BOOKINGS, null)
        return if (bookingsJson != null) {
            try {
                val type = object : TypeToken<List<Booking>>() {}.type
                gson.fromJson(bookingsJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()
    }

    // ========== SERVICE TYPE OPERATIONS ==========
    fun saveServiceTypes(serviceTypes: List<ServiceType>) {
        val serviceTypesJson = gson.toJson(serviceTypes)
        prefs.edit().putString(KEY_SERVICE_TYPES, serviceTypesJson).apply()
    }

    fun getServiceTypes(): List<ServiceType> {
        val serviceTypesJson = prefs.getString(KEY_SERVICE_TYPES, null)
        return if (serviceTypesJson != null) {
            try {
                val type = object : TypeToken<List<ServiceType>>() {}.type
                gson.fromJson(serviceTypesJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()
    }

    // ========== APP STATE OPERATIONS ==========
    fun setFirstLaunch(isFirst: Boolean) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply()
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
    }

    // ========== HELPER FUNCTIONS ==========
    fun hasData(): Boolean {
        return getCurrentUser() != null && getVehicles().isNotEmpty()
    }

    fun initializeDemoData() {
        // This will be called from MockDataGenerator
        setFirstLaunch(false)
    }

    fun saveCurrentUser(user: User?) {
        if (user != null) {
            val userJson = gson.toJson(user)
            prefs.edit().putString(KEY_CURRENT_USER, userJson).apply()
        } else {
            // Clear current user but keep other data
            prefs.edit().remove(KEY_CURRENT_USER).apply()
        }
    }

    fun getPrefs(): SharedPreferences {
        return prefs
    }

    fun clearCurrentUser() {
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }
}