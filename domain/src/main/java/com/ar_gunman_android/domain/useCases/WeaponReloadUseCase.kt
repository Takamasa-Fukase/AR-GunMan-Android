package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.weapon.WeaponReloadStartResult
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

interface WeaponReloadUseCaseInterface {
    val reloadStartResultEvent: SharedFlow<WeaponReloadStartResult>
    suspend fun execute()
    fun stopCurrentReloadIfExists()
}

class WeaponReloadUseCase(
    private val weaponStore: WeaponStoreInterface,
    private val scope: CoroutineScope
) : WeaponReloadUseCaseInterface {
    override val reloadStartResultEvent: SharedFlow<WeaponReloadStartResult> get() = _reloadStartResultEvent.asSharedFlow()
    private val _reloadStartResultEvent = MutableSharedFlow<WeaponReloadStartResult>()
    private var reloadJob: Job? = null

    override suspend fun execute() {
        val startResult = weaponStore.updateWeaponWithResult { weapon ->
            weapon.startReload()
        }
        _reloadStartResultEvent.emit(startResult)

        reloadJob = scope.launch {
            // 現在の武器のリロードにかかる秒数分待機
            delay(timeMillis = weaponStore.weapon.value.currentType.reloadWaitingTimeMillisec.toLong())

            weaponStore.updateWeapon { weapon ->
                weapon.finishReload()
            }
        }
    }

    override fun stopCurrentReloadIfExists() {
        reloadJob?.cancel()
        reloadJob = null
    }
}