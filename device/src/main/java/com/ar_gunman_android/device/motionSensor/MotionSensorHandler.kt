package com.ar_gunman_android.device.motionSensor

import com.ar_gunman_android.domain.entities.motion.PhysicalMotion

interface MotionSensorHandlerInterface {
    var motionUpdated: ((PhysicalMotion) -> Unit)?
    fun startDetection()
    fun stopDetection()
}

class MotionSensorHandler {

}