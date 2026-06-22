package com.example.domain.dataModels

import com.example.domain.entities.weapon.Weapon

data class CurrentWeapon(
    val weapon: Weapon<*, *>,
    var state: State
) {
    data class State(
        var bulletsCount: Int,
        var isReloading: Boolean
    )

    fun getBulletsCountImageName(): String {
        return weapon.resources.bulletsCountImageBaseName + state.bulletsCount
    }
}
