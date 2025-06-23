package com.example.bengkelku.data.repository

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.bengkelku.data.model.Vehicle
import com.example.bengkelku.data.local.PreferenceManager
import kotlinx.coroutines.delay
import kotlin.random.Random

class VehicleRepository private constructor(private val prefsManager: PreferenceManager) {

    companion object {
        @Volatile
        private var INSTANCE: VehicleRepository? = null

        fun getInstance(prefsManager: PreferenceManager): VehicleRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VehicleRepository(prefsManager).also { INSTANCE = it }
            }
        }
    }

    private val _vehicles = mutableStateOf<List<Vehicle>>(emptyList())
    val vehicles: State<List<Vehicle>> = _vehicles

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadVehiclesFromStorage()
    }

    // ========== PRIVATE METHODS ==========
    private fun loadVehiclesFromStorage() {
        _vehicles.value = prefsManager.getVehicles()
    }

    private fun saveVehiclesToStorage() {
        prefsManager.saveVehicles(_vehicles.value)
    }

    private suspend fun simulateNetworkDelay() {
        delay(Random.nextLong(800, 1500))
    }

    private fun generateVehicleId(): String {
        return "vehicle_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
    }

    // ========== PUBLIC METHODS ==========
    suspend fun addVehicle(vehicle: Vehicle): Result<String> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            val newVehicle = vehicle.copy(
                vehicleId = generateVehicleId(),
                userId = getCurrentUserId(),
                createdAt = System.currentTimeMillis()
            )

            val updatedList = _vehicles.value.toMutableList()
            updatedList.add(newVehicle)
            _vehicles.value = updatedList
            saveVehiclesToStorage()

            _isLoading.value = false
            Result.success(newVehicle.vehicleId)
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    suspend fun updateVehicle(updatedVehicle: Vehicle): Result<Unit> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            val currentList = _vehicles.value.toMutableList()
            val index = currentList.indexOfFirst { it.vehicleId == updatedVehicle.vehicleId }

            if (index != -1) {
                currentList[index] = updatedVehicle
                _vehicles.value = currentList
                saveVehiclesToStorage()

                _isLoading.value = false
                Result.success(Unit)
            } else {
                _isLoading.value = false
                Result.failure(Exception("Vehicle not found"))
            }
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    suspend fun deleteVehicle(vehicleId: String): Result<Unit> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            val currentList = _vehicles.value.toMutableList()
            val removed = currentList.removeIf { it.vehicleId == vehicleId }

            if (removed) {
                _vehicles.value = currentList
                saveVehiclesToStorage()

                _isLoading.value = false
                Result.success(Unit)
            } else {
                _isLoading.value = false
                Result.failure(Exception("Vehicle not found"))
            }
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    fun getVehicleById(vehicleId: String): Vehicle? {
        return _vehicles.value.find { it.vehicleId == vehicleId }
    }

    fun getUserVehicles(userId: String? = null): List<Vehicle> {
        val targetUserId = userId ?: getCurrentUserId()
        return _vehicles.value.filter { it.userId == targetUserId }
    }

    fun getVehiclesByType(type: String): List<Vehicle> {
        return _vehicles.value.filter { it.type.equals(type, ignoreCase = true) }
    }

    fun getVehiclesNeedingService(): List<Vehicle> {
        return _vehicles.value.filter { it.needsService() }
    }

    suspend fun updateLastServiceDate(vehicleId: String, serviceDate: String): Result<Unit> {
        return try {
            val vehicle = getVehicleById(vehicleId)
                ?: return Result.failure(Exception("Vehicle not found"))

            val updatedVehicle = vehicle.copy(lastServiceDate = serviceDate)
            updateVehicle(updatedVehicle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCurrentUserId(): String {
        return "user_fathin_001" // Get from UserRepository in real implementation
    }

    fun refreshVehicles() {
        loadVehiclesFromStorage()
    }

    fun getVehicleCount(): Int {
        return _vehicles.value.size
    }

    fun hasVehicles(): Boolean {
        return _vehicles.value.isNotEmpty()
    }

    // ========== SEARCH & FILTER ==========
    fun searchVehicles(query: String): List<Vehicle> {
        if (query.isBlank()) return _vehicles.value

        val searchQuery = query.lowercase()
        return _vehicles.value.filter { vehicle ->
            vehicle.brand.lowercase().contains(searchQuery) ||
                    vehicle.model.lowercase().contains(searchQuery) ||
                    vehicle.plateNumber.lowercase().contains(searchQuery) ||
                    vehicle.type.lowercase().contains(searchQuery)
        }
    }

    fun getVehiclesByYear(year: Int): List<Vehicle> {
        return _vehicles.value.filter { it.year == year }
    }

    fun getVehiclesByBrand(brand: String): List<Vehicle> {
        return _vehicles.value.filter {
            it.brand.equals(brand, ignoreCase = true)
        }
    }
}