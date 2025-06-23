package com.example.bengkelku.data.firebase

/*
 * DEMO MODE: Firebase implementation commented out
 * Using local storage instead for demo purposes
 */

class FirebaseService {

    // ========== DEMO PLACEHOLDER METHODS ==========

    fun getCurrentUserId(): String? = "demo_fathin_001"

    fun isUserLoggedIn(): Boolean = false // Always false for demo

    suspend fun initializeServiceTypes(): Result<Unit> = Result.success(Unit)

    fun signOut() {
        // Demo mode - no actual sign out needed
    }
}

/*
// ORIGINAL FIREBASE IMPLEMENTATION - COMMENTED FOR DEMO
// Uncomment this section if you want to use real Firebase

package com.example.bengkelku.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.bengkelku.data.model.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Collections
    private val usersCollection = firestore.collection("users")
    private val vehiclesCollection = firestore.collection("vehicles")
    private val bookingsCollection = firestore.collection("bookings")
    private val serviceTypesCollection = firestore.collection("serviceTypes")

    // ========== AUTHENTICATION ==========

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<String> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // ========== USER OPERATIONS ==========

    suspend fun createUser(user: User): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            val userWithId = user.copy(userId = userId)
            usersCollection.document(userId).set(userWithId).await()
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPoints(userId: String, points: Int): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("totalPoints", points).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== VEHICLE OPERATIONS ==========

    suspend fun createVehicle(vehicle: Vehicle): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            val vehicleWithUserId = vehicle.copy(userId = userId)
            val docRef = vehiclesCollection.add(vehicleWithUserId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserVehicles(userId: String): Result<List<Vehicle>> {
        return try {
            val querySnapshot = vehiclesCollection
                .whereEqualTo("userId", userId)
                .get().await()

            val vehicles = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Vehicle::class.java)?.copy(vehicleId = doc.id)
            }
            Result.success(vehicles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== BOOKING OPERATIONS ==========

    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            val bookingWithUserId = booking.copy(userId = userId)
            val docRef = bookingsCollection.add(bookingWithUserId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserBookings(userId: String): Result<List<Booking>> {
        return try {
            val querySnapshot = bookingsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()

            val bookings = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Booking::class.java)?.copy(bookingId = doc.id)
            }
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        return try {
            bookingsCollection.document(bookingId)
                .update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== SERVICE TYPE OPERATIONS ==========

    suspend fun getServiceTypes(): Result<List<ServiceType>> {
        return try {
            val querySnapshot = serviceTypesCollection
                .whereEqualTo("isActive", true)
                .get().await()

            val serviceTypes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(ServiceType::class.java)?.copy(serviceId = doc.id)
            }
            Result.success(serviceTypes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== HELPER FUNCTIONS ==========

    suspend fun initializeServiceTypes(): Result<Unit> {
        return try {
            // Check if service types already exist
            val existing = serviceTypesCollection.limit(1).get().await()
            if (!existing.isEmpty) {
                return Result.success(Unit)
            }

            // Create default service types
            val defaultServiceTypes = listOf(
                ServiceType(
                    name = "Ganti Oli",
                    description = "Ganti oli mesin dan filter oli",
                    price = 50000,
                    pointsReward = 50,
                    estimatedTime = "30 menit",
                    vehicleType = "both"
                ),
                ServiceType(
                    name = "Service Rutin",
                    description = "Pemeriksaan rutin kendaraan",
                    price = 75000,
                    pointsReward = 75,
                    estimatedTime = "45 menit",
                    vehicleType = "both"
                ),
                ServiceType(
                    name = "Tune Up",
                    description = "Tune up lengkap mesin",
                    price = 150000,
                    pointsReward = 150,
                    estimatedTime = "90 menit",
                    vehicleType = "both"
                ),
                ServiceType(
                    name = "Ganti Ban",
                    description = "Ganti ban kendaraan",
                    price = 200000,
                    pointsReward = 200,
                    estimatedTime = "60 menit",
                    vehicleType = "both"
                )
            )

            defaultServiceTypes.forEach { serviceType ->
                serviceTypesCollection.add(serviceType).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
*/