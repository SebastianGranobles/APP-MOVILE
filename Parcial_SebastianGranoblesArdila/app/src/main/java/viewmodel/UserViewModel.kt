package com.example.parcial_sebastiangranoblesardila.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


data class UserData(
    val fullName: String,
    val email: String,
    val profileImageUri: Uri?,
    // Nuevos campos
    val phone: String = "",
    val age: String = "",
    val city: String = "",
    val nationality: String = ""
)

class UserViewModel : ViewModel() {


    private val _userState = mutableStateOf<UserData?>(null)
    val userState: State<UserData?> = _userState

    var isLoggedIn by mutableStateOf(false)
        private set

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

    private val hardcodedEmail = "sebastiangranobles@hotmail.com"
    private var hardcodedPass by mutableStateOf("llulucasia2025")
    private val hardcodedFullName = "Sebastian Granobles Ardila"

    fun onLogin(email: String, pass: String): Boolean {
        return if (email == hardcodedEmail && pass == hardcodedPass) {
            // Al hacer login, cargamos los datos iniciales del usuario
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

    fun changePassword(currentPass: String, newPass: String): Boolean {
        if (currentPass == this.hardcodedPass) {
            this.hardcodedPass = newPass
            return true //
        }
        return false //
    }
}
