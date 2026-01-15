package com.example.parcial_sebastiangranoblesardila.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_sebastiangranoblesardila.model.PasswordChangeLog
import com.example.parcial_sebastiangranoblesardila.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Modelo de datos profesional para Gestión de Taller
data class Appointment(
    val id: String = java.util.UUID.randomUUID().toString(),
    // 1. Datos de la Moto
    val plate: String,
    val brand: String,
    val model: String,
    val year: String,
    val displacement: String,
    val type: String, // Automática / Mecánica
    val color: String,
    val mileage: String,
    // 2. Datos del Cliente
    val clientName: String,
    val clientId: String,
    val phone1: String,
    val phone2: String, // WhatsApp
    val email: String,
    val address: String,
    // 3. Servicios
    val selectedServices: List<String>,
    val problemDescription: String,
    // 4. Control del Servicio
    val entryDate: String,
    val entryTime: String,
    val estimatedDelivery: String,
    val status: String = "Recibido", // Recibido, En diagnóstico, En reparación, Listo, Entregado
    // 5. Liquidación
    val laborCost: Double = 0.0,
    val partsCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val paymentMethod: String = "Efectivo",
    val isPaid: Boolean = false,
    // 6. Evidencia (Rutas de fotos)
    val photoEntry: String? = null,
    val photoDamage: String? = null,
    val photoFinish: String? = null,
    // 7. Observaciones
    val mechanicNotes: String = ""
)

enum class AuthState { IDLE, LOADING, SUCCESS, ERROR }

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    private val _authState = MutableStateFlow(AuthState.IDLE)
    val authState = _authState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _passwordChangeHistory = MutableStateFlow<List<PasswordChangeLog>>(emptyList())
    val passwordChangeHistory = _passwordChangeHistory.asStateFlow()

    val nationalities = listOf("Colombiana", "Argentina", "Chilena", "Mexicana", "Española")

    val isProfileEditingLocked: Boolean
        get() {
            val lastUpdate = _user.value?.lastProfileUpdateTime ?: 0L
            if (lastUpdate == 0L) return false
            return (System.currentTimeMillis() - lastUpdate) < 3 * 60 * 60 * 1000
        }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                loadUserData(firebaseUser.uid)
                _authState.value = AuthState.SUCCESS
            } else {
                _user.value = null
                _appointments.value = emptyList()
                _authState.value = AuthState.IDLE
            }
        }
    }

    // --- FUNCIONES DE AUTENTICACIÓN ---

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.LOADING
        try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            _authState.value = AuthState.SUCCESS
        } catch (e: Exception) {
            _errorMessage.value = "Correo o contraseña incorrectos"
            _authState.value = AuthState.ERROR
        }
    }

    fun register(fullName: String, email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.LOADING
        try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val newUser = User(uid = firebaseUser.uid, fullName = fullName.trim(), email = email.trim())
                usersCollection.document(firebaseUser.uid).set(newUser).await()
                _user.value = newUser
                _authState.value = AuthState.SUCCESS
            }
        } catch (e: Exception) {
            _errorMessage.value = e.localizedMessage ?: "Error en el registro"
            _authState.value = AuthState.ERROR
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.IDLE
        _errorMessage.value = null
    }

    fun logout() {
        auth.signOut()
        _user.value = null
        _appointments.value = emptyList()
        resetAuthState()
    }

    // --- FUNCIONES DE CITAS (Actualizado para el nuevo formulario) ---

    fun addAppointment(newAppointment: Appointment): Boolean {
        val currentList = _appointments.value
        if (currentList.size >= 4) {
            _errorMessage.value = "Límite: Máximo 4 órdenes permitidas."
            return false
        }

        // Podrías agregar aquí lógica para guardar en Firestore si lo deseas
        _appointments.value = currentList + newAppointment
        return true
    }

    fun removeAppointment(id: String) {
        _appointments.value = _appointments.value.filter { it.id != id }
    }

    // --- OTRAS FUNCIONES ---

    fun updateUserInfo(phone: String, age: String, city: String, nationality: String) = viewModelScope.launch {
        val currentUser = _user.value ?: return@launch
        val updatedUser = currentUser.copy(phone = phone, age = age, city = city, nationality = nationality, lastProfileUpdateTime = System.currentTimeMillis())
        usersCollection.document(currentUser.uid).set(updatedUser, SetOptions.merge()).await()
        _user.value = updatedUser
    }

    fun changePassword(currentPass: String, newPass: String) = viewModelScope.launch {
        _authState.value = AuthState.LOADING
        val firebaseUser = auth.currentUser ?: return@launch
        try {
            val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, currentPass)
            firebaseUser.reauthenticate(credential).await()
            firebaseUser.updatePassword(newPass).await()
            _authState.value = AuthState.SUCCESS
        } catch (e: Exception) {
            _errorMessage.value = "Error al cambiar contraseña"
            _authState.value = AuthState.ERROR
        }
    }

    fun deleteAccount() = viewModelScope.launch {
        val firebaseUser = auth.currentUser ?: return@launch
        usersCollection.document(firebaseUser.uid).delete().await()
        firebaseUser.delete().await()
    }

    private fun loadUserData(uid: String) = viewModelScope.launch {
        try {
            val document = usersCollection.document(uid).get().await()
            _user.value = document.toObject(User::class.java)
        } catch (e: Exception) { }
    }

    // Soporte para Fotos
    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("MOTO_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    // --- AGREGAR ESTO AL USERVIEWMODEL ---

    private val _finishedAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val finishedAppointments = _finishedAppointments.asStateFlow()

    fun finishAppointment(id: String) {
        val app = _appointments.value.find { it.id == id }
        if (app != null) {
            // Lo agregamos a la lista de terminados con estado actualizado
            _finishedAppointments.value = _finishedAppointments.value + app.copy(status = "Entregado")
            // Lo eliminamos de la lista activa (esto libera el cupo de las 4 citas)
            _appointments.value = _appointments.value.filter { it.id != id }
        }
    }

    fun removeFinishedAppointment(id: String) {
        _finishedAppointments.value = _finishedAppointments.value.filter { it.id != id }
    }
}