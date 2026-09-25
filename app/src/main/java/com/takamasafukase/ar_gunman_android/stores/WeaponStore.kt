package com.takamasafukase.ar_gunman_android.stores

import com.ar_gunman_android.domain.entities.weapon.Weapon
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object WeaponStore : WeaponStoreInterface {
    override val weapon: StateFlow<Weapon> get() = _weapon.asStateFlow()
    private val _weapon = MutableStateFlow(value = Weapon())

    override fun updateWeapon(value: Weapon) {
        _weapon.value = value
    }

    override fun reset() {
        _weapon.value = Weapon()
    }
}