package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController, userViewModel: UserViewModel) {
    val userState by userViewModel.userState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarjeta de Usuario") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
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
            if (userState.profileImageUri != null) {
                AsyncImage(
                    model = userState.profileImageUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(150.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Person),
                    contentDescription = "Icono de perfil por defecto",
                    modifier = Modifier.size(150.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = userState.fullName,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary
            )

            // ⭐ AJUSTE: Botón para volver al menú principal.
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { navController.popBackStack() }, // popBackStack() es la forma más segura de "volver atrás".
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            ) {
                Text("VOLVER AL MENÚ")
            }
        }
    }
}

