package com.ar_gunman_android.domain.entities.weapon

data class Weapon(
    val currentType: WeaponType = WeaponType.defaultType,
    val bulletsCount: Int = WeaponType.defaultType.capacity,
    val isReloading: Boolean = false,
) {
    internal fun fire(): Pair<Weapon, WeaponFireResult> {
        if (isReloading) {
            return Pair(this, WeaponFireResult.Failure(reason = WeaponFireResult.FailureReason.RELOADING))
        }
        if (bulletsCount <= 0) {
            return Pair(this, WeaponFireResult.Failure(reason = WeaponFireResult.FailureReason.OUT_OF_BULLETS))
        }
        return Pair(this.copy(bulletsCount = bulletsCount - 1), WeaponFireResult.Success)
    }

    internal fun startReload(): Pair<Weapon, WeaponReloadStartResult> {
        if (isReloading || bulletsCount > 0) {
            return Pair(this, WeaponReloadStartResult.FAILURE)
        }
        return Pair(this.copy(isReloading = true), WeaponReloadStartResult.SUCCESS)
    }

    internal fun finishReload(): Weapon {
        return this.copy(
            bulletsCount = currentType.capacity,
            isReloading = false,
        )
    }

    internal fun change(newType: WeaponType): Weapon {
        return this.copy(currentType = newType).finishReload()
    }
}

sealed interface WeaponFireResult {
    object Success : WeaponFireResult
    data class Failure(val reason: FailureReason) : WeaponFireResult

    enum class FailureReason {
        RELOADING,
        OUT_OF_BULLETS;
    }
}

enum class WeaponReloadStartResult {
    SUCCESS,
    FAILURE;
}