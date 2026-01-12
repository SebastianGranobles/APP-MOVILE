package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    // Definimos colores locales únicos para este archivo para evitar conflictos globales
    val LocalRed = Color(0xFFD32F2F)
    val LocalLightRed = Color(0xFFFFEBEE)
    val LocalGrey = Color(0xFF757575)

    val user by userViewModel.user.collectAsState()
    val isLocked = userViewModel.isProfileEditingLocked
    val nationalities = userViewModel.nationalities

    // Inicialización blindada contra nulos
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var age by remember { mutableStateOf(user?.age ?: "") }
    var city by remember { mutableStateOf(user?.city ?: "") }
    var selectedNationality by remember {
        mutableStateOf(user?.nationality ?: nationalities.firstOrNull() ?: "Colombiana")
    }

    var profileImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? -> profileImageUri = uri }

    LaunchedEffect(user) {
        user?.let {
            if (phone.isEmpty()) phone = it.phone
            if (age.isEmpty()) age = it.age
            if (city.isEmpty()) city = it.city
            if (selectedNationality.isEmpty() || selectedNationality == nationalities.firstOrNull()) {
                selectedNationality = if (it.nationality.isNotBlank()) it.nationality
                else nationalities.firstOrNull() ?: "Colombiana"
            }
        }
    }

    LaunchedEffect(isLocked, user) {
        if (isLocked && user != null) {
            coroutineScope.launch {
                while (userViewModel.isProfileEditingLocked) {
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
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AJUSTES DE PERFIL", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalRed),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(LocalLightRed, Color.White)))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(130.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(3.dp, LocalRed),
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable { if (!isLocked) imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(
                                model = profileImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = LocalGrey)
                        }
                    }
                }
                Text("Toca para cambiar imagen", fontSize = 12.sp, color = LocalGrey, modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(24.dp))

                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = LocalRed,
                    focusedLabelColor = LocalRed,
                    cursorColor = LocalRed
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLocked,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = LocalRed) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Edad") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLocked,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null, tint = LocalRed) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLocked,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = LocalRed) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!isLocked) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedNationality,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Nacionalidad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = LocalRed) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        enabled = !isLocked,
                        shape = RoundedCornerShape(16.dp),
                        colors = textFieldColors
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

                Spacer(modifier = Modifier.height(30.dp))

                errorMessage?.let { Text(it, color = LocalRed, fontSize = 14.sp) }
                successMessage?.let { Text(it, color = Color(0xFF2E7D32), fontSize = 14.sp) }

                Button(
                    onClick = {
                        val ageInt = age.toIntOrNull()
                        if (phone.isBlank() || age.isBlank() || city.isBlank() || selectedNationality.isBlank()) {
                            errorMessage = "Campos obligatorios vacíos."
                        } else if (ageInt == null || ageInt < 18) {
                            errorMessage = "Debes ser mayor de 18 años."
                        } else {
                            userViewModel.updateUserInfo(phone, age, city, selectedNationality)
                            successMessage = "¡Perfil actualizado!"
                            errorMessage = null
                            coroutineScope.launch {
                                delay(1000)
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isLocked,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LocalRed)
                ) {
                    Text(if (isLocked) "BLOQUEADO ($remainingTime)" else "GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LocalRed),
                    border = BorderStroke(1.dp, LocalRed)
                ) {
                    Text("BORRAR CUENTA", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar cuenta?", fontWeight = FontWeight.Bold, color = LocalRed) },
            text = { Text("Esta acción es irreversible. Perderás todos tus datos de piloto.") },
            confirmButton = {
                Button(onClick = {
                    userViewModel.deleteAccount()
                    navController.navigate("login_route") { popUpTo(0) }
                }, colors = ButtonDefaults.buttonColors(containerColor = LocalRed)) {
                    Text("SÍ, ELIMINAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCELAR", color = LocalGrey)
                }
            }
        )
    }
}
