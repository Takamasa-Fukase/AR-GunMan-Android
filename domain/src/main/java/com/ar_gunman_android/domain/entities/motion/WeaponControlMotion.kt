package com.ar_gunman_android.domain.entities.motion

enum class WeaponControlMotion {
    FIRE,
    RELOAD;

    companion object {
        fun from(acceleration: PhysicalMotion, gyro: PhysicalMotion?): WeaponControlMotion? {
            val accelerationComposite = acceleration.getCompositeValue(
                dimensions = setOf(
                    PhysicalMotion.Dimension.Y,
                    PhysicalMotion.Dimension.Z,
                )
            )
            val gyroComposite = gyro?.getCompositeValue(
                dimensions = setOf(
                    PhysicalMotion.Dimension.Z,
                )
            ) ?: 0.0
            return if (isFiringMotion(accelerationComposite, gyroComposite)) {
                FIRE
            } else {
                null
            }
        }

        fun from(gyro: PhysicalMotion): WeaponControlMotion? {
            val gyroComposite = gyro.getCompositeValue(
                dimensions = setOf(
                    PhysicalMotion.Dimension.Z,
                )
            )
            return if (isReloadingMotion(gyroComposite)) {
                RELOAD
            } else {
                null
            }
        }

        private fun isFiringMotion(
            accelerationComposite: Double,
            gyroComposite: Double,
        ): Boolean {
            return accelerationComposite >= 144.25 && gyroComposite < 10
        }

        private fun isReloadingMotion(
            gyroComposite: Double
        ): Boolean {
            return gyroComposite >= 10
        }
    }
}