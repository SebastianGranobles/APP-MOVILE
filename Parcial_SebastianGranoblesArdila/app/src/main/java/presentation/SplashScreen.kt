// El nombre del paquete ya es correcto.
package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
// import androidx.compose.foundation.Image // <-- Ya no es necesaria esta importación
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
// import androidx.compose.ui.res.painterResource // <-- Tampoco esta
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// import com.example.parcial_sebastiangranoblesardila.R // <-- Ni esta
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500), // Animación de aparición suave.
        label = "splash_alpha_animation"
    )

    // Este efecto se ejecuta una sola vez para controlar la animación y la navegación.
    LaunchedEffect(key1 = true) {
        startAnimation = true // Inicia la animación.
        delay(2500L) // Espera 2.5 segundos.

        // Navega a la pantalla de login y elimina la splash screen del historial.
        navController.navigate("login_route") {
            popUpTo("splash_route") { inclusive = true }
        }
    }

    // Contenedor principal que centra todo.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Columna que ahora solo contiene el texto, con la animación de aparición.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .alpha(alphaAnimation.value), // Aplica la animación a todo el contenido.
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenidos",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
