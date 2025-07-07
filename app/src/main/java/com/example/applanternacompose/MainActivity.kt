package com.example.applanternacompose

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.applanternacompose.ui.theme.AppLanternaComposeTheme
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName;

    private lateinit var cameraManager: CameraManager;
    private lateinit var cameraId: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "AppLanternaCompose onCreate - Inicializando a Activity")

        val notHasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED;

        if (notHasPermission) {
            Log.d(TAG, "AppLanternaCompose onCreate - Solicitar permissão de câmera")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: run {
            Toast.makeText(this, "Dispositivo não tem flash", Toast.LENGTH_SHORT).show()
            return
        }

        setContent {
            AppLanternaComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LanternFeature(
                        modifier = Modifier.padding(innerPadding), onToggleFlashlight = { state ->
                            toggleFlashlight(state)
                        }

                    )
                }
            }
        }
    }

    private fun toggleFlashlight(state: Boolean) {
        Log.d(TAG, "AppLanternaCompose toggleFlashlight - Alterando estado da lanterna")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.setTorchMode(cameraId, state)
        }
    }
}

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
