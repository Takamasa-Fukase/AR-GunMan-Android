package com.ar_gunman_android.domain.storeInterfaces

import com.ar_gunman_android.domain.entities.weapon.Weapon

interface WeaponStoreInterface {
    var weapon: Weapon
    fun reset()
}