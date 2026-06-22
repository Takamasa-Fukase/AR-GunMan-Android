package com.example.domain.serviceInterfaces

import com.example.domain.entities.vectorMotionData.VectorMotionData

interface CoreMotionHandlerInterface {
    var accelerationUpdated: ((VectorMotionData) -> Unit)?
    var gyroUpdated: ((VectorMotionData) -> Unit)?
    fun startDetection()
    fun stopDetection()
}