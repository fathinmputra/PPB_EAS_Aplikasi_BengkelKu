package com.example.bengkelku.data.repository

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.bengkelku.data.model.User
import com.example.bengkelku.data.local.PreferenceManager
import com.example.bengkelku.data.local.MockDataGenerator
import kotlinx.coroutines.delay
import kotlin.random.Random

class UserRepository private constructor(private val prefsManager: PreferenceManager) {

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(prefsManager: PreferenceManager): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(prefsManager).also { INSTANCE = it }
            }
        }
    }

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Store for registered users (demo purpose)
    private val registeredUsers = mutableMapOf<String, User>()

    init {
        loadUserFromStorage()
        initializeDefaultUser()
    }

    // ========== PRIVATE METHODS ==========
    private fun loadUserFromStorage() {
        _currentUser.value = prefsManager.getCurrentUser()
    }

    private fun saveUserToStorage() {
        _currentUser.value?.let { user ->
            prefsManager.saveCurrentUser(user)
        }
    }

    private fun initializeDefaultUser() {
        // Always ensure default user exists
        val defaultUser = MockDataGenerator.generateUser()
        registeredUsers[defaultUser.phone] = defaultUser

        // If no current user, set default as current
        if (_currentUser.value == null) {
            _currentUser.value = defaultUser
            saveUserToStorage()
        }
    }

    private suspend fun simulateNetworkDelay() {
        delay(Random.nextLong(800, 2000))
    }

    // ========== AUTH SIMULATION ==========
    suspend fun login(phone: String, password: String): Result<User> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            // Check if user exists in registered users or is default user
            val user = when {
                // Default user login
                phone == "08123456789" && password == "password123" -> {
                    val defaultUser = MockDataGenerator.generateUser()
                    registeredUsers[phone] = defaultUser
                    defaultUser
                }
                // Registered user login
                registeredUsers.containsKey(phone) -> {
                    registeredUsers[phone]
                }
                else -> null
            }

            if (user != null) {
                _currentUser.value = user
                saveUserToStorage()
                _isLoading.value = false
                Result.success(user)
            } else {
                _isLoading.value = false
                Result.failure(Exception("Nomor HP atau password salah"))
            }
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    suspend fun register(name: String, phone: String, email: String, password: String): Result<User> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            // Check if phone already registered
            if (registeredUsers.containsKey(phone)) {
                _isLoading.value = false
                return Result.failure(Exception("Nomor HP sudah terdaftar"))
            }

            // Create new user
            val newUser = User(
                userId = "user_${System.currentTimeMillis()}",
                name = name,
                phone = phone,
                email = email,
                totalPoints = 0,
                createdAt = System.currentTimeMillis(),
                isActive = true
            )

            // Store in registered users
            registeredUsers[phone] = newUser

            _isLoading.value = false
            Result.success(newUser)
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    suspend fun verifyOTP(phone: String, otp: String): Result<User> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            // ✅ Accept any 6-digit OTP for demo
            if (otp.length == 6 && otp.all { it.isDigit() }) {

                // ✅ ENSURE: Default user always exists for demo
                var user = prefsManager.getCurrentUser()

                if (user == null) {
                    val defaultUser = com.example.bengkelku.data.local.MockDataGenerator.generateUser()
                    prefsManager.saveCurrentUser(defaultUser)
                    user = defaultUser

                    com.example.bengkelku.data.local.MockDataGenerator.ensureDemoDataAvailable(prefsManager)
                }

                _currentUser.value = user
                _isLoading.value = false

                Result.success(user)

            } else {
                _isLoading.value = false
                Result.failure(Exception("Invalid OTP code"))
            }

        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(Exception("Verifikasi gagal. Silakan coba lagi."))
        }
    }

    // ========== PUBLIC METHODS ==========
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            _currentUser.value = user
            saveUserToStorage()

            // Update in registered users if exists
            registeredUsers[user.phone] = user

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        name: String,
        email: String,
        phone: String
    ): Result<Unit> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            val currentUser = _currentUser.value ?: throw Exception("User not found")
            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                phone = phone
            )

            _currentUser.value = updatedUser
            saveUserToStorage()

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    fun addPoints(points: Int) {
        val currentUser = _currentUser.value ?: return
        val updatedPoints = currentUser.totalPoints + points

        val updatedUser = currentUser.copy(totalPoints = updatedPoints)
        _currentUser.value = updatedUser
        saveUserToStorage()
        prefsManager.updateUserPoints(updatedPoints)

        // Update in registered users
        registeredUsers[currentUser.phone] = updatedUser
    }

    fun deductPoints(points: Int): Boolean {
        val currentUser = _currentUser.value ?: return false

        return if (currentUser.totalPoints >= points) {
            val updatedPoints = currentUser.totalPoints - points
            val updatedUser = currentUser.copy(totalPoints = updatedPoints)

            _currentUser.value = updatedUser
            saveUserToStorage()
            prefsManager.updateUserPoints(updatedPoints)

            // Update in registered users
            registeredUsers[currentUser.phone] = updatedUser
            true
        } else {
            false
        }
    }

    fun getCurrentPoints(): Int {
        return _currentUser.value?.totalPoints ?: 0
    }

    fun getCurrentUserId(): String? {
        return _currentUser.value?.userId
    }

    fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }

    suspend fun logout(): Result<Unit> {
        return try {
            _isLoading.value = true
            simulateNetworkDelay()

            // Clear current user but keep registered users and default data
            _currentUser.value = null

            // Clear current user from preferences but keep other data
            prefsManager.saveCurrentUser(null)

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }

    fun refreshUserData() {
        loadUserFromStorage()
    }

    // ========== DEMO HELPERS ==========
    fun getRegisteredUsers(): Map<String, User> {
        return registeredUsers.toMap()
    }

    fun resetToDefaultUser() {
        val defaultUser = MockDataGenerator.generateUser()
        _currentUser.value = defaultUser
        saveUserToStorage()
        registeredUsers[defaultUser.phone] = defaultUser
    }
}