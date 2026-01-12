package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()

    // Redirección si el usuario cierra sesión
    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login_route") {
                popUpTo("main_route") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("MOTOMAX RACING", fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 2.sp)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed)
            )
        }
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Círculo decorativo con icono de Moto
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = AppRed,
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bienvenida Personalizada
                user?.let { userData ->
                    Text(
                        text = "¡Hola, Piloto!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppGrey
                    )
                    Text(
                        text = userData.fullName.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // BOTÓN: VER PERFIL
                MainButton(
                    text = "VER MI PERFIL",
                    icon = Icons.Default.AccountCircle,
                    onClick = { navController.navigate("user_route") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN: EDITAR AJUSTES
                MainButton(
                    text = "EDITAR AJUSTES",
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate("profile_route") }
                )

                Spacer(modifier = Modifier.weight(1f))

                // BOTÓN: CERRAR SESIÓN (Outlined)
                OutlinedButton(
                    onClick = { userViewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppRed)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun MainButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppRed),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
