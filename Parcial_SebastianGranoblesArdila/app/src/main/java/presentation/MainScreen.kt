package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// ⭐ ¡Asegúrate de que la ruta de importación es la correcta! ⭐
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()
    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login_route") {
                popUpTo("main_route") { inclusive = true }
            }
        }
    }

    //  UI
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            user?.let { userData ->
                Text(
                    text = "¡Bienvenido, ${userData.fullName}!",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("user_route") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver mi perfil")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("profile_route") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar información y Ajustes")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    userViewModel.logout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}
