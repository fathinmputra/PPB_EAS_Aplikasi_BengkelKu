package com.example.bengkelku.data.repository

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.bengkelku.data.model.*
import com.example.bengkelku.data.local.PreferenceManager
import kotlinx.coroutines.delay
import kotlin.random.Random

class BookingRepository private constructor(
    private val prefsManager: PreferenceManager,
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository
) {

    companion object {
        @Volatile
        private var INSTANCE: BookingRepository? = null

        fun getInstance(
            prefsManager: PreferenceManager,
            vehicleRepository: VehicleRepository,
            userRepository: UserRepository
        ): BookingRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BookingRepository(prefsManager, vehicleRepository, userRepository)
                    .also { INSTANCE = it }
            }
        }
    }

    private val _bookings = mutableStateOf<List<Booking>>(emptyList())
    val bookings: State<List<Booking>> = _bookings

    private val _serviceTypes = mutableStateOf<List<ServiceType>>(emptyList())
    val serviceTypes: State<List<ServiceType>> = _serviceTypes

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadDataFromStorage()
    }

    // ========== PRIVATE METHODS ==========
    private fun loadDataFromStorage() {
        _bookings.value = prefsManager.getBookings()
        _serviceTypes.value = prefsManager.getServiceTypes()
    }

    private fun saveBookingsToStorage() {
        prefsManager.saveBookings(_bookings.value)
    }

    private fun saveServiceTypesToStorage() {
        prefsManager.saveServiceTypes(_serviceTypes.value)
    }

    private suspend fun simulateNetworkDelay() {
        delay(Random.nextLong(1000, 2500))
    }

    private fun generateBookingId(): String {
        val timestamp = System.currentTimeMillis()
        val randomNum = Random.nextInt(100, 999)
        return "BK${timestamp.toString().takeLast(6)}$randomNum"
    }

    private fun getCurrentUserId(): String {
        return userRepository.getCurrentUserId() ?: "user_fathin_001"
    }

    // ========== BOOKING OPERATIONS ==========
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            // Simulate occasional network error (5% chance)
            if (Random.nextFloat() <= 0.05f) {
                _isLoading.value = false
                return Result.failure(Exception("Network error. Please try again."))
            }

            val newBooking = booking.copy(
                bookingId = generateBookingId(),
                userId = getCurrentUserId(),
                status = BookingStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                completedAt = 0L
            )

            val updatedList = _bookings.value.toMutableList()
            updatedList.add(0, newBooking) // Add to beginning for latest first
            _bookings.value = updatedList
            saveBookingsToStorage()

            _isLoading.value = false
            Result.success(newBooking.bookingId)
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            val currentList = _bookings.value.toMutableList()
            val index = currentList.indexOfFirst { it.bookingId == bookingId }

            if (index != -1) {
                val booking = currentList[index]
                val serviceType = getServiceTypeById(booking.serviceTypeId)

                val updatedBooking = booking.copy(
                    status = status,
                    completedAt = if (status == BookingStatus.COMPLETED)
                        System.currentTimeMillis() else 0L,
                    pointsEarned = if (status == BookingStatus.COMPLETED)
                        serviceType?.pointsReward ?: 0 else 0
                )

                currentList[index] = updatedBooking
                _bookings.value = currentList
                saveBookingsToStorage()

                // Update user points if completed
                if (status == BookingStatus.COMPLETED && serviceType != null) {
                    userRepository.addPoints(serviceType.pointsReward)

                    // Update vehicle last service date
                    val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd",
                        java.util.Locale.getDefault()).format(java.util.Date())
                    vehicleRepository.updateLastServiceDate(booking.vehicleId, currentDate)
                }

                _isLoading.value = false
                Result.success(Unit)
            } else {
                _isLoading.value = false
                Result.failure(Exception("Booking not found"))
            }
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        return updateBookingStatus(bookingId, BookingStatus.CANCELLED)
    }

    // ========== GETTER METHODS ==========
    fun getUserBookings(userId: String? = null): List<Booking> {
        val targetUserId = userId ?: getCurrentUserId()
        return _bookings.value.filter { it.userId == targetUserId }
            .sortedByDescending { it.createdAt }
    }

    fun getBookingById(bookingId: String): Booking? {
        return _bookings.value.find { it.bookingId == bookingId }
    }

    fun getServiceTypeById(serviceId: String): ServiceType? {
        return _serviceTypes.value.find { it.serviceId == serviceId }
    }

    fun getBookingsByStatus(status: BookingStatus, userId: String? = null): List<Booking> {
        val targetUserId = userId ?: getCurrentUserId()
        return _bookings.value.filter {
            it.userId == targetUserId && it.status == status
        }.sortedByDescending { it.createdAt }
    }

    fun getActiveBookings(userId: String? = null): List<Booking> {
        val targetUserId = userId ?: getCurrentUserId()
        val activeStatuses = listOf(
            BookingStatus.PENDING,
            BookingStatus.CONFIRMED,
            BookingStatus.IN_PROGRESS
        )
        return _bookings.value.filter {
            it.userId == targetUserId && it.status in activeStatuses
        }.sortedByDescending { it.createdAt }
    }

    fun getCompletedBookings(userId: String? = null): List<Booking> {
        return getBookingsByStatus(BookingStatus.COMPLETED, userId)
    }

    // ========== SERVICE TYPES ==========
    fun getAvailableServiceTypes(): List<ServiceType> {
        return _serviceTypes.value.filter { it.isActive }
    }

    fun getServiceTypesByVehicleType(vehicleType: String): List<ServiceType> {
        return _serviceTypes.value.filter { serviceType ->
            serviceType.isActive && (
                    serviceType.vehicleType.equals("both", ignoreCase = true) ||
                            serviceType.vehicleType.equals(vehicleType, ignoreCase = true)
                    )
        }
    }

    // ========== ENRICHED DATA ==========
    fun getEnrichedBookings(userId: String? = null): List<EnrichedBooking> {
        val targetUserId = userId ?: getCurrentUserId()
        return getUserBookings(targetUserId).map { booking ->
            val vehicle = vehicleRepository.getVehicleById(booking.vehicleId)
            val serviceType = getServiceTypeById(booking.serviceTypeId)

            EnrichedBooking(
                booking = booking,
                vehicleName = vehicle?.let { "${it.brand} ${it.model}" }
                    ?: "Vehicle not found",
                vehiclePlateNumber = vehicle?.plateNumber ?: "",
                serviceName = serviceType?.name ?: "Service not found",
                serviceDescription = serviceType?.description ?: ""
            )
        }
    }

    // ========== STATISTICS ==========
    fun getBookingStats(userId: String? = null): BookingStats {
        val userBookings = getUserBookings(userId)
        val completed = userBookings.filter { it.status == BookingStatus.COMPLETED }

        return BookingStats(
            totalBookings = userBookings.size,
            completedBookings = completed.size,
            totalSpent = completed.sumOf { it.totalPrice },
            totalPointsEarned = completed.sumOf { it.pointsEarned },
            pendingBookings = userBookings.count { it.status == BookingStatus.PENDING },
            confirmedBookings = userBookings.count { it.status == BookingStatus.CONFIRMED }
        )
    }

    // ========== SEARCH & FILTER ==========
    fun searchBookings(query: String, userId: String? = null): List<Booking> {
        if (query.isBlank()) return getUserBookings(userId)

        val searchQuery = query.lowercase()
        return getUserBookings(userId).filter { booking ->
            val vehicle = vehicleRepository.getVehicleById(booking.vehicleId)
            val serviceType = getServiceTypeById(booking.serviceTypeId)

            booking.notes.lowercase().contains(searchQuery) ||
                    booking.bookingId.lowercase().contains(searchQuery) ||
                    vehicle?.brand?.lowercase()?.contains(searchQuery) == true ||
                    vehicle?.model?.lowercase()?.contains(searchQuery) == true ||
                    serviceType?.name?.lowercase()?.contains(searchQuery) == true
        }
    }

    fun refreshData() {
        loadDataFromStorage()
    }
}

// ========== DATA CLASSES ==========
data class EnrichedBooking(
    val booking: Booking,
    val vehicleName: String,
    val vehiclePlateNumber: String,
    val serviceName: String,
    val serviceDescription: String
)

data class BookingStats(
    val totalBookings: Int,
    val completedBookings: Int,
    val totalSpent: Int,
    val totalPointsEarned: Int,
    val pendingBookings: Int,
    val confirmedBookings: Int
)