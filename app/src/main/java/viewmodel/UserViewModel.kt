package com.example.parcial_sebastiangranoblesardila.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_sebastiangranoblesardila.model.PasswordChangeLog
import com.example.parcial_sebastiangranoblesardila.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AuthState {
    IDLE, LOADING, SUCCESS, ERROR
}

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    private val authStateListener: FirebaseAuth.AuthStateListener
    private val _authState = MutableStateFlow(AuthState.IDLE)
    val authState = _authState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _passwordChangeHistory = MutableStateFlow<List<PasswordChangeLog>>(emptyList())
    val passwordChangeHistory = _passwordChangeHistory.asStateFlow()

    val isProfileEditingLocked: Boolean
        get() = (_user.value?.lastProfileUpdateTime ?: 0L) > 0 &&
                (System.currentTimeMillis() - (_user.value?.lastProfileUpdateTime ?: 0L)) < 3 * 60 * 60 * 1000 // 3 horas

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

    init {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                if (_user.value == null) {
                    loadUserData(firebaseUser.uid)
                }
                _authState.value = AuthState.SUCCESS
            } else {
                _user.value = null
                _passwordChangeHistory.value = emptyList()
                _authState.value = AuthState.IDLE
            }
        }
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        val cleanEmail = email.trim() // ⭐ ELIMINA ESPACIOS
        _authState.value = AuthState.LOADING
        try {
            auth.signInWithEmailAndPassword(cleanEmail, password).await()
        } catch (e: Exception) {
            _errorMessage.value = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "El correo o la contraseña no son correctos."
                else -> e.message ?: "Error desconocido."
            }
            _authState.value = AuthState.ERROR
        }
    }
    fun register(fullName: String, email: String, password: String) = viewModelScope.launch {
        val cleanEmail = email.trim()
        val cleanName = fullName.trim()

        // 1. Validaciones
        if (cleanName.isEmpty() || cleanEmail.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Por favor, completa todos los campos."
            _authState.value = AuthState.ERROR
            return@launch
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _errorMessage.value = "Formato de correo inválido."
            _authState.value = AuthState.ERROR
            return@launch
        }

        _authState.value = AuthState.LOADING
        _errorMessage.value = null

        try {
            // Intentar crear en Auth
            val result = auth.createUserWithEmailAndPassword(cleanEmail, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val newUser = User(
                    uid = firebaseUser.uid,
                    fullName = cleanName,
                    email = cleanEmail
                )
                // Guardar en Firestore
                usersCollection.document(firebaseUser.uid).set(newUser).await()

                android.util.Log.d("AUTH_DEBUG", "Usuario creado con éxito: ${firebaseUser.uid}")
                _authState.value = AuthState.SUCCESS
            }
        } catch (e: Exception) {
            android.util.Log.e("AUTH_DEBUG", "Error en Firebase: ${e.message}")
            _errorMessage.value = when (e) {
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "El correo ya está registrado."
                else -> e.localizedMessage ?: "Error desconocido."
            }
            _authState.value = AuthState.ERROR
        }
    }
    fun updateUserInfo(phone: String, age: String, city: String, nationality: String) = viewModelScope.launch {
        val currentUser = _user.value ?: return@launch
        val updatedTimestamp = System.currentTimeMillis()
        val updatedUser = currentUser.copy(
            phone = phone,
            age = age,
            city = city,
            nationality = nationality,
            lastProfileUpdateTime = updatedTimestamp
        )
        try {
            usersCollection.document(currentUser.uid).set(updatedUser).await()
            _user.value = updatedUser
        } catch (e: Exception) {
            _errorMessage.value = "No se pudo actualizar el perfil."
        }
    }

    fun changePassword(currentPass: String, newPass: String) = viewModelScope.launch {
        _authState.value = AuthState.LOADING
        val firebaseUser = auth.currentUser ?: return@launch
        val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, currentPass)
        try {
            firebaseUser.reauthenticate(credential).await()
            firebaseUser.updatePassword(newPass).await()
            val timestamp = System.currentTimeMillis()
            val logEntry = PasswordChangeLog(
                timestamp = timestamp,
                dateString = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
            )
            usersCollection.document(firebaseUser.uid)
                .collection("passwordHistory")
                .add(logEntry)
                .await()
            _authState.value = AuthState.SUCCESS
        } catch (e: Exception) {
            _errorMessage.value = when(e) {
                is FirebaseAuthInvalidCredentialsException -> "La contraseña actual es incorrecta."
                else -> "Error al cambiar la contraseña: ${e.message}"
            }
            _authState.value = AuthState.ERROR
        }
    }

    fun deleteAccount() = viewModelScope.launch {
        _authState.value = AuthState.LOADING
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            try {
                usersCollection.document(firebaseUser.uid).delete().await()
                firebaseUser.delete().await()
            } catch (e: Exception) {
                _errorMessage.value = "Error al borrar la cuenta. Es posible que necesites volver a iniciar sesión para completar esta acción."
                _authState.value = AuthState.ERROR
            }
        }
    }

    private fun loadUserData(uid: String) = viewModelScope.launch {
        try {
            val document = usersCollection.document(uid).get().await()
            _user.value = document.toObject(User::class.java)
            loadPasswordHistory(uid)
        } catch (e: Exception) {
            _errorMessage.value = "Error al cargar el perfil."
        }
    }

    private fun loadPasswordHistory(uid: String) = viewModelScope.launch {
        try {
            val snapshot = usersCollection.document(uid)
                .collection("passwordHistory")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            _passwordChangeHistory.value = snapshot.toObjects(PasswordChangeLog::class.java)
        } catch (e: Exception) {
            _errorMessage.value = "No se pudo cargar el historial."
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun resetAuthState() {
        _authState.value = AuthState.IDLE
        _errorMessage.value = null
    }
}
