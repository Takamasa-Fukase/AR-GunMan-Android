package com.ar_gunman_android.domain.repositoryInterfaces

import com.ar_gunman_android.domain.entities.weapon.Weapon

interface WeaponRepositoryInterface {
    fun getById(id: Int): Weapon<*, *>
    fun getDefault(): Weapon<*, *>
    fun getAll(): List<Weapon<*, *>>
}