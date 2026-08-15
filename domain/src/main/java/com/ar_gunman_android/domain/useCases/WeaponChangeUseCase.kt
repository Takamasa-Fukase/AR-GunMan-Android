package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.weapon.WeaponType
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface

interface WeaponChangeUseCaseInterface {
    fun execute(newType: WeaponType)
}

class WeaponChangeUseCase(
    private val weaponStore: WeaponStoreInterface,
    private val weaponReloadUseCase: WeaponReloadUseCaseInterface,
) : WeaponChangeUseCaseInterface {
    override fun execute(newType: WeaponType) {
        // 既存のリロードをキャンセルする
        weaponReloadUseCase.stopCurrentReloadIfExists()
        weaponStore.updateWeapon { weapon ->
            weapon.change(newType = newType)
        }
    }
}