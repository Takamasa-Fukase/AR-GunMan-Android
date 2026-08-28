package com.takamasafukase.ar_gunman_android.stores

import com.ar_gunman_android.domain.entities.weapon.Weapon
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object WeaponStore : WeaponStoreInterface {
    override val weapon: StateFlow<Weapon> get() = _weapon.asStateFlow()
    private val _weapon = MutableStateFlow(value = Weapon())

    override fun updateWeapon(transform: (Weapon) -> Weapon) {
        _weapon.update(transform)
    }

    override fun <R> updateWeaponWithResult(transform: (Weapon) -> Pair<Weapon, R>): R {
        val (updatedWeapon, result) = transform(_weapon.value)
        _weapon.value = updatedWeapon
        return result
    }

    override fun reset() {
        _weapon.value = Weapon()
    }
}