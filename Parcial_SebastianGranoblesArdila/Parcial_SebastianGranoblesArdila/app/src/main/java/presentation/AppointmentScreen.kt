package com.example.parcial_sebastiangranoblesardila.presentation

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavController, userViewModel: UserViewModel) {
    val AppRed = Color(0xFFD32F2F)
    val scrollState = rememberScrollState()

    // Validación de Placa: 3 letras, 2 números, 1 letra
    val plateRegex = remember { Regex("^[A-Z]{3}[0-9]{2}[A-Z]$") }

    // Estados de Datos
    var clientName by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var clientAddress by remember { mutableStateOf("") }

    var selectedBrand by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var selectedDisplacement by remember { mutableStateOf("") }

    // --- NUEVO ESTADO PARA EL SERVICIO ---
    val services = listOf("Sincronización", "Cambio de Aceite", "Mantenimiento General", "Frenos", "Eléctrico")
    var selectedService by remember { mutableStateOf("") }

    var diagnosis by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf("") }

    val isPlateValid = plate.matches(plateRegex)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("REGISTRO DE SERVICIO", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle("DATOS DEL CLIENTE")
            OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Nombre Cliente") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = clientId, onValueChange = { clientId = it }, label = { Text("Cédula (Dato Sensible)") }, modifier = Modifier.fillMaxWidth())

            SectionTitle("DATOS TÉCNICOS")
            AppointmentDropdown("Marca", listOf("Yamaha", "Honda", "Suzuki", "AKT", "Bajaj")) { selectedBrand = it }
            OutlinedTextField(
                value = plate,
                onValueChange = { if (it.length <= 6) plate = it.uppercase() },
                label = { Text("Placa (ABC12D)") },
                modifier = Modifier.fillMaxWidth(),
                isError = plate.isNotEmpty() && !isPlateValid
            )

            SectionTitle("DETALLES DEL TRABAJO")
            // --- AQUÍ EL USUARIO AHORA ELIGE EL SERVICIO ---
            AppointmentDropdown("Tipo de Servicio", services) { selectedService = it }

            OutlinedTextField(
                value = diagnosis,
                onValueChange = { diagnosis = it },
                label = { Text("Diagnóstico / Observaciones") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            AppointmentDropdown("Hora de Ingreso", listOf("08:00 AM", "10:00 AM", "02:00 PM")) { selectedHour = it }

            Button(
                onClick = {
                    userViewModel.addAppointment(
                        clientName = clientName,
                        clientId = clientId,
                        clientPhone = clientPhone,
                        clientAddress = clientAddress,
                        brand = selectedBrand,
                        motoModel = "",
                        plate = plate,
                        displacement = selectedDisplacement,
                        service = selectedService, // <-- Ahora enviamos lo seleccionado
                        diagnosis = diagnosis,
                        time = selectedHour
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                enabled = clientName.isNotEmpty() && isPlateValid && selectedService.isNotEmpty()
            ) {
                Text("GUARDAR REGISTRO", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDropdown(label: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selection ->
                DropdownMenuItem(text = { Text(selection) }, onClick = {
                    selectedOption = selection
                    onSelect(selection)
                    expanded = false
                })
            }
        }
    }
}
