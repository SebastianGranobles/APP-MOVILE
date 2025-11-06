package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// ⭐ ¡Asegúrate de que la ruta de importación es la correcta! ⭐
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.AuthState
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContraseñaScreen(navController: NavController, userViewModel: UserViewModel) {

    // --- ESTADOS LOCALES PARA LOS CAMPOS DE TEXTO ---
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    // --- OBSERVADORES DEL VIEWMODEL ---
    val authState by userViewModel.authState.collectAsState()
    val firebaseErrorMessage by userViewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(authState) {
        when (authState) {
            AuthState.SUCCESS -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("¡Contraseña cambiada con éxito!")
                }
                userViewModel.resetAuthState()
                navController.popBackStack()
            }
            AuthState.ERROR -> {

                firebaseErrorMessage?.let {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(it)
                    }
                }
            }
            else -> { /* No hacer nada en IDLE o LOADING */ }
        }
    }

    // --- UI ---
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Limpia el estado si el usuario cancela y se va
                        userViewModel.resetAuthState()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña Actual") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Nueva Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = newPassword != confirmPassword && confirmPassword.isNotEmpty()
            )
            if (newPassword != confirmPassword && confirmPassword.isNotEmpty()) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
                )
            }

            localErrorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (authState == AuthState.LOADING) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        localErrorMessage = null // Limpiar errores locales
                        userViewModel.resetAuthState() // Limpiar errores de Firebase

                        // Validaciones locales antes de llamar al ViewModel
                        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                            localErrorMessage = "Todos los campos son obligatorios."
                        } else if (newPassword != confirmPassword) {
                            localErrorMessage = "Las contraseñas nuevas no coinciden."
                        } else {
                            // Si todo está bien, llama a la función del ViewModel
                            userViewModel.changePassword(currentPassword, newPassword)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CAMBIAR CONTRASEÑA")
                }
            }
        }
    }
}
