package com.ar_gunman_android.device.motionSensor

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.ar_gunman_android.domain.entities.motion.PhysicalMotion

interface MotionSensorHandlerInterface {
    var motionUpdated: ((PhysicalMotion) -> Unit)?
    fun startDetection()
    fun stopDetection()
}

class MotionSensorHandler(
    context: Context,
    override var motionUpdated: ((PhysicalMotion) -> Unit)?,
) : MotionSensorHandlerInterface, SensorEventListener {
    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private var lastAccelerationProcessedTimeMs = 0L
    private var lastGyroProcessedTimeMs = 0L

    override fun startDetection() {
        // 二重登録を防止するため、登録前に一度解除しておく
        stopDetection()

        // 加速度Listenerの登録
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("debug", "TYPE_ACCELEROMETER not supported")
        }
        // ジャイロListenerの登録
        val gyroScope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (gyroScope != null) {
            sensorManager.registerListener(this, gyroScope, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("debug", "TYPE_GYROSCOPE not supported")
        }
    }

    override fun stopDetection() {
        // Listenerを解除
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTimeMs = System.currentTimeMillis()

            // 前回の処理から「200ms」経つまでは無視
            if (currentTimeMs - lastAccelerationProcessedTimeMs < 200L) {
                return
            }
            Log.d("Android", "ログAndroid: 🟢🟥200ms経ったので通過")
            lastAccelerationProcessedTimeMs = currentTimeMs

            motionUpdated?.invoke(event.accelerationMotion)
        }
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val currentTimeMs = System.currentTimeMillis()

            // 前回の処理から「200ms」経つまでは無視
            if (currentTimeMs - lastGyroProcessedTimeMs < 200L) {
                return
            }
            Log.d("Android", "ログAndroid: 🟢🟦200ms経ったので通過")
            lastGyroProcessedTimeMs = currentTimeMs

            motionUpdated?.invoke(event.gyroMotion)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
}

private val SensorEvent.accelerationMotion: PhysicalMotion
    get() = PhysicalMotion(
        type = PhysicalMotion.MotionType.ACCELERATION,
        x = values[0].toDouble(),
        y = values[1].toDouble(),
        z = values[2].toDouble()
    )

private val SensorEvent.gyroMotion: PhysicalMotion
    get() = PhysicalMotion(
        type = PhysicalMotion.MotionType.GYRO,
        x = values[0].toDouble(),
        y = values[1].toDouble(),
        z = values[2].toDouble()
    )