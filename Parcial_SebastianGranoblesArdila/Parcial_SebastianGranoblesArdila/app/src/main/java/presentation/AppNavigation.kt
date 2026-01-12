package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Única instancia del ViewModel para toda la App (Single Source of Truth)
    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash_route"
    ) {
        // 1. Splash Screen (Entrada con animación)
        composable("splash_route") {
            SplashScreen(navController = navController, userViewModel = userViewModel)
        }
// Dentro de tu NavHost { ... }
        composable("appointment_route") {
            AppointmentScreen(navController, userViewModel)
        }

        // 2. Login (Acceso)
        composable("login_route") {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }

        // 3. Registro (IMPORTANTE: Faltaba en tu archivo anterior)
        composable("register_route") {
            RegisterScreen(navController = navController, userViewModel = userViewModel)
        }

        // 4. Main Screen (Dashboard Minimalista con el Menú Lateral)
        composable("main_route") {
            MainScreen(navController = navController, userViewModel = userViewModel)
        }

        // 5. Perfil / Información de Cuenta (Ruta unificada)
        composable("profile_route") {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }

        // 6. Visualización de datos (Si decides mantenerla aparte)
        composable("user_route") {
            UserScreen(navController = navController, userViewModel = userViewModel)
        }

        // 7. Recuperar Contraseña
        composable("recover_password_route") {
            RecuperarContraseñaScreen(navController = navController, userViewModel = userViewModel)
        }

        // 8. Historial de Cambios de Contraseña
        composable("password_history_route") {
            PasswordHistoryScreen(navController = navController, userViewModel = userViewModel)
        }
    }
}
