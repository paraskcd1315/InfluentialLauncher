package com.paraskcd.influentiallauncher.devicestatus.infrastructure

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.view.Display
import com.paraskcd.influentiallauncher.devicestatus.domain.model.Tilt
import com.paraskcd.influentiallauncher.devicestatus.domain.model.tiltOnScreen
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceTiltSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp

@Singleton
class AndroidDeviceTiltSource @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceTiltSource {

    override val tilt: Flow<Tilt> = callbackFlow {
        val sensors = context.getSystemService(SensorManager::class.java)
        val gyroscope = sensors?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (sensors == null || gyroscope == null) {
            trySend(Tilt.Level)
            awaitClose()
            return@callbackFlow
        }
        val display = context.getSystemService(DisplayManager::class.java)?.getDisplay(Display.DEFAULT_DISPLAY)
        val max = TiltMetrics.MaxAngleRadians
        var aroundX = 0f
        var aroundY = 0f
        var lastNanos = 0L
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (lastNanos != 0L) {
                    val seconds = (event.timestamp - lastNanos) * TiltMetrics.NanosToSeconds
                    val keep = exp(-seconds / TiltMetrics.RecenterSeconds)
                    aroundX = ((aroundX + event.values[0] * seconds) * keep).coerceIn(-max, max)
                    aroundY = ((aroundY + event.values[1] * seconds) * keep).coerceIn(-max, max)
                    trySend(tiltOnScreen(aroundY / max, aroundX / max, display?.rotation ?: 0))
                }
                lastNanos = event.timestamp
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }
        sensors.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        awaitClose { sensors.unregisterListener(listener) }
    }
}
