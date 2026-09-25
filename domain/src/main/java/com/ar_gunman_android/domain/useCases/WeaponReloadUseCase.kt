package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.weapon.WeaponReloadStartResult
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface WeaponReloadUseCaseInterface {
    val reloadStartResultEvent: SharedFlow<WeaponReloadStartResult>
    suspend fun execute()
    fun stopCurrentReloadIfExists()
    fun setScope(scope: CoroutineScope)
}

class WeaponReloadUseCase(
    private val weaponStore: WeaponStoreInterface,
) : WeaponReloadUseCaseInterface {
    override val reloadStartResultEvent: SharedFlow<WeaponReloadStartResult> get() = _reloadStartResultEvent.asSharedFlow()
    private var scope: CoroutineScope? = null

    private val _reloadStartResultEvent = MutableSharedFlow<WeaponReloadStartResult>()
    private var reloadJob: Job? = null

    override suspend fun execute() {
        val (updatedWeapon, startResult) = weaponStore.weapon.value.startReload()
        weaponStore.updateWeapon(value = updatedWeapon)
        _reloadStartResultEvent.emit(startResult)

        reloadJob = scope?.launch {
            // 現在の武器のリロードにかかる秒数分待機
            delay(timeMillis = weaponStore.weapon.value.currentType.reloadWaitingTimeMillisec.toLong())

            weaponStore.updateWeapon(
                value = weaponStore.weapon.value.finishReload()
            )
        }
    }

    override fun stopCurrentReloadIfExists() {
        reloadJob?.cancel()
        reloadJob = null
    }

    override fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }
}