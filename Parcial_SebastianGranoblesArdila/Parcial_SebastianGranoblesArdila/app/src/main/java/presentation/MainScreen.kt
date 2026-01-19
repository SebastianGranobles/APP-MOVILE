package com.example.parcial_sebastiangranoblesardila.presentation

import android.icu.text.NumberFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()
    val appointments by userViewModel.appointments.collectAsState()
    val finishedAppointments by userViewModel.finishedAppointments.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val myRed = Color(0xFFD32F2F)
    val myLightRed = Color(0xFFFFEBEE)
    val myGrey = Color(0xFF757575)
    val plateYellow = Color(0xFFFFD54F)
    val myBlue = Color(0xFF1976D2)

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login_route") {
                popUpTo("main_route") { inclusive = true }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = myRed) }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp)) {
                Text(
                    text = "HISTORIAL DE CITAS TERMINADAS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                )
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    if (finishedAppointments.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No hay registros terminados hoy.", color = Color.Gray)
                        }
                    } else {
                        finishedAppointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, myRed, plateYellow, myGrey, isHistory = true)
                        }
                    }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(0.dp)) {
                Box(modifier = Modifier.fillMaxWidth().background(myRed).padding(24.dp)) {
                    Column {
                        Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Text("MOTOMAXRACING", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    label = { Text("Agendar Cita Taller") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close(); delay(100); navController.navigate("appointment_route") } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null, tint = myBlue) },
                    label = { Text("CITAS TERMINADAS", color = myBlue, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close(); showBottomSheet = true } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = myRed) },
                    label = { Text("Cerrar Sesión", color = myRed) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close(); userViewModel.logout() } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("MOTOMAX RACING", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = myRed)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(myLightRed, Color.White))).padding(padding)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(modifier = Modifier.size(130.dp), shape = CircleShape, color = Color.White, border = BorderStroke(4.dp, myRed), shadowElevation = 8.dp) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(25.dp).fillMaxSize(), tint = myGrey)
                    }
                    Text(text = user?.fullName?.uppercase() ?: "PILOTO", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "ÓRDENES ACTIVAS EN TALLER (${appointments.size}/4)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = myRed, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))

                    if (appointments.isNotEmpty()) {
                        appointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, myRed, plateYellow, myGrey, isHistory = false)
                        }
                    } else {
                        Text(text = "No hay motos en reparación", color = myGrey, fontSize = 13.sp, modifier = Modifier.padding(30.dp))
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    cita: Appointment,
    userViewModel: UserViewModel,
    currencyFormat: NumberFormat,
    myRed: Color,
    plateYellow: Color,
    myGrey: Color,
    isHistory: Boolean
) {
    val servicePrices = mapOf(
        "Cambio de Aceite" to 60000.0,
        "Ajuste General" to 140000.0,
        "Kit de arrastre" to 70000.0,
        "Batería" to 45000.0,
        "Lavado" to 15000.0,
        "Eléctrico" to 60000.0
    )

    var showFinishConfirm by remember { mutableStateOf(false) }

    if (showFinishConfirm) {
        AlertDialog(
            onDismissRequest = { showFinishConfirm = false },
            title = { Text("Finalizar Servicio") },
            text = { Text("¿Deseas marcar el servicio de la placa ${cita.plate} como terminado?") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishConfirm = false
                    userViewModel.finishAppointment(cita.id)
                }) {
                    Text("SÍ, FINALIZAR", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishConfirm = false }) { Text("CANCELAR") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isHistory) Color.LightGray else myRed.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. CABECERA
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CLIENTE: ${cita.clientName.uppercase()}", fontSize = 10.sp, color = myRed, fontWeight = FontWeight.Bold)
                    Text(text = "${cita.brand} ${cita.model}".uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Black)

                    // Datos técnicos restaurados
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.SettingsInputComponent, null, Modifier.size(12.dp), tint = myGrey)
                        Text(" ${cita.displacement} CC", fontSize = 11.sp, color = Color.DarkGray)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Speed, null, Modifier.size(12.dp), tint = myGrey)
                        Text(" ${cita.mileage} KM", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.CalendarToday, null, Modifier.size(12.dp), tint = myGrey)
                        Text(" ${cita.year}", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
                IconButton(onClick = {
                    if (isHistory) userViewModel.removeFinishedAppointment(cita.id)
                    else userViewModel.removeAppointment(cita.id)
                }) {
                    Icon(Icons.Default.Delete, null, tint = myRed.copy(alpha = 0.4f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. PLACA, ESTADO Y MECÁNICO
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(color = plateYellow, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color.Black)) {
                    Text(text = cita.plate, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "MECÁNICO ASIGNADO", fontSize = 8.sp, color = myGrey, fontWeight = FontWeight.Bold)
                    Text(text = cita.mechanic.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1976D2))
                }
            }

            // 3. FECHA Y HORA DE SERVICIO
            Surface(
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                color = Color(0xFFF9F9F9),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.5.dp, Color.LightGray)
            ) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, Modifier.size(14.dp), tint = myRed)
                    Text(" INGRESO: ${cita.entryDate} ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.Schedule, null, Modifier.size(14.dp), tint = myRed)
                    Text(" HORA: ${cita.entryTime}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // BOTÓN FINALIZAR (Solo si no es historial)
            if (!isHistory) {
                Button(
                    onClick = { showFinishConfirm = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("FINALIZAR SERVICIO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            // 4. DESGLOSE DE COSTOS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "MANO DE OBRA", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                    Text(text = currencyFormat.format(cita.laborCost), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "REPUESTOS/SERVICIOS", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                    Text(text = currencyFormat.format(cita.partsCost), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. GUÍA DE SERVICIOS
            Text(text = "SERVICIOS SELECCIONADOS (GUÍA)", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(8.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    if (cita.selectedServices.isEmpty()) {
                        Text("Solo mano de obra", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        cita.selectedServices.forEach { servicio ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "• $servicio", fontSize = 11.sp, color = Color.DarkGray)
                                Text(text = currencyFormat.format(servicePrices[servicio] ?: 0.0), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 6. TOTAL FINAL
            Row(
                modifier = Modifier.fillMaxWidth().background(if (isHistory) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), RoundedCornerShape(8.dp)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "TOTAL A PAGAR", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                Text(text = currencyFormat.format(cita.totalCost), fontSize = 20.sp, fontWeight = FontWeight.Black, color = if (isHistory) Color(0xFF2E7D32) else myRed)
            }
        }
    }
}