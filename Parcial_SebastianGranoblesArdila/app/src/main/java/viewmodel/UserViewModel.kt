package com.example.parcial_sebastiangranoblesardila.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Estructura de datos del usuario (sin cambios)
data class UserData(
    val fullName: String,
    val email: String,
    val profileImageUri: Uri?,
    val phone: String = "",
    val age: String = "",
    val city: String = "",
    val nationality: String = ""
)

// ⭐ NUEVA ESTRUCTURA: Para guardar la información de cada cambio de contraseña
data class PasswordChangeLog(
    val userEmail: String,
    val oldPass: String, // En una app real, esto sería un hash
    val newPass: String, // En una app real, esto también sería un hash
    val timestamp: String // La fecha y hora del cambio
)

class UserViewModel : ViewModel() {

    // --- ESTADO PÚBLICO ---
    private val _userState = mutableStateOf<UserData?>(null)
    val userState: State<UserData?> = _userState

    var isLoggedIn by mutableStateOf(false)
        private set

    // ⭐ NUEVA LISTA: Aquí almacenaremos el historial de cambios. Es observable.
    val passwordChangeHistory = mutableStateListOf<PasswordChangeLog>()

    // Lista de nacionalidades (código previo)
    val nationalities: List<String> = listOf(
        "Afgana", "Albanesa", "Alemana", "Andorrana", "Angoleña", "Argelina", "Argentina",
        "Australiana", "Austriaca", "Bangladesí", "Belga", "Bielorrusa", "Boliviana",
        "Bosnia", "Brasileña", "Búlgara", "Canadiense", "Chilena", "China", "Chipriota",
        "Colombiana", "Costarricense", "Croata", "Cubana", "Danesa", "Dominicana",
        "Ecuatoriana", "Egipcia", "Salvadoreña", "Eslovaca", "Eslovena", "Española",
        "Estadounidense", "Estonia", "Filipina", "Finlandesa", "Francesa", "Griega",
        "Guatemalteca", "Hondureña", "Húngara", "India", "Indonesa", "Irlandesa",
        "Islandesa", "Israelí", "Italiana", "Japonesa", "Letona", "Lituana", "Luxemburguesa",
        "Marroquí", "Mexicana", "Nicaragüense", "Noruega", "Neozelandesa", "Panameña",
        "Paraguaya", "Peruana", "Polaca", "Portuguesa", "Puertorriqueña", "Británica",
        "Checa", "Rumana", "Rusa", "Serbia", "Sueca", "Suiza", "Tailandesa", "Turca",
        "Ucraniana", "Uruguaya", "Venezolana", "Vietnamita"
    )

    // --- DATOS HARCODEADOS ---
    private val hardcodedEmail = "sebastiangranobles@hotmail.com"
    private var hardcodedPass by mutableStateOf("llulucasia2025")
    private val hardcodedFullName = "Sebastian Granobles Ardila"

    // --- LÓGICA DE NEGOCIO ---

    fun onLogin(email: String, pass: String): Boolean {
        return if (email == hardcodedEmail && pass == hardcodedPass) {
            _userState.value = UserData(
                fullName = hardcodedFullName,
                email = hardcodedEmail,
                profileImageUri = null
            )
            isLoggedIn = true
            true
        } else {
            isLoggedIn = false
            false
        }
    }

    fun logout() {
        isLoggedIn = false
        _userState.value = null
    }

    fun onProfileImageChange(uri: Uri?) {
        _userState.value = _userState.value?.copy(profileImageUri = uri)
    }

    fun updateUserInfo(phone: String, age: String, city: String, nationality: String) {
        _userState.value = _userState.value?.copy(
            phone = phone,
            age = age,
            city = city,
            nationality = nationality
        )
    }

    /**
     * ⭐ FUNCIÓN MODIFICADA: Ahora guarda un registro del cambio.
     */
    fun changePassword(currentPass: String, newPass: String): Boolean {
        if (currentPass == this.hardcodedPass) {
            val oldPassword = this.hardcodedPass // Guardamos la contraseña vieja
            this.hardcodedPass = newPass // Actualizamos a la nueva

            // Creamos una entrada para el log
            val logEntry = PasswordChangeLog(
                userEmail = this.hardcodedEmail,
                oldPass = oldPassword,
                newPass = newPass,
                timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            )
            // Añadimos la entrada a nuestra lista de historial
            passwordChangeHistory.add(logEntry)

            return true // Éxito
        }
        return false // Contraseña actual incorrecta
    }
}
