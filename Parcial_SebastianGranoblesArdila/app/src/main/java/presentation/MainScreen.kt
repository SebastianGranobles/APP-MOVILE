package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel) {

    val userState by userViewModel.userState

    // Leemos 'isLoggedIn' como una variable normal para obtener el valor Boolean.
    val isLoggedIn = userViewModel.isLoggedIn

    // Efecto de seguridad: se ejecuta si el estado de isLoggedIn cambia.
    // Ahora 'isLoggedIn' es un Boolean y el operador '!' funcionará.
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login_route") {
                // Borra todo el historial para que no se pueda volver atrás.
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    // Esta comprobación de seguridad evita mostrar la pantalla si el usuario ya no está logueado.
    if (!isLoggedIn) {
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido, ${userState.fullName.split(" ").firstOrNull() ?: "Usuario"}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido a la red de datos de Michi Bank",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { navController.navigate("profile_route") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("VER MI PERFIL")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("user_route") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("TARJETA DE USUARIO")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    userViewModel.logout() // Llama a la función de logout real del ViewModel
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("CERRAR SESIÓN")
            }
        }
    }
}