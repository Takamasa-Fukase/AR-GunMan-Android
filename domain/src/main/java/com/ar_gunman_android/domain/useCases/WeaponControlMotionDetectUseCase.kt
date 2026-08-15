package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.motion.PhysicalMotion
import com.ar_gunman_android.domain.entities.motion.WeaponControlMotion

interface WeaponControlMotionDetectUseCaseInterface {
    fun execute(motion: PhysicalMotion): WeaponControlMotion?
}

class WeaponControlMotionDetectUseCase : WeaponControlMotionDetectUseCaseInterface {
    private var latestGyro: PhysicalMotion? = null

    override fun execute(motion: PhysicalMotion): WeaponControlMotion? {
        return when (motion.type) {
            PhysicalMotion.MotionType.ACCELERATION -> {
                WeaponControlMotion.from(acceleration = motion, gyro = latestGyro)
            }

            PhysicalMotion.MotionType.GYRO -> {
                // ジャイロの値は発射モーションの判別にも使うので最新値を保持
                latestGyro = motion

                WeaponControlMotion.from(gyro = motion)
            }
        }
    }
}