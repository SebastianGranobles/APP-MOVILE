package com.example.parcial_sebastiangranoblesardila.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    val userState by userViewModel.userState
    userState?.let { currentUser ->
        var phone by remember { mutableStateOf(currentUser.phone) }
        var age by remember { mutableStateOf(currentUser.age) }
        var city by remember { mutableStateOf(currentUser.city) }
        var nationality by remember { mutableStateOf(currentUser.nationality) }
        var ageError by remember { mutableStateOf<String?>(null) }
        var showSuccessMessage by remember { mutableStateOf(false) }
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            userViewModel.onProfileImageChange(uri)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ajustes de Perfil") },
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Box(modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }) {
                    val painter = if (currentUser.profileImageUri != null) {
                        rememberAsyncImagePainter(currentUser.profileImageUri)
                    } else {
                        rememberVectorPainter(Icons.Default.Person)
                    }
                    Image(
                        painter = painter,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(120.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                }
                Text("Toca la imagen para cambiarla", fontSize = 12.sp, color = Color.Gray)

                Spacer(Modifier.height(24.dp))
                Text(currentUser.fullName, style = MaterialTheme.typography.headlineSmall)
                Text(currentUser.email, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Divider(modifier = Modifier.padding(vertical = 24.dp))
                Text("EDITAR INFORMACIÓN PERSONAL", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        age = it
                        ageError = null
                    },
                    label = { Text("Edad") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = ageError != null,
                    supportingText = { if (ageError != null) Text(ageError!!) }
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                NationalityDropdown(
                    nationalities = userViewModel.nationalities,
                    selectedNationality = nationality,
                    onNationalitySelected = { nationality = it }
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val ageAsInt = age.toIntOrNull()
                        if (ageAsInt == null || ageAsInt < 18) {
                            ageError = "Debes ser mayor de 18 años."
                        } else {
                            ageError = null
                            userViewModel.updateUserInfo(phone, age, city, nationality)
                            showSuccessMessage = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("GUARDAR CAMBIOS")
                }

                if (showSuccessMessage) {
                    Text("¡Información guardada con éxito!", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NationalityDropdown(
    nationalities: List<String>,
    selectedNationality: String,
    onNationalitySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedNationality,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nacionalidad") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            nationalities.forEach { nationality ->
                DropdownMenuItem(
                    text = { Text(nationality) },
                    onClick = {
                        onNationalitySelected(nationality)
                        expanded = false
                    }
                )
            }
        }
    }
}
