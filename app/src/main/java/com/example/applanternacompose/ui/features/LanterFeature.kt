package com.example.applanternacompose.ui.features

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun LanternFeature(onToggleFlashlight: (Boolean) -> Unit, modifier: Modifier) {
    var isFlashOn by remember { mutableStateOf(false) }
    val context = LocalContext.current;



    Column {
        Text(text = "LanternFeature", modifier)
        Button(onClick = {
            Log.d("LanternFeature", "Ligar Lanterna")
            Toast.makeText(context, "Test de toast", Toast.LENGTH_SHORT).show()
            isFlashOn = !isFlashOn
            onToggleFlashlight(isFlashOn)


        }) {
            Text(if (isFlashOn) "Desligar lanterna" else "Ligar lanterna")
        }
    }

}
