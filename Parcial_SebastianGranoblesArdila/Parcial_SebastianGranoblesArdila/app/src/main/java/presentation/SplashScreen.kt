package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// IMPORT CORREGIDO: Eliminamos la palabra 'presentation' del path
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, userViewModel: UserViewModel) {

    // Animación de escala para el logo
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Animación de rebote al entrar
        scale.animateTo(
            targetValue = 1f,
            animationSpec = overshootTween(
                durationMillis = 1000,
                friction = 2f
            )
        )

        delay(2000) // Tiempo total de espera de la splash

        // Verificamos si hay un usuario logueado para decidir a dónde ir
        val destination = if (userViewModel.user.value != null) {
            "main_route"
        } else {
            "login_route"
        }

        navController.navigate(destination) {
            popUpTo("splash_route") { inclusive = true }
        }
    }

    // Diseño de la Pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD32F2F), // Rojo brillante
                        Color(0xFF8B0000)  // Rojo oscuro
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            Surface(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = Icons.Default.TwoWheeler,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .padding(25.dp)
                        .size(80.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MOTOMAX",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "RACING",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 8.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

// Función auxiliar para el efecto de rebote
fun <T> overshootTween(durationMillis: Int, friction: Float): TweenSpec<T> =
    tween(durationMillis = durationMillis, easing = {
        val t = it - 1.0f
        t * t * ((friction + 1) * t + friction) + 1.0f
    })