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

data class UserData(
    val fullName: String,
    val email: String,
    val profileImageUri: Uri?,
    val phone: String = "",
    val age: String = "",
    val city: String = "",
    val nationality: String = ""
)

data class PasswordChangeLog(
    val userEmail: String,
    val oldPass: String,
    val newPass: String,
    val timestamp: String
)

class UserViewModel : ViewModel() {

    // --- ESTADO PÚBLICO ---
    private val _userState = mutableStateOf<UserData?>(null)
    val userState: State<UserData?> = _userState

    var isLoggedIn by mutableStateOf(false)
        private set

    val passwordChangeHistory = mutableStateListOf<PasswordChangeLog>()
    var lastProfileUpdateTime by mutableStateOf(0L)
        private set
    val isProfileEditingLocked: Boolean
        get() = (System.currentTimeMillis() - lastProfileUpdateTime) < 3 * 60 * 60 * 1000 // 3 horas

    val nationalities: List<String> = listOf(
        "Afgana", "Albanesa", "Alemana", "Andorrana", "Angoleña", "Antiguana", "Saudita",
        "Argelina", "Argentina", "Armenia", "Australiana", "Austríaca", "Azerbaiyana",
        "Bahameña", "Bangladesí", "Barbadense", "Bareiní", "Belga", "Beliceña", "Beninesa",
        "Bielorrusa", "Birmana", "Boliviana", "Bosnia", "Botsuana", "Brasileña", "Bruneana",
        "Búlgara", "Burkinesa", "Burundesa", "Butanesa", "Caboverdiana", "Camboyana",
        "Camerunesa", "Canadiense", "Catarí", "Chadiana", "Chilena", "China", "Chipriota",
        "Colombiana", "Comorense", "Congoleña", "Norcoreana", "Surcoreana", "Marfileña",
        "Costarricense", "Croata", "Cubana", "Danesa", "Dominiquesa", "Ecuatoriana",
        "Egipcia", "Salvadoreña", "Emiratí", "Eritrea", "Eslovaca", "Eslovena", "Española",
        "Estadounidense", "Estonia", "Etíope", "Filipina", "Finlandesa", "Fiyiana",
        "Francesa", "Gabonesa", "Gambiana", "Georgiana", "Ghanesa", "Granadina", "Griega",
        "Guatemalteca", "Guineana", "Ecuatoguineana", "Guyanesa", "Haitiana", "Hondureña",
        "Húngara", "India", "Indonesa", "Iraquí", "Iraní", "Irlandesa", "Islandesa",
        "Israelí", "Italiana", "Jamaicana", "Japonesa", "Jordana", "Kazaja", "Keniata",
        "Kirguisa", "Kiribatiana", "Kuwaití", "Laosiana", "Lesotense", "Letona", "Libanesa",
        "Liberiana", "Libia", "Liechtensteiniana", "Lituana", "Luxemburguesa", "Macedonia",
        "Malasia", "Malauí", "Maldiva", "Maliense", "Maltesa", "Marroquí", "Mauriciana",
        "Mauritana", "Mexicana", "Micronesia", "Moldava", "Monegasca", "Mongola",
        "Montenegrina", "Mozambiqueña", "Namibia", "Nauruana", "Nepalí", "Nicaragüense",
        "Nigeriana", "Noruega", "Neozelandesa", "Omaní", "Neerlandesa", "Pakistaní",
        "Palauana", "Panameña", "Papú", "Paraguaya", "Peruana", "Polaca", "Portuguesa",
        "Británica", "Centroafricana", "Checa", "Dominicana", "Ruandesa", "Rumana",
        "Rusa", "Samoana", "Sancristobaleña", "Sanmarinense", "Santaluciana",
        "Santomense", "Senegalesa", "Serbia", "Seychellense", "Sierraleonesa",
        "Singapurense", "Siria", "Somalí", "Ceilanés", "Suazi", "Sudafricana",
        "Sudanesa", "Sueca", "Suiza", "Surinamesa", "Tailandesa", "Tanzana", "Tayika",
        "Timorense", "Togolesa", "Tongana", "Trinitense", "Tunecina", "Turcomana",
        "Turca", "Tuvaluana", "Ucraniana", "Ugandesa", "Uruguaya", "Uzbeka", "Vanuatuense",
        "Vaticana", "Venezolana", "Vietnamita", "Yemení", "Yibutiana", "Zambiana", "Zimbabuense"
    )

    private val hardcodedEmail = "sebastiangranobles@hotmail.com"
    private var hardcodedPass by mutableStateOf("llulucasia2025")
    private val hardcodedFullName = "Sebastian Granobles Ardila"

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

    fun deleteAccount() {
        logout()
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

        lastProfileUpdateTime = System.currentTimeMillis()
    }

    fun changePassword(currentPass: String, newPass: String): Boolean {
        if (currentPass == this.hardcodedPass) {
            val oldPassword = this.hardcodedPass
            this.hardcodedPass = newPass
            val logEntry = PasswordChangeLog(
                userEmail = this.hardcodedEmail,
                oldPass = oldPassword,
                newPass = newPass,
                timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            )
            passwordChangeHistory.add(0, logEntry) // Añade al principio de la lista
            return true
        }
        return false
    }
}
