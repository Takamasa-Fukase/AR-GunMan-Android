package com.ar_gunman_android.domain.storeInterfaces

import com.ar_gunman_android.domain.entities.weapon.Weapon
import kotlinx.coroutines.flow.StateFlow

interface WeaponStoreInterface {
    val weapon: StateFlow<Weapon>
    fun updateWeapon(transform: (Weapon) -> Weapon)
    fun <R> updateWeaponWithResult(transform: (Weapon) -> Pair<Weapon, R>): R
    fun reset()
}