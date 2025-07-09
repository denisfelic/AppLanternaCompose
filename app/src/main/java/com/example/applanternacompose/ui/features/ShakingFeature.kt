package com.example.applanternacompose.ui.features

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ShakingFeature(isShaking: Boolean) {
    Text(text = "ShakingFeature")
    Text(
        text = if (isShaking) "Shaking" else "Not Shaking",
        style = MaterialTheme.typography.headlineMedium
    )
}