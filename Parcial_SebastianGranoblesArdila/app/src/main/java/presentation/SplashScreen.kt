package com.example.parcial_sebastiangranoblesardila.presentation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, userViewModel: UserViewModel) {


    LaunchedEffect(key1 = true) {
        delay(2000)
        val destination = if (userViewModel.user.value != null) {
            "main_route"
        } else {
            "login_route"
        }
        navController.navigate(destination) {
            popUpTo("splash_route") { inclusive = true }
        }
    }

    // Tu UI del Splash Screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Cargando Michi-Bank...", fontSize = 24.sp)
    }
}
