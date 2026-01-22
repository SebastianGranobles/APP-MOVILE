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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import com.google.firebase.messaging.FirebaseMessaging
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

    val localeCo = Locale.Builder().setLanguage("es").setRegion("CO").build()
    val currencyFormat = NumberFormat.getCurrencyInstance(localeCo).apply {
        maximumFractionDigits = 0
    }

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login_route") {
                popUpTo("main_route") { inclusive = true }
            }
        } else {
            // 1. Iniciar escucha de citas
            userViewModel.startAppointmentsRealtimeListener()

            // 2. SUSCRIBIR AL TEMA DE NOTIFICACIONES GLOBALES
            FirebaseMessaging.getInstance().subscribeToTopic("taller")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Suscripción exitosa al tema: taller")
                    }
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
                            Text("No hay registros terminados.", color = Color.Gray)
                        }
                    } else {
                        finishedAppointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, myRed, plateYellow, myGrey, myBlue, isHistory = true, userRole = user?.role ?: "ASESOR")
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

                if (user?.role == "ADMIN" || user?.role == "ASESOR") {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Build, contentDescription = null) },
                        label = { Text("Agendar Cita Taller") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close(); delay(100); navController.navigate("appointment_route") } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

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
                    Text(text = "CARGO: ${user?.role ?: "ASESOR"}", fontSize = 14.sp, color = myRed, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "ÓRDENES ACTIVAS EN TALLER", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = myRed, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))

                    if (appointments.isNotEmpty()) {
                        appointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, myRed, plateYellow, myGrey, myBlue, false, user?.role ?: "ASESOR")
                        }
                    } else {
                        Text(text = "No hay motos en reparación", color = myGrey, fontSize = 13.sp, modifier = Modifier.padding(30.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate("profile_route") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("AJUSTES DE PERFIL", fontWeight = FontWeight.Bold)
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
    myBlue: Color,
    isHistory: Boolean,
    userRole: String
) {
    var showFinishConfirm by remember { mutableStateOf(false) }

    if (showFinishConfirm) {
        AlertDialog(
            onDismissRequest = { showFinishConfirm = false },
            title = { Text("Finalizar Servicio") },
            text = { Text("¿Deseas marcar la placa ${cita.plate} como terminada?") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishConfirm = false
                    userViewModel.finishAppointment(cita.id)
                }) {
                    Text("SÍ, FINALIZAR", color = myBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showFinishConfirm = false }) { Text("CANCELAR") } }
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
            // ENCABEZADO
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CLIENTE: ${cita.clientName.uppercase()}", fontSize = 10.sp, color = myRed, fontWeight = FontWeight.Bold)
                    Text(text = "${cita.brand} ${cita.model}".uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
                if (userRole == "ADMIN") {
                    IconButton(onClick = {
                        if (isHistory) userViewModel.removeFinishedAppointment(cita.id)
                        else userViewModel.removeAppointment(cita.id)
                    }) {
                        Icon(Icons.Default.Delete, null, tint = myRed.copy(alpha = 0.4f))
                    }
                }
            }

            // INFO TÉCNICA (CILINDRAJE, KM, AÑO)
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconInfo(Icons.Default.SettingsInputComponent, "${cita.displacement} CC", myGrey)
                IconInfo(Icons.Default.Speed, "${cita.mileage} KM", myGrey)
                IconInfo(Icons.Default.Event, cita.year, myGrey)
            }

            // PLACA, TELÉFONO Y MECÁNICO
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.6f)) {
                    Surface(
                        color = plateYellow,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text(
                            text = cita.plate.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Phone, null, tint = myBlue, modifier = Modifier.size(14.dp))
                    Text(text = cita.phone1, fontSize = 13.sp, color = Color.DarkGray)
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(0.4f)) {
                    Text(text = "MECÁNICO", fontSize = 9.sp, color = myGrey)
                    Text(text = cita.mechanic.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = myBlue, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // GUÍA DE PRODUCTOS / SERVICIOS
            if (cita.selectedServices.isNotEmpty()) {
                Text(text = "SERVICIOS Y REPUESTOS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = myGrey)
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    cita.selectedServices.forEach { servicio ->
                        Text(text = "• $servicio", fontSize = 11.sp, color = Color.Black)
                    }
                }
            }

            // BOTÓN FINALIZAR (AZUL)
            if (!isHistory && (userRole == "ADMIN" || userRole == "MECANICO")) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showFinishConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = myBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("FINALIZAR TRABAJO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            // DESGLOSE DE COSTOS
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "MANO DE OBRA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = myGrey)
                    Text(text = currencyFormat.format(cita.laborCost), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "REPUESTOS/INSUMOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = myGrey)
                    Text(text = currencyFormat.format(cita.partsCost), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "TOTAL A PAGAR", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        Text(
                            text = currencyFormat.format(cita.totalCost),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isHistory) Color(0xFF2E7D32) else myRed
                        )
                    }
                    Text(text = cita.entryDate, color = myGrey, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun IconInfo(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(15.dp), tint = color)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
    }
}