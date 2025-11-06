package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
// ⭐ ¡Asegúrate de que la ruta de importación es la correcta! ⭐
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    // --- OBSERVADORES DEL VIEWMODEL ---
    val user by userViewModel.user.collectAsState()
    val isLocked = userViewModel.isProfileEditingLocked
    val nationalities = userViewModel.nationalities

    // --- ESTADOS LOCALES PARA LA UI ---
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedNationality by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        profileImageUri = uri

    }
    LaunchedEffect(user) {
        user?.let {
            phone = it.phone
            age = it.age
            city = it.city
            selectedNationality = if (it.nationality.isNotBlank()) it.nationality else nationalities.firstOrNull() ?: ""
        }
    }
    LaunchedEffect(isLocked, user) {
        if (isLocked) {
            coroutineScope.launch {
                while (userViewModel.isProfileEditingLocked) {
                    val lastUpdate = user?.lastProfileUpdateTime ?: 0L
                    if (lastUpdate == 0L) break
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
    }

    // --- UI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes de Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección de imagen de perfil
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = rememberVectorPainter(Icons.Default.Person),
                        contentDescription = "Icono de perfil",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Text("Toca la imagen para cambiarla", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(user?.fullName ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(user?.email ?: "", style = MaterialTheme.typography.bodyLarge)
            Divider(modifier = Modifier.padding(vertical = 24.dp))

            // Formulario de edición
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), enabled = !isLocked)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth(), isError = age.isNotEmpty() && (age.toIntOrNull() ?: 0) < 18, enabled = !isLocked)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad de Residencia") }, modifier = Modifier.fillMaxWidth(), enabled = !isLocked)
            Spacer(modifier = Modifier.height(8.dp))

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if (!isLocked) expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedNationality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nacionalidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isLocked
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    nationalities.forEach { nationality ->
                        DropdownMenuItem(
                            text = { Text(nationality) },
                            onClick = {
                                selectedNationality = nationality
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (successMessage != null) {
                Text(successMessage!!, color = Color(0xFF006400), modifier = Modifier.padding(bottom = 8.dp))
            }

            Button(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    if (phone.isBlank() || age.isBlank() || city.isBlank() || selectedNationality.isBlank()) {
                        errorMessage = "Todos los campos son obligatorios."
                        successMessage = null
                    } else if (ageInt == null || ageInt < 18) {
                        errorMessage = "Debes ser mayor de 18 años."
                        successMessage = null
                    } else {
                        userViewModel.updateUserInfo(phone, age, city, selectedNationality)
                        successMessage = "¡Perfil actualizado con éxito!"
                        errorMessage = null
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLocked
            ) {
                Text(if (isLocked) "BLOQUEADO ($remainingTime)" else "GUARDAR CAMBIOS")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text("BORRAR CUENTA")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Borrado de Cuenta") },
            text = { Text("¿Estás seguro? Esta acción es irreversible y se borrarán todos tus datos de la nube.") },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.deleteAccount()
                        showDeleteDialog = false
                        navController.navigate("login_route") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("SÍ, BORRAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("NO, CANCELAR")
                }
            }
        )
    }
}
