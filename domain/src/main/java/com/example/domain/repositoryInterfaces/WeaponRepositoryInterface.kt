package com.example.domain.repositoryInterfaces

import com.example.domain.entities.weapon.Weapon

interface WeaponRepositoryInterface {
    fun getById(id: Int): Weapon<*, *>
    fun getDefault(): Weapon<*, *>
    fun getAll(): List<Weapon<*, *>>
}