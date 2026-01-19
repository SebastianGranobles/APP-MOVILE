package com.example.parcial_sebastiangranoblesardila.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

// --- MODELOS ---
enum class AuthState { IDLE, LOADING, SUCCESS, ERROR }

data class PasswordChangeLog(val dateString: String = "")

data class User(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val city: String = "",
    val nationality: String = "",
    val lastProfileUpdateTime: Long = 0L
)

data class Appointment(
    val id: String = UUID.randomUUID().toString(),
    val plate: String = "",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val displacement: String = "",
    val mileage: String = "",
    val clientName: String = "",
    val phone1: String = "",
    val email: String = "",
    val selectedServices: List<String> = emptyList(),
    val problemDescription: String = "",
    val entryDate: String = "",
    val entryTime: String = "",
    val laborCost: Double = 0.0,
    val partsCost: Double = 0.0,
    val totalCost: Double = laborCost + partsCost,
    val paymentMethod: String = "Efectivo",
    val status: String = "Recibido",
    val mechanic: String = "Sin asignar",
    val clientId: String = "",
    val phone2: String = "",
    val address: String = ""
)

// --- VIEWMODEL ---
class UserViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    private val _finishedAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val finishedAppointments: StateFlow<List<Appointment>> = _finishedAppointments

    private val _authState = MutableStateFlow(AuthState.IDLE)
    val authState: StateFlow<AuthState> = _authState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _passwordChangeHistory = MutableStateFlow<List<PasswordChangeLog>>(emptyList())
    val passwordChangeHistory: StateFlow<List<PasswordChangeLog>> = _passwordChangeHistory

    private val _isProfileEditingLocked = MutableStateFlow(false)
    val isProfileEditingLocked: StateFlow<Boolean> = _isProfileEditingLocked

    val nationalities = listOf("Colombiana", "Venezolana", "Ecuatoriana", "Peruana", "Otra")

    // --- FUNCIONES DE AUTENTICACIÓN ---

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Por favor completa todos los campos"
            return
        }
        _authState.value = AuthState.LOADING
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = User(email = email, fullName = "Piloto MotoMax")
                    _authState.value = AuthState.SUCCESS
                } else {
                    _authState.value = AuthState.ERROR
                    _errorMessage.value = "Error: ${task.exception?.localizedMessage}"
                }
            }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Todos los campos son obligatorios"
            return
        }
        _authState.value = AuthState.LOADING
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = User(fullName = name, email = email)
                    _authState.value = AuthState.SUCCESS
                } else {
                    _authState.value = AuthState.ERROR
                    _errorMessage.value = "Error: ${task.exception?.localizedMessage}"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
        _authState.value = AuthState.IDLE
    }

    fun deleteAccount() {
        val currentUser = auth.currentUser
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = null
                _authState.value = AuthState.IDLE
            } else {
                _errorMessage.value = "No se pudo eliminar: ${task.exception?.localizedMessage}"
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.IDLE
        _errorMessage.value = null
    }

    // --- CAMBIO DE CONTRASEÑA ---

    fun changePassword(oldPass: String, newPass: String) {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null || firebaseUser.email == null) {
            _errorMessage.value = "No hay un usuario activo."
            return
        }
        if (newPass.length < 6) {
            _errorMessage.value = "La nueva contraseña debe tener al menos 6 caracteres."
            return
        }

        _authState.value = AuthState.LOADING

        val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, oldPass)

        firebaseUser.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                firebaseUser.updatePassword(newPass).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val currentDate = sdf.format(Date())
                        _passwordChangeHistory.value += PasswordChangeLog(currentDate)

                        _authState.value = AuthState.SUCCESS
                        _errorMessage.value = "Contraseña actualizada con éxito"
                    } else {
                        _errorMessage.value = "Error: ${updateTask.exception?.localizedMessage}"
                        _authState.value = AuthState.ERROR
                    }
                }
            } else {
                _errorMessage.value = "La contraseña actual es incorrecta"
                _authState.value = AuthState.ERROR
            }
        }
    }

    // --- FUNCIONES DE PERFIL ---

    fun updateUserInfo(phone: String, age: String, city: String, nationality: String) {
        _user.value = _user.value?.copy(
            phone = phone,
            age = age,
            city = city,
            nationality = nationality,
            lastProfileUpdateTime = System.currentTimeMillis()
        )
        _isProfileEditingLocked.value = true
    }

    // --- FUNCIONES DE CITAS ---
    fun addAppointment(app: Appointment): Boolean {
        val finalApp = app.copy(totalCost = app.laborCost + app.partsCost)
        _appointments.value += finalApp
        return true
    }

    fun finishAppointment(id: String) {
        val app = _appointments.value.find { it.id == id }
        app?.let {
            _appointments.value = _appointments.value.filter { it.id != id }
            _finishedAppointments.value += it.copy(status = "Listo")
        }
    }

    fun removeAppointment(id: String) {
        _appointments.value = _appointments.value.filter { it.id != id }
    }

    fun removeFinishedAppointment(id: String) {
        _finishedAppointments.value = _finishedAppointments.value.filter { it.id != id }
    }
}