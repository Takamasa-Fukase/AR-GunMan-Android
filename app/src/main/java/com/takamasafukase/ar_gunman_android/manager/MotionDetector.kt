package com.takamasafukase.ar_gunman_android.manager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class MotionDetector(
    private val sensorManager: SensorManager,
    val onDetectWeaponFiringMotion: () -> Unit,
    val onDetectWeaponReloadingMotion: () -> Unit,
    ) : SensorEventListener {
    // 発射動作の判定では加速度＋ジャイロも使うので、最新の値としてここに格納して使う
    private var gyroCompositeValue = 0f
    private var lastAccelerationProcessedTimeMs = 0L
    private var lastGyroProcessedTimeMs = 0L

    init {
        registerListeners()
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

            handleUpdatedAccelerationData(
                compositeValue = getCompositeValue(
                    x = 0f,
                    y = event.values[1],
                    z = event.values[2],
                ),
                gyroZSquaredValue = gyroCompositeValue,
            )
        }
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val currentTimeMs = System.currentTimeMillis()

            // 前回の処理から「200ms」経つまでは無視
            if (currentTimeMs - lastGyroProcessedTimeMs < 200L) {
                return
            }
            Log.d("Android", "ログAndroid: 🟢🟦200ms経ったので通過")
            lastGyroProcessedTimeMs = currentTimeMs

            // 加速度の方の判定でジャイロも使うので格納する
            gyroCompositeValue = getCompositeValue(
                x = 0f,
                y = 0f,
                z = event.values[2]
            )
            handleUpdatedGyroData(compositeValue = gyroCompositeValue)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    fun stopUpdate() {
        // Listenerを解除
        sensorManager.unregisterListener(this)
    }

    private fun registerListeners() {
        // 加速度Listenerの登録
        val acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (acceleration != null) {
            sensorManager.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_NORMAL)
        }else {
            Log.d("debug", "TYPE_ACCELEROMETER not supported")
        }
        // ジャイロListenerの登録
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (gyro != null) {
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("debug", "TYPE_GYROSCOPE not supported")
        }
    }

    private fun handleUpdatedAccelerationData(
        compositeValue: Float,
        gyroZSquaredValue: Float,
    ) {
        Log.d("Android", "ログAndroid: 🟥onDetectWeaponFiringMotion")
        if (compositeValue >= 144.25 && gyroZSquaredValue < 10) {
            Log.d("Android", "ログAndroid: 🟥⭐️onDetectWeaponFiringMotion")
            onDetectWeaponFiringMotion()
        }
    }

    private fun handleUpdatedGyroData(
        compositeValue: Float,
    ) {
        Log.d("Android", "ログAndroid: 🟦handleUpdatedGyroData")
        if (compositeValue >= 10) {
            Log.d("Android", "ログAndroid: 🟦⭐️onDetectWeaponReloadingMotion")
            onDetectWeaponReloadingMotion()
        }
    }

    private fun getCompositeValue(x: Float, y: Float, z: Float): Float {
        return (x * x) + (y * y) + (z * z)
    }
}

