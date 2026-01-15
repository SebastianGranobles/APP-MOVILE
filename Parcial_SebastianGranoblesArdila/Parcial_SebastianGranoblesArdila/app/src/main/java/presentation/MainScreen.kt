package com.example.parcial_sebastiangranoblesardila.presentation

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
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.Appointment
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
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

    // Estado para la Hoja Inferior (Citas Terminadas)
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
            navController.navigate("login_route") { popUpTo("main_route") { inclusive = true } }
        }
    }

    // --- HOJA INFERIOR (MODAL BOTTOM SHEET) PARA CITAS TERMINADAS ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = myRed) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "CITAS TERMINADAS DEL DÍA DE HOY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
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
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            showBottomSheet = true // Abre la hoja sin quitar el Dashboard
                        }
                    },
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
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(25.dp).fillMaxSize(), tint = myGrey)
                    }
                    Text(text = user?.fullName?.uppercase() ?: "PILOTO", fontSize = 26.sp, fontWeight = FontWeight.Black)

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "ÓRDENES ACTIVAS EN TALLER (${appointments.size}/4)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = myRed,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )

                    if (appointments.isNotEmpty()) {
                        appointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, myRed, plateYellow, myGrey, isHistory = false)
                        }
                    } else {
                        Text("No hay motos en reparación", color = myGrey, fontSize = 13.sp, modifier = Modifier.padding(30.dp))
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
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if(isHistory) Color.LightGray else myRed.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CLIENTE: ${cita.clientName.uppercase()}", fontSize = 10.sp, color = myRed, fontWeight = FontWeight.Bold)
                    Text(text = "${cita.brand} ${cita.model}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Black)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(12.dp), tint = myGrey)
                        Text(text = " ${cita.mileage} KM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = myGrey)
                        Text(text = " ${cita.year}", fontSize = 11.sp, color = myGrey)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.SettingsInputComponent, contentDescription = null, modifier = Modifier.size(12.dp), tint = myGrey)
                        Text(text = " ${cita.displacement}", fontSize = 11.sp, color = myGrey)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val (statusText, statusColor, statusIcon) = when(cita.status) {
                        "En diagnóstico" -> Triple("DIAGNÓSTICO", Color(0xFFFB8C00), Icons.Default.Search)
                        "En reparación" -> Triple("REPARACIÓN", Color(0xFF1976D2), Icons.Default.Build)
                        "Listo" -> Triple("LISTO", Color(0xFF388E3C), Icons.Default.CheckCircle)
                        "Entregado" -> Triple("ENTREGADO", Color.Gray, Icons.Default.DoneAll)
                        else -> Triple("RECIBIDO", Color(0xFF757575), Icons.Default.AccessTime)
                    }

                    Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, statusColor)) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(statusIcon, contentDescription = null, modifier = Modifier.size(12.dp), tint = statusColor)
                            Spacer(Modifier.width(4.dp))
                            Text(text = statusText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusColor)
                        }
                    }

                    if (!isHistory) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { userViewModel.finishAppointment(cita.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("FINALIZAR", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Surface(color = plateYellow, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color.Black)) {
                        Text(text = cita.plate, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(text = "TOTAL COMPLETO", fontSize = 8.sp, color = myGrey, fontWeight = FontWeight.Bold)
                    Text(text = currencyFormat.format(cita.totalCost), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Text(text = "Pago: ${cita.paymentMethod}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF25D366))
                        Text(text = " ${cita.phone1}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Text(text = cita.email, fontSize = 11.sp, color = myGrey)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            Text(text = "SERVICIOS SOLICITADOS", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
            Text(text = cita.selectedServices.joinToString(", ").uppercase(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = myRed)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "SÍNTOMAS / PROBLEMA", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
            Text(text = cita.problemDescription.ifEmpty { "Sin descripción" }, fontSize = 13.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "INGRESO", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                    Text(text = "${cita.entryDate} ${cita.entryTime}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                IconButton(onClick = {
                    if (isHistory) userViewModel.removeFinishedAppointment(cita.id)
                    else userViewModel.removeAppointment(cita.id)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = myRed.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}