package com.example.parcial_sebastiangranoblesardila

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// ⭐ ARREGLO: Se eliminó la importación duplicada y errónea. Solo queda la correcta.
import com.example.parcial_sebastiangranoblesardila.presentation.AppNavigation
import com.example.parcial_sebastiangranoblesardila.ui.theme.Parcial_SebastianGranoblesArdilaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Parcial_SebastianGranoblesArdilaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Esta llamada es correcta porque la importación ahora es la única y correcta.
                    AppNavigation()
                }
            }
        }
    }
}
