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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()
    val appointments by userViewModel.appointments.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val myRed = Color(0xFFD32F2F)
    val myLightRed = Color(0xFFFFEBEE)
    val myGrey = Color(0xFF757575)
    val plateYellow = Color(0xFFFFD54F)

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login_route") { popUpTo("main_route") { inclusive = true } }
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

                    if (appointments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        appointments.forEach { cita ->
                            Card(
                                modifier = Modifier.fillMaxWidth(0.95f).padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(6.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, myRed.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "CLIENTE: ${cita.clientName.uppercase()}", fontSize = 10.sp, color = myRed, fontWeight = FontWeight.Bold)
                                            Text(text = "${cita.brand} (${cita.displacement})", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                        }

                                        // INDICADOR DE ESTADO (RF-12)
                                        val (statusText, statusColor, statusIcon) = when(cita.status) {
                                            "En Proceso" -> Triple("EN PROCESO", Color(0xFF1976D2), Icons.Default.Build)
                                            "Lista para Entrega" -> Triple("LISTA", Color(0xFF388E3C), Icons.Default.CheckCircle)
                                            else -> Triple("EN ESPERA", Color(0xFF9E9E9E), Icons.Default.AccessTime)
                                        }

                                        Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, statusColor)) {
                                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(statusIcon, contentDescription = null, modifier = Modifier.size(12.dp), tint = statusColor)
                                                Spacer(Modifier.width(4.dp))
                                                Text(text = statusText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusColor)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(color = plateYellow, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color.Black)) {
                                        Text(text = cita.plate, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                                    Text(text = "SERVICIO TÉCNICO", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                                    Text(text = cita.service.uppercase(), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = myRed)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "DIAGNÓSTICO TÉCNICO", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                                    Text(text = cita.diagnosis.ifEmpty { "Sin observaciones" }, fontSize = 13.sp, color = Color.DarkGray)

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text(text = "HORA INGRESO", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                                            Text(text = cita.time, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                        IconButton(onClick = { userViewModel.removeAppointment(cita.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = myRed.copy(alpha = 0.4f))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text("No tienes citas programadas", color = myGrey, modifier = Modifier.padding(top = 40.dp))
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
