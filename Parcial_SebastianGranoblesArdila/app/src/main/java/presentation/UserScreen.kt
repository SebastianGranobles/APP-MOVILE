package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        user?.let { userData ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Hacemos la columna deslizable
            ) {

                InfoRow("Nombre Completo:", userData.fullName)
                InfoRow("Correo Electrónico:", userData.email)
                InfoRow("Teléfono:", if (userData.phone.isNotBlank()) userData.phone else "No especificado")
                InfoRow("Edad:", if (userData.age.isNotBlank()) userData.age else "No especificada")
                InfoRow("Ciudad:", if (userData.city.isNotBlank()) userData.city else "No especificada")
                InfoRow("Nacionalidad:", if (userData.nationality.isNotBlank()) userData.nationality else "No especificada")

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("profile_route") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar Perfil y Ajustes")
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}
