package com.example.parcial_sebastiangranoblesardila.Presentation.viewmodel

import androidx.lifecycle.ViewModel

class RecuperarContrasenaViewModel : ViewModel() {

    private val userCredentials = mapOf(
        "sebas@gmail.com" to "1144109752"
        // Aquí podrías añadir más usuarios si quisieras
    )

    fun recoverPassword(email: String): String? {
        // Busca el email en el mapa y devuelve la contraseña si la encuentra.
        // .trim() elimina espacios en blanco al inicio y al final.
        return userCredentials[email.trim()]
    }
}
