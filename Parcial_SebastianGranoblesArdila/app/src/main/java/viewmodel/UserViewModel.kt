package com.example.parcial_sebastiangranoblesardila.presentation.viewmodel // <-- ARREGLO #1: Paquete corregido

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Estructura de datos para el usuario
data class UserData(
    val fullName: String = "", // Cambiado a fullName para ser más claro
    val profileImageUri: Uri? = null
)

class UserViewModel : ViewModel() {

    // --- ESTADO DEL USUARIO ---
    private val _userState = mutableStateOf(UserData())
    val userState: State<UserData> = _userState

    // --- ESTADO DE LA SESIÓN (LOGIN) ---
    // Esta variable es la que controla si el usuario ha iniciado sesión o no.
    var isLoggedIn by mutableStateOf(false)
        private set // Solo el ViewModel puede cambiar este valor

    // --- DATOS HARCODEADOS (PRIVADOS) ---
    private val hardcodedUser = "Sebastian Granobles Ardila"
    private val hardcodedPass = "1144109752"

    /**
     * Valida las credenciales del usuario y actualiza el estado de la sesión.
     */
    fun onLogin(user: String, pass: String): Boolean {
        return if (user == hardcodedUser && pass == hardcodedPass) {
            // Si las credenciales son correctas:
            // 1. Actualizamos los datos del usuario.
            _userState.value = UserData(fullName = user, profileImageUri = null)
            // 2. Marcamos la sesión como iniciada. ¡ESTO ES CRUCIAL!
            isLoggedIn = true // <-- ARREGLO #2: Lógica de sesión añadida
            true
        } else {
            // Si las credenciales son incorrectas, nos aseguramos de que la sesión esté cerrada.
            isLoggedIn = false
            false
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    fun logout() {
        isLoggedIn = false
        // Opcional: limpiar los datos del usuario al cerrar sesión.
        _userState.value = UserData()
    }

    /**
     * Actualiza la imagen de perfil del usuario.
     */
    fun onProfileImageChange(uri: Uri?) {
        _userState.value = _userState.value.copy(
            profileImageUri = uri
        )
    }
}
