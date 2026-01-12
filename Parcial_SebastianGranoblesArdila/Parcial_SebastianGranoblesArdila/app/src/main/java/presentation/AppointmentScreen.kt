package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

    // Listas de opciones
    val brands = listOf(
        "Yamaha", "Honda", "Kawasaki", "Suzuki", "TVS", "KTM", "AKT",
        "Ducati", "Harley-Davidson", "Triumph", "Bajaj", "Aprilia", "Indian","ECO DELUXE"
    )

    // Lista de Cilindraje (100cc a 700cc)
    val displacements = listOf("100cc", "125cc", "150cc", "200cc", "250cc", "300cc", "400cc", "500cc", "600cc", "700cc")

    val categories = listOf("Mantenimiento Preventivo", "Reparaciones Mecánicas")

    val preventiveServices = listOf(
        "Cambio de aceite y filtro",
        "Revisión general / puesta a punto",
        "Ajuste de cadena y tensión",
        "Verificación y ajuste de frenos",
        "Alineación de ruedas"
    )

    val mechanicalServices = listOf(
        "Reparación o rectificación de motor",
        "Cambio de embrague",
        "Reemplazo de bujías",
        "Averías de transmisión",
        "Reparación de suspensión"
    )

    val hours = listOf("08:00 AM", "10:00 AM", "12:00 PM", "02:00 PM", "04:00 PM")

    // Estados de selección
    var selectedBrand by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") } // NUEVO: Estado para la placa
    var selectedDisplacement by remember { mutableStateOf("") } // NUEVO: Estado para el cilindraje
    var selectedCategory by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    // Determinar qué lista de servicios mostrar
    val currentServiceOptions = when (selectedCategory) {
        "Mantenimiento Preventivo" -> preventiveServices
        "Reparaciones Mecánicas" -> mechanicalServices
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AGENDAR CITA TALLER", color = Color.White, fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Detalles de la Cita", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)

            // 1. Selector de Marca
            AppointmentDropdown(label = "Marca de Moto", options = brands) { selectedBrand = it }

            // 2. Campo de Placa (Manual)
            OutlinedTextField(
                value = plate,
                onValueChange = { if (it.length <= 6) plate = it.uppercase() },
                label = { Text("Placa de la Moto (Ej: ABC12D)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("Máximo 6 caracteres") }
            )

            // 3. Selector de Cilindraje
            AppointmentDropdown(label = "Cilindraje", options = displacements) { selectedDisplacement = it }

            // 4. Selector de Categoría (Preventivo vs Mecánico)
            AppointmentDropdown(label = "Categoría de Servicio", options = categories) {
                selectedCategory = it
                selectedService = "" // Reiniciamos el servicio si cambia la categoría
            }

            // 5. Selector de Servicio Específico
            if (selectedCategory.isNotEmpty()) {
                AppointmentDropdown(
                    label = "Servicio Específico",
                    options = currentServiceOptions
                ) { selectedService = it }
            }

            // 6. Selector de Hora
            AppointmentDropdown(label = "Hora Preferida", options = hours) { selectedHour = it }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Confirmar (Validación actualizada)
            Button(
                onClick = {
                    if (selectedBrand.isNotEmpty() && plate.isNotEmpty() && selectedDisplacement.isNotEmpty() &&
                        selectedService.isNotEmpty() && selectedHour.isNotEmpty()) {

                        userViewModel.addAppointment(
                            brand = selectedBrand,
                            plate = plate,
                            displacement = selectedDisplacement,
                            service = selectedService,
                            time = selectedHour
                        )
                        showSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(16.dp),
                enabled = selectedBrand.isNotEmpty() && plate.length >= 5 &&
                        selectedDisplacement.isNotEmpty() && selectedService.isNotEmpty() &&
                        selectedHour.isNotEmpty()
            ) {
                Text("CONFIRMAR CITA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("¡Cita Agendada!") },
            text = { Text("Tu cita para la placa $plate ($selectedBrand) ha sido registrada localmente.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    navController.popBackStack()
                }) { Text("ACEPTAR", color = AppRed) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDropdown(label: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    LaunchedEffect(options) {
        if (!options.contains(selectedOption)) {
            selectedOption = ""
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selection ->
                DropdownMenuItem(
                    text = { Text(selection) },
                    onClick = {
                        selectedOption = selection
                        onSelect(selection)
                        expanded = false
                    }
                )
            }
        }
    }
}
