package com.example.parcial_sebastiangranoblesardila.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.Presentation.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContraseñaScreen(
    navController: NavController,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperar Contraseña",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Ingresa tu correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val password = viewModel.recoverPassword(email)
                if (password != null) {
                    Toast.makeText(
                        context,
                        "Recuperación exitosa. Tu contraseña es: $password",
                        Toast.LENGTH_LONG
                    ).show()
                    // Regresa a la pantalla de login
                    navController.popBackStack()
                } else {
                    Toast.makeText(
                        context,
                        "El correo ingresado no está registrado.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("RECUPERAR")
        }
    }
}


