package com.example.parcial_sebastiangranoblesardila.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.AuthState
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isRegisterMode by remember { mutableStateOf(false) }
    val authState by userViewModel.authState.collectAsState()
    val firebaseErrorMessage by userViewModel.errorMessage.collectAsState()
    LaunchedEffect(authState) {
        when (authState) {
            AuthState.SUCCESS -> {
                navController.navigate("main_route") {
                    popUpTo("login_route") { inclusive = true }
                }
            }
            AuthState.ERROR -> {
                firebaseErrorMessage?.let {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(it)
                    }
                }
            }
            else -> {  }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Hacemos la columna deslizable
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isRegisterMode) "Crear Cuenta" else "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            // CAMPO DE NOMBRE COMPLETO (SOLO VISIBLE EN MODO REGISTRO)
            if (isRegisterMode) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mín. 6 caracteres)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (authState == AuthState.LOADING) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val showError: (String) -> Unit = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                        val cleanEmail = email.trim()
                        val cleanFullName = fullName.trim()
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                            showError("El formato del correo electrónico no es válido.")
                            return@Button
                        }
                        if (password.length < 6) {
                            showError("La contraseña debe tener al menos 6 caracteres.")
                            return@Button
                        }
                        if (isRegisterMode && cleanFullName.isBlank()) {
                            showError("El nombre completo no puede estar vacío.")
                            return@Button
                        }
                        userViewModel.resetAuthState()
                        if (isRegisterMode) {
                            userViewModel.register(cleanEmail, password, cleanFullName)
                        } else {
                            userViewModel.login(cleanEmail, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isRegisterMode) "REGISTRARSE" else "ENTRAR")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                isRegisterMode = !isRegisterMode // Cambia el modo
                userViewModel.resetAuthState() // Limpia errores al cambiar de modo
            }) {
                Text(
                    if (isRegisterMode) "¿Ya tienes cuenta? Inicia Sesión"
                    else "¿No tienes cuenta? Regístrate"
                )
            }
        }
    }
}
