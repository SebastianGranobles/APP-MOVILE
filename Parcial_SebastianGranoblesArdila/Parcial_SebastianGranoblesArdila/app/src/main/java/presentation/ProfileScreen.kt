package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    val LocalRed = Color(0xFFD32F2F)
    val LocalLightRed = Color(0xFFFFEBEE)
    val LocalGrey = Color(0xFF757575)

    val user by userViewModel.user.collectAsState()
    val isLocked by userViewModel.isProfileEditingLocked.collectAsState()
    val nationalities = userViewModel.nationalities

    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedNationality by remember { mutableStateOf("Colombiana") }
    var remainingTime by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var expandedNationality by remember { mutableStateOf(false) }

    // Estilo común para los campos de texto (Letras negras)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedBorderColor = LocalRed,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = LocalRed
    )

    // Cargar datos iniciales del usuario desde Firebase
    LaunchedEffect(user) {
        user?.let {
            phone = it.phone
            age = it.age
            city = it.city
            selectedNationality = if (it.nationality.isNotBlank()) it.nationality else "Colombiana"
        }
    }

    // Lógica del contador de bloqueo (3 horas)
    LaunchedEffect(isLocked, user) {
        if (isLocked && user != null) {
            while (true) {
                val lastUpdate = user?.lastProfileUpdateTime ?: 0L
                val remainingMillis = (lastUpdate + 3 * 60 * 60 * 1000) - System.currentTimeMillis()
                if (remainingMillis <= 0) break

                val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60
                remainingTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                delay(1000)
            }
        }
    }

    // DIÁLOGO DE ELIMINACIÓN
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar cuenta?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción es permanente y borrará todos tus datos de piloto de la base de datos.") },
            confirmButton = {
                TextButton(onClick = {
                    userViewModel.deleteAccount()
                    navController.navigate("login_route") { popUpTo(0) }
                }) {
                    Text("BORRAR", color = LocalRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI PERFIL TÉCNICO", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalRed),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        userViewModel.logout()
                        navController.navigate("login_route") { popUpTo(0) }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(LocalLightRed, Color.White)))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de Perfil / Icono
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(4.dp, LocalRed),
                shadowElevation = 4.dp
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(25.dp).fillMaxSize(), tint = LocalGrey)
            }

            Text(
                text = user?.fullName?.uppercase() ?: "PILOTO",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(text = user?.email ?: "", fontSize = 14.sp, color = LocalGrey)

            Spacer(Modifier.height(32.dp))

            // CAMPOS DE EDICIÓN
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono de contacto") },
                enabled = !isLocked,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = LocalRed) },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Edad") },
                    enabled = !isLocked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ciudad") },
                    enabled = !isLocked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
            }

            Spacer(Modifier.height(12.dp))

            // Selector de Nacionalidad (Dropdown corregido)
            ExposedDropdownMenuBox(
                expanded = expandedNationality && !isLocked,
                onExpandedChange = { if (!isLocked) expandedNationality = !expandedNationality }
            ) {
                OutlinedTextField(
                    value = selectedNationality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nacionalidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedNationality) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isLocked,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedNationality,
                    onDismissRequest = { expandedNationality = false }
                ) {
                    nationalities.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedNationality = option
                                expandedNationality = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // BOTÓN GUARDAR (Se conecta con Firebase mediante UserViewModel)
            Button(
                onClick = {
                    userViewModel.updateUserInfo(phone, age, city, selectedNationality)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLocked && phone.isNotEmpty() && age.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalRed,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLocked) {
                    Icon(Icons.Default.Lock, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("BLOQUEADO POR 3H ($remainingTime)", fontSize = 13.sp)
                } else {
                    Icon(Icons.Default.CloudUpload, null)
                    Spacer(Modifier.width(8.dp))
                    Text("SINCRONIZAR CON FIREBASE", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { showDeleteDialog = true }) {
                Text("ELIMINAR CUENTA PERMANENTEMENTE", color = LocalRed, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}