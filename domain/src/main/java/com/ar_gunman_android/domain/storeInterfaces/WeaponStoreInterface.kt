package com.ar_gunman_android.domain.storeInterfaces

import com.ar_gunman_android.domain.entities.weapon.Weapon
import kotlinx.coroutines.flow.StateFlow

interface WeaponStoreInterface {
    val weapon: StateFlow<Weapon>
    fun updateWeapon(value: Weapon)
    fun reset()
}