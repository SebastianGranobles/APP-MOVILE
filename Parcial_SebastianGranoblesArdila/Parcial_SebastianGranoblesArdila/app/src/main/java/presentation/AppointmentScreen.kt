package com.example.parcial_sebastiangranoblesardila.presentation

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
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.Appointment
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavController, userViewModel: UserViewModel) {
    val AppRed = Color(0xFFD32F2F)
    val scrollState = rememberScrollState()
    val plateRegex = remember { Regex("^[A-Z]{3}[0-9]{2}[A-Z]$") }

    // --- ESTADOS DEL FORMULARIO ---
    // 1. Datos de la Moto
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var modelLine by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var displacement by remember { mutableStateOf("") }
    var motoType by remember { mutableStateOf("Mecánica") } // RadioButton
    var motoColor by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }

    // 2. Datos del Cliente
    var clientName by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var phone1 by remember { mutableStateOf("") }
    var phone2 by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var clientAddress by remember { mutableStateOf("") }

    // 3. Servicios
    val serviceOptions = listOf(
        "Cambio de aceite", "Ajuste general", "Revisión de frenos",
        "Kit de arrastre", "Batería", "Lavado", "Diagnóstico general", "Eléctrico"
    )
    val selectedServices = remember { mutableStateListOf<String>() }
    var otherService by remember { mutableStateOf("") }
    var problemDescription by remember { mutableStateOf("") }

    // 4. Control y Liquidación
    var estimatedDelivery by remember { mutableStateOf("") }
    var laborCost by remember { mutableStateOf("") }
    var partsCost by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Efectivo") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ORDEN DE ENTRADA TALLER", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECCIÓN 1: DATOS DE LA MOTO ---
            SectionTitle("1. DATOS DE LA MOTOCICLETA")
            OutlinedTextField(
                value = plate,
                onValueChange = { if (it.length <= 6) plate = it.uppercase() },
                label = { Text("Placa (Ej: ABC12D) *") },
                modifier = Modifier.fillMaxWidth(),
                isError = plate.isNotEmpty() && !plate.matches(plateRegex)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppointmentDropdown("Marca", listOf("Yamaha", "Honda", "Suzuki", "AKT", "Bajaj", "KTM", "TVS"), Modifier.weight(1f)) { brand = it }
                OutlinedTextField(value = modelLine, onValueChange = { modelLine = it }, label = { Text("Modelo/Línea") }, modifier = Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Año") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = displacement, onValueChange = { displacement = it }, label = { Text("Cilindraje") }, modifier = Modifier.weight(1f))
            }

            Text("Tipo de Moto:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = motoType == "Mecánica", onClick = { motoType = "Mecánica" })
                Text("Mecánica")
                Spacer(Modifier.width(16.dp))
                RadioButton(selected = motoType == "Automática", onClick = { motoType = "Automática" })
                Text("Automática")
            }

            OutlinedTextField(value = motoColor, onValueChange = { motoColor = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mileage, onValueChange = { mileage = it }, label = { Text("Kilometraje Actual") }, modifier = Modifier.fillMaxWidth())

            // --- SECCIÓN 2: DATOS DEL CLIENTE ---
            SectionTitle("2. DATOS DEL DUEÑO")
            OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Nombre Completo *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = clientId, onValueChange = { clientId = it }, label = { Text("Documento / Cédula") }, modifier = Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = phone1, onValueChange = { phone1 = it }, label = { Text("Teléfono 1") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = phone2, onValueChange = { phone2 = it }, label = { Text("WhatsApp") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = clientEmail, onValueChange = { clientEmail = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = clientAddress, onValueChange = { clientAddress = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

            // --- SECCIÓN 3: SERVICIOS Y SÍNTOMAS ---
            SectionTitle("3. SERVICIOS Y DIAGNÓSTICO")
            serviceOptions.forEach { service ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedServices.contains(service),
                        onCheckedChange = { if (it) selectedServices.add(service) else selectedServices.remove(service) }
                    )
                    Text(service, fontSize = 14.sp)
                }
            }
            OutlinedTextField(value = otherService, onValueChange = { otherService = it }, label = { Text("Otro servicio...") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = problemDescription,
                onValueChange = { problemDescription = it },
                label = { Text("Descripción detallada del problema") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Ej: Hace un ruido raro al acelerar...") }
            )

            // --- SECCIÓN 4: CONTROL Y COSTOS ---
            SectionTitle("4. CONTROL Y LIQUIDACIÓN")
            OutlinedTextField(value = estimatedDelivery, onValueChange = { estimatedDelivery = it }, label = { Text("Fecha estimada entrega") }, modifier = Modifier.fillMaxWidth())

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = laborCost, onValueChange = { laborCost = it }, label = { Text("Mano de Obra $") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = partsCost, onValueChange = { partsCost = it }, label = { Text("Repuestos $") }, modifier = Modifier.weight(1f))
            }

            AppointmentDropdown("Método de Pago", listOf("Efectivo", "Transferencia", "Tarjeta"), Modifier.fillMaxWidth()) { paymentMethod = it }

            Spacer(Modifier.height(24.dp))

            // --- BOTÓN DE GUARDADO ---
            Button(
                onClick = {
                    val labor = laborCost.toDoubleOrNull() ?: 0.0
                    val parts = partsCost.toDoubleOrNull() ?: 0.0

                    val newApp = Appointment(
                        plate = plate,
                        brand = brand,
                        model = modelLine,
                        year = year,
                        displacement = displacement,
                        type = motoType,
                        color = motoColor,
                        mileage = mileage,
                        clientName = clientName,
                        clientId = clientId,
                        phone1 = phone1,
                        phone2 = phone2,
                        email = clientEmail,
                        address = clientAddress,
                        selectedServices = selectedServices.toList() + if(otherService.isNotEmpty()) listOf(otherService) else emptyList(),
                        problemDescription = problemDescription,
                        entryDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        entryTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                        estimatedDelivery = estimatedDelivery,
                        laborCost = labor,
                        partsCost = parts,
                        totalCost = labor + parts,
                        paymentMethod = paymentMethod
                    )

                    if (userViewModel.addAppointment(newApp)) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(12.dp),
                enabled = plate.matches(plateRegex) && clientName.isNotEmpty()
            ) {
                Text("GENERAR ORDEN DE ENTRADA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.DarkGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDropdown(label: String, options: List<String>, modifier: Modifier = Modifier, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
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