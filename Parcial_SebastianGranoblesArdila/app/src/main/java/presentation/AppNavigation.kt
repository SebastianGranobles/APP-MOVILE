package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// ⭐ 1. ¡CORRECCIÓN! Esta es la ruta correcta a tu ViewModel
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "splash_route" // Empezamos siempre por el splash
    ) {
        composable("splash_route") {
            SplashScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("login_route") {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }

        composable("main_route") {
            MainScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("user_route") {
            UserScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("profile_route") {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("recover_password_route") {
            RecuperarContraseñaScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("password_history_route") {
            PasswordHistoryScreen(navController = navController, userViewModel = userViewModel)
        }
    }
}
