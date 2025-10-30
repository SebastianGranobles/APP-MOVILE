package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContraseñaScreen(navController: NavController, userViewModel: UserViewModel) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Gestión de Contraseña", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))

            // Campo para la contraseña actual
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña Actual") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text("Confirmar Nueva Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = (newPassword != confirmNewPassword),
                singleLine = true
            )


            if (newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty()) {
                Text(
                    "Las contraseñas nuevas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {

                    errorMessage = null
                    successMessage = null

                    // Validaciones
                    if (currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
                        errorMessage = "Todos los campos son obligatorios."
                    } else if (newPassword != confirmNewPassword) {
                        errorMessage = "La nueva contraseña y su confirmación no coinciden."
                    } else {
                        val success = userViewModel.changePassword(
                            currentPass = currentPassword,
                            newPass = newPassword
                        )

                        if (success) {
                            successMessage = "¡Contraseña cambiada con éxito!"

                            currentPassword = ""
                            newPassword = ""
                            confirmNewPassword = ""
                        } else {
                            errorMessage = "La contraseña actual es incorrecta."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("CAMBIAR CONTRASEÑA")
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            if (successMessage != null) {
                Text(successMessage!!, color = Color(0xFF006400)) // Color verde oscuro
            }
        }
    }
}
