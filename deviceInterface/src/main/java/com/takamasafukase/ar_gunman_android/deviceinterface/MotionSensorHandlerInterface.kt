package com.takamasafukase.ar_gunman_android.deviceinterface

import com.example.domain.entities.vectorMotionData.VectorMotionData

interface MotionSensorHandlerInterface {
    var accelerationUpdated: ((VectorMotionData) -> Unit)?
    var gyroUpdated: ((VectorMotionData) -> Unit)?
    fun startDetection()
    fun stopDetection()
}