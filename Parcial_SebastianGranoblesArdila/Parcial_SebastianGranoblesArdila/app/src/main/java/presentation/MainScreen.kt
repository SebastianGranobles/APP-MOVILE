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
    // ESTADOS
    val user by userViewModel.user.collectAsState()
    val appointments by userViewModel.appointments.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // COLORES LOCALES
    val myRed = Color(0xFFD32F2F)
    val myLightRed = Color(0xFFFFEBEE)
    val myGrey = Color(0xFF757575)
    val plateYellow = Color(0xFFFFD54F)

    // Redirección de seguridad
    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login_route") {
                popUpTo("main_route") { inclusive = true }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(myRed)
                        .padding(24.dp)
                ) {
                    Column {
                        Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("MOTOMAXRACING", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
                    label = { Text("Información de Cuenta") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            delay(100)
                            navController.navigate("profile_route")
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    label = { Text("Agendar Cita Taller") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            delay(100)
                            navController.navigate("appointment_route")
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = myRed) },
                    label = { Text("Cerrar Sesión", color = myRed) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            userViewModel.logout()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("MOTOMAX RACING", fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 2.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = myRed)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(myLightRed, Color.White)))
                    .padding(padding)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier.size(130.dp),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(4.dp, myRed),
                        shadowElevation = 8.dp
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(25.dp).fillMaxSize(), tint = myGrey)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(text = "BIENVENIDO", fontSize = 12.sp, color = myGrey, letterSpacing = 4.sp)
                    Text(
                        text = user?.fullName?.uppercase() ?: "PILOTO",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.width(50.dp).height(4.dp).background(myRed, RoundedCornerShape(2.dp)))

                    if (appointments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "SUS CITAS PROGRAMADAS (${appointments.size}/4):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = myRed,
                            modifier = Modifier.fillMaxWidth(0.9f).padding(bottom = 8.dp)
                        )

                        appointments.forEach { cita ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, myRed.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = myRed, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(text = "MOTOCICLETA", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                                                Text(text = "${cita.brand} (${cita.displacement})", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                            }
                                        }
                                        IconButton(onClick = { userViewModel.removeAppointment(cita.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = myRed.copy(alpha = 0.6f))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        color = plateYellow,
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, Color.Black)
                                    ) {
                                        Text(
                                            text = cita.plate,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = myGrey.copy(alpha = 0.2f))

                                    Text(text = "SERVICIO", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                                    Text(text = cita.service, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = "HORA", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                                            Text(text = cita.time, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = myRed)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = "FECHA REGISTRO", fontSize = 9.sp, color = myGrey, fontWeight = FontWeight.Bold)
                                            Text(text = cita.date, fontSize = 12.sp, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(60.dp))
                        Icon(Icons.Default.EventNote, contentDescription = null, modifier = Modifier.size(60.dp), tint = myGrey.copy(alpha = 0.3f))
                        Text("No tienes citas programadas", color = myGrey, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}