package com.example.parcial_sebastiangranoblesardila.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_sebastiangranoblesardila.model.PasswordChangeLog
import com.example.parcial_sebastiangranoblesardila.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Modelo de datos para las Citas (Local)
data class Appointment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val brand: String,
    val plate: String,
    val displacement: String,
    val service: String,
    val time: String,
    val date: String
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

    val nationalities = listOf("Colombiana", "Argentina", "Boliviana", "Brasileña", "Chilena", "Ecuatoriana", "Española", "Mexicana", "Venezolana")

    // --- LÓGICA DE BLOQUEO DE PERFIL ---
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
                if (_user.value == null) loadUserData(firebaseUser.uid)
                _authState.value = AuthState.SUCCESS
            } else {
                _user.value = null
                _appointments.value = emptyList()
                _authState.value = AuthState.IDLE
            }
        }
    }

    // --- LÓGICA DE CITAS (MODIFICADA) ---

    /**
     * Intenta agregar una cita.
     * Retorna true si se agregó con éxito, false si rompe alguna validación.
     */
    fun addAppointment(brand: String, plate: String, displacement: String, service: String, time: String): Boolean {
        val currentList = _appointments.value

        // 1. Validar límite de 4 citas
        if (currentList.size >= 4) {
            _errorMessage.value = "Límite alcanzado: Máximo 4 citas permitidas."
            return false
        }

        // 2. Validar que no se repita el horario
        if (currentList.any { it.time == time }) {
            _errorMessage.value = "Conflicto de horario: Ya tienes una cita a las $time."
            return false
        }

        // 3. Crear y agregar la cita
        val newAppointment = Appointment(
            brand = brand,
            plate = plate.uppercase(),
            displacement = displacement,
            service = service,
            time = time,
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )

        _appointments.value = currentList + newAppointment
        _errorMessage.value = null // Limpiar errores previos
        return true
    }

    /**
     * Elimina una cita específica por su ID
     */
    fun removeAppointment(id: String) {
        _appointments.value = _appointments.value.filter { it.id != id }
    }

    // --- AUTENTICACIÓN ---
    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.LOADING
        try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
        } catch (e: Exception) {
            _errorMessage.value = "Correo o clave incorrectos."
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
            _errorMessage.value = e.localizedMessage ?: "Error en el registro."
            _authState.value = AuthState.ERROR
        }
    }

    fun logout() {
        auth.signOut()
        resetAuthState()
    }

    fun resetAuthState() {
        _authState.value = AuthState.IDLE
        _errorMessage.value = null
    }

    // --- PERFIL Y SEGURIDAD ---
    fun updateUserInfo(phone: String, age: String, city: String, nationality: String) {
        val currentUser = _user.value ?: return
        val updatedUser = currentUser.copy(phone = phone, age = age, city = city, nationality = nationality, lastProfileUpdateTime = System.currentTimeMillis())
        _user.value = updatedUser
        viewModelScope.launch {
            try { usersCollection.document(currentUser.uid).set(updatedUser, SetOptions.merge()).await() }
            catch (e: Exception) { _errorMessage.value = "Guardado localmente." }
        }
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
            _errorMessage.value = "Error al cambiar contraseña."
            _authState.value = AuthState.ERROR
        }
    }

    fun deleteAccount() = viewModelScope.launch {
        val firebaseUser = auth.currentUser ?: return@launch
        try {
            usersCollection.document(firebaseUser.uid).delete().await()
            firebaseUser.delete().await()
            _user.value = null
        } catch (e: Exception) { _errorMessage.value = "Error al eliminar cuenta." }
    }

    private fun loadUserData(uid: String) = viewModelScope.launch {
        try {
            val document = usersCollection.document(uid).get().await()
            _user.value = document.toObject(User::class.java)
        } catch (e: Exception) { }
    }
}
