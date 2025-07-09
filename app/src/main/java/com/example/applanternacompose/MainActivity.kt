package com.example.applanternacompose

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.applanternacompose.ui.features.LanternFeature
import com.example.applanternacompose.ui.features.ShakingFeature
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    private val DEBUG_MODE_TOAST = true;

    // --- BEGIN
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var shakeTimestamp: Long = 0
    private val shakeThreshold = 10f
    private val shakeSlopTimeMs = 500

    private val _shakeDetected = mutableStateOf(false)
    val shakeDetected: State<Boolean> get() = _shakeDetected

    // --- END

    private val TAG = this::class.java.simpleName;

    private lateinit var cameraManager: CameraManager;
    private lateinit var cameraId: String;

    private val lightState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- BEGIN
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // --- END


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
                    ShakingFeature(isShaking = shakeDetected.value)
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

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce > shakeThreshold) {
            val now = System.currentTimeMillis()
            if (shakeTimestamp + shakeSlopTimeMs > now) return

            shakeTimestamp = now

            _shakeDetected.value = true
            lightState.value = !lightState.value
            toggleFlashlight(lightState.value)


            if (DEBUG_MODE_TOAST) {
                Toast.makeText(this, "Shake detected!!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO("Not yet implemented")
    }
}



