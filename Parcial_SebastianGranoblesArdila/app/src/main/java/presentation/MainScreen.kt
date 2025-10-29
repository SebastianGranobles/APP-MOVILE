package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val currentUser = userState
    val isLoggedIn = userViewModel.isLoggedIn

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login_route") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    if (!isLoggedIn || currentUser == null) {
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido, ${currentUser.fullName.split(" ").firstOrNull() ?: "Usuario"}") },
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
                onClick = { navController.navigate("user_route") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("TARJETA DE USUARIO")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("profile_route") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("AJUSTES DE PERFIL")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { userViewModel.logout() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("CERRAR SESIÓN")
            }
        }
    }
}
