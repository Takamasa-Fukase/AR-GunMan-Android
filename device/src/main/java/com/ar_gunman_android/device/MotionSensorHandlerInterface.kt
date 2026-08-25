package com.ar_gunman_android.device

import com.ar_gunman_android.domain.entities.vectorMotionData.VectorMotionData

interface MotionSensorHandlerInterface {
    var accelerationUpdated: ((VectorMotionData) -> Unit)?
    var gyroUpdated: ((VectorMotionData) -> Unit)?
    fun startDetection()
    fun stopDetection()
}