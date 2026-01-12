package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.AuthState
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch

// Definición de colores personalizados para la App
val AppRed = Color(0xFFD32F2F)      // Rojo principal
val AppLightRed = Color(0xFFFFEBEE) // Rojo muy claro para fondos
val AppGrey = Color(0xFF757575)      // Gris para textos secundarios
val AppLightGrey = Color(0xFFF5F5F5) // Gris para fondos de campos

@OptIn(ExperimentalMaterial3Api::class)
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
                    coroutineScope.launch { snackbarHostState.showSnackbar(it) }
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(AppLightRed, Color.White)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ICONO PRINCIPAL EN ROJO
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        imageVector = if (isRegisterMode) Icons.Default.PersonAdd else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp),
                        tint = AppRed
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isRegisterMode) "Crear Cuenta" else "Bienvenido",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = AppRed
                    )
                )

                Text(
                    text = if (isRegisterMode) "Únete a nosotros ahora" else "Accede a tu cuenta",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppGrey
                )

                Spacer(modifier = Modifier.height(40.dp))

                // CAMPO NOMBRE (Solo en registro)
                if (isRegisterMode) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = AppRed) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,   // ⭐ Texto negro al escribir
                            unfocusedTextColor = Color.Black, // ⭐ Texto negro siempre
                            focusedBorderColor = AppRed,
                            focusedLabelColor = AppRed,
                            cursorColor = AppRed
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // CAMPO EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AppRed) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,       // ⭐ Texto negro al escribir
                        unfocusedTextColor = Color.Black,     // ⭐ Texto negro siempre
                        focusedBorderColor = AppRed,
                        focusedLabelColor = AppRed,
                        cursorColor = AppRed
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = AppRed) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,       // ⭐ Texto negro al escribir
                        unfocusedTextColor = Color.Black,     // ⭐ Texto negro siempre
                        focusedBorderColor = AppRed,
                        focusedLabelColor = AppRed,
                        cursorColor = AppRed
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // BOTÓN PRINCIPAL
                if (authState == AuthState.LOADING) {
                    CircularProgressIndicator(color = AppRed)
                } else {
                    Button(
                        onClick = {
                            val cleanEmail = email.trim()
                            val cleanFullName = fullName.trim()

                            if (cleanEmail.isEmpty() || password.isEmpty() || (isRegisterMode && cleanFullName.isEmpty())) {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Completa todos los campos") }
                                return@Button
                            }

                            if (isRegisterMode) {
                                userViewModel.register(cleanFullName, cleanEmail, password)
                            } else {
                                userViewModel.login(cleanEmail, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                    ) {
                        Text(
                            text = if (isRegisterMode) "REGISTRARSE" else "INICIAR SESIÓN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CAMBIAR MODO (LOGIN/REGISTRO)
                TextButton(onClick = {
                    isRegisterMode = !isRegisterMode
                    userViewModel.resetAuthState()
                }) {
                    Text(
                        text = if (isRegisterMode) "¿Ya tienes cuenta? Inicia Sesión"
                        else "¿No tienes cuenta? Regístrate ahora",
                        color = AppGrey,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
