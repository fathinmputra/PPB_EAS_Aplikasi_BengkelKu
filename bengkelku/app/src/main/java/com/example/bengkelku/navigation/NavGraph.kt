package com.example.bengkelku.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bengkelku.ui.screens.SplashScreen
import com.example.bengkelku.ui.screens.auth.LoginScreen
import com.example.bengkelku.ui.screens.auth.RegisterScreen
import com.example.bengkelku.ui.screens.auth.OTPScreen
import com.example.bengkelku.ui.screens.main.DashboardScreen
import com.example.bengkelku.ui.screens.main.BookingScreen
import com.example.bengkelku.ui.screens.main.HistoryScreen
import com.example.bengkelku.ui.screens.main.ProfileScreen
import com.example.bengkelku.ui.screens.main.VehicleManagementScreen
import com.example.bengkelku.ui.screens.auth.VehicleSetupScreen

// Screen routes
object Screen {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp"
    const val VEHICLE_SETUP = "vehicle_setup"
    const val DASHBOARD = "dashboard"
    const val BOOKING = "booking"
    const val HISTORY = "history"
    const val PROFILE = "profile"
    const val VEHICLE_MANAGEMENT = "vehicle_management"
}

@Composable
fun BengkelKuNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Authentication Screens
        composable(Screen.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.REGISTER)
                },
                onNavigateToOTP = { phone ->
                    navController.navigate("${Screen.OTP}/$phone")
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.REGISTER) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOTP = { phone ->
                    navController.navigate("${Screen.OTP}/$phone")
                }
            )
        }

        composable("${Screen.OTP}/{phone}") { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            OTPScreen(
                phone = phone,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVehicleSetup = {
                    navController.navigate(Screen.VEHICLE_SETUP) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.VEHICLE_SETUP) {
            VehicleSetupScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        popUpTo(Screen.VEHICLE_SETUP) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.VEHICLE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        // Main App Screens
        composable(Screen.DASHBOARD) {
            DashboardScreen(
                onNavigateToBooking = {
                    navController.navigate(Screen.BOOKING)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.HISTORY)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.PROFILE)
                }
            )
        }

        composable(Screen.BOOKING) {
            BookingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.DASHBOARD) {
                        popUpTo(Screen.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.HISTORY) {
                        popUpTo(Screen.BOOKING) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HISTORY) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PROFILE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVehicleManagement = {
                    navController.navigate(Screen.VEHICLE_MANAGEMENT)
                },
                onLogout = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.VEHICLE_MANAGEMENT) {
            VehicleManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Placeholder Composables (akan kita buat nanti)
@Composable
fun VehicleSetupScreen(
    onNavigateToDashboard: () -> Unit
) {
    // TODO: Implement vehicle setup screen
}

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVehicleManagement: () -> Unit,
    onLogout: () -> Unit
) {
    // TODO: Implement profile screen
}