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
    // Se crea una ÚNICA instancia del ViewModel que se compartirá entre todas las pantallas.
    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash_route"
    ) {
        composable("splash_route") {
            // ⭐ ARREGLO: Llamamos a NUESTRA SplashScreen, no a la del sistema.
            SplashScreen(navController = navController)
        }
        composable("login_route") {
            // Pasamos el ViewModel a la pantalla de Login.
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("main_route") {
            // Pasamos el ViewModel a la pantalla Main.
            MainScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("user_route") {
            // Pasamos el ViewModel a la pantalla de User.
            UserScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("profile_route") {
            // Pasamos el ViewModel a la pantalla de Profile.
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("recover_password_route") {
            RecuperarContraseñaScreen(navController = navController)
        }
    }
}