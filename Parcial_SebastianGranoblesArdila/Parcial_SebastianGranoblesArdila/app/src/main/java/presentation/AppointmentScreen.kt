package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavController, userViewModel: UserViewModel) {
    val AppRed = Color(0xFFD32F2F)
    val AppLightRed = Color(0xFFFFEBEE)
    val scrollState = rememberScrollState()

    // Mantenemos el mapa para el cálculo interno del total
    val servicePrices = mapOf(
        "Cambio de Aceite" to 60000.0,
        "Ajuste General" to 140000.0,
        "Kit de arrastre" to 70000.0,
        "Batería" to 45000.0,
        "Lavado" to 15000.0,
        "Eléctrico" to 60000.0
    )

    // --- ESTADOS DE LOS CAMPOS ---
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var displacement by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var phone1 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedMechanic by remember { mutableStateOf("") }
    var laborCost by remember { mutableStateOf("") }
    var problemDescription by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Efectivo") }
    val selectedServices = remember { mutableStateListOf<String>() }

    // --- LÓGICA DE HORARIOS (L-V 9AM-5PM) ---
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

    val isWorkingDay = dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY
    val isWorkingHour = hourOfDay in 9..16

    fun clearFields() {
        plate = ""; brand = ""; model = ""; year = ""; displacement = ""; mileage = ""
        clientName = ""; phone1 = ""; email = ""; selectedMechanic = ""
        laborCost = ""; problemDescription = ""; paymentMethod = "Efectivo"
        selectedServices.clear()
    }

    val totalServicesCost = selectedServices.sumOf { servicePrices[it] ?: 0.0 }
    val labor = laborCost.toDoubleOrNull() ?: 0.0
    val totalToPay = labor + totalServicesCost

    val mechanicsList = listOf("Sebastian Granobles", "Juan Perez", "Carlos Rodriguez", "Andrés Mendoza")
    var showConfirmDialog by remember { mutableStateOf(false) }

    val isFormValid = plate.length == 6 && clientName.isNotEmpty() &&
            phone1.isNotEmpty() && selectedMechanic.isNotEmpty() &&
            isWorkingDay && isWorkingHour

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = AppRed,
        unfocusedLabelColor = Color.Black,
        focusedBorderColor = AppRed,
        unfocusedBorderColor = Color.Gray
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar Orden", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Cliente: $clientName", color = Color.Black)
                    Text("Moto: $brand $model ($plate)", color = Color.DarkGray)
                    Text("Mano de Obra: $${String.format("%,.0f", labor)}", color = Color.Black)
                    Text("Repuestos/Servicios: $${String.format("%,.0f", totalServicesCost)}", color = Color.Black)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("TOTAL FINAL: $${String.format("%,.0f", totalToPay)}", fontWeight = FontWeight.ExtraBold, color = AppRed, fontSize = 18.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        val newApp = Appointment(
                            plate = plate, brand = brand, model = model, year = year,
                            displacement = displacement, mileage = mileage, clientName = clientName,
                            phone1 = phone1, email = email, selectedServices = selectedServices.toList(),
                            problemDescription = problemDescription,
                            entryDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                            entryTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                            laborCost = labor,
                            partsCost = totalServicesCost,
                            totalCost = totalToPay,
                            paymentMethod = paymentMethod,
                            mechanic = selectedMechanic,
                            status = "Recibido"
                        )
                        if (userViewModel.addAppointment(newApp)) {
                            clearFields()
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                ) {
                    Text("AGENDAR", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ORDEN DE ENTRADA", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(AppLightRed, Color.White)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Alerta de horario
                if (!isWorkingDay || !isWorkingHour) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        border = BorderStroke(1.dp, AppRed)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = AppRed)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "FUERA DE HORARIO: Atención Lunes a Viernes (9:00 AM - 5:00 PM)",
                                color = AppRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                SectionTitle("1. DATOS DE LA MOTO")

                OutlinedTextField(
                    value = plate,
                    onValueChange = { if (it.length <= 6) plate = it.uppercase() },
                    label = { Text("Placa (6 caracteres) *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppointmentDropdown("Marca", listOf("Yamaha", "Honda", "Suzuki", "AKT", "KTM", "Otra"), Modifier.weight(1f)) { brand = it }
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Modelo *") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = displacement,
                        onValueChange = { if (it.all { c -> c.isDigit() }) displacement = it },
                        label = { Text("Cilindraje (CC)") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = mileage,
                        onValueChange = { if (it.all { c -> c.isDigit() }) mileage = it },
                        label = { Text("Kilometraje (Km) *") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                SectionTitle("2. SERVICIOS Y SÍNTOMAS")

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    servicePrices.keys.toList().chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { service ->
                                val isSelected = selectedServices.contains(service)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedServices.remove(service)
                                        else selectedServices.add(service)
                                    },
                                    label = { Text(service) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AppRed,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                SectionTitle("3. COSTOS Y VALORES")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = laborCost,
                        onValueChange = { if (it.all { c -> c.isDigit() }) laborCost = it },
                        label = { Text("Mano de Obra ($) *") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = String.format("%,.0f", totalServicesCost),
                        onValueChange = {},
                        label = { Text("Total Repuestos ($)") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        readOnly = true
                    )
                }

                // TOTAL ESTIMADO (Resumen visual)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppRed.copy(alpha = 0.1f)),
                    border = BorderStroke(2.dp, AppRed)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL ESTIMADO:", fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("$${String.format("%,.0f", totalToPay)}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = AppRed)
                    }
                }

                SectionTitle("4. ASIGNACIÓN Y CLIENTE")

                AppointmentDropdown("Mecánico Asignado *", mechanicsList, Modifier.fillMaxWidth()) { selectedMechanic = it }

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nombre del Cliente *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = phone1,
                    onValueChange = { if (it.all { c -> c.isDigit() }) phone1 = it },
                    label = { Text("Teléfono *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GENERAR ORDEN DE SERVICIO", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, modifier = Modifier.padding(top = 8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentDropdown(label: String, options: List<String>, modifier: Modifier = Modifier, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selected = option
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}