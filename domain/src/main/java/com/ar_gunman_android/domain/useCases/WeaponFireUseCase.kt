package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.weapon.WeaponFireResult
import com.ar_gunman_android.domain.entities.weapon.WeaponType
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface WeaponFireUseCaseInterface {
    val fireResultEvent: SharedFlow<WeaponFireResult>
    suspend fun execute()
}

class WeaponFireUseCase(
    private val weaponStore: WeaponStoreInterface,
    private val weaponReloadUseCase: WeaponReloadUseCaseInterface,
) : WeaponFireUseCaseInterface {
    override val fireResultEvent: SharedFlow<WeaponFireResult> get() = _fireResultEvent.asSharedFlow()
    private val _fireResultEvent = MutableSharedFlow<WeaponFireResult>()

    override suspend fun execute() {
        val fireResult = weaponStore.updateWeaponWithResult { weapon ->
            weapon.fire()
        }
        _fireResultEvent.emit(fireResult)

        if (fireResult == WeaponFireResult.Success && weaponStore.weapon.value.currentType.reloadType == WeaponType.ReloadType.AUTO) {
            // リロードを自動的に実行
            weaponReloadUseCase.execute()
        }
    }
}