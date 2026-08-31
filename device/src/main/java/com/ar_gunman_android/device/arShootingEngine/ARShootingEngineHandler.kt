package com.ar_gunman_android.device.arShootingEngine

import com.ar_gunman_android.domain.entities.weapon.WeaponType as DomainWeaponType

interface ARShootingEngineHandlerInterface {
    var targetHit: ((DomainWeaponType) -> Unit)?
    fun run()
    fun pause()
    fun showWeapon(type: DomainWeaponType)
    fun renderWeaponFiring()
    fun changeTargetsAppearance()
}

class ARShootingEngineHandler(
//    private val arShootingController:
) : ARShootingEngineHandlerInterface {
    override var targetHit: ((DomainWeaponType) -> Unit)? = null

    override fun run() {

    }

    override fun pause() {

    }

    override fun showWeapon(type: DomainWeaponType) {

    }

    override fun renderWeaponFiring() {
        // TODO
//                    // 現在の武器の射撃命令のメッセージを作成
//                    val toUnityMessage = AndroidToUnityMessage(
//                        eventType = AndroidToUnityMessageEventType.FIRE_WEAPON,
//                        weaponType = currentWeapon.weaponTypeChanged.value,
//                    )
//                    UnityMessageCenter.sendMessageToUnity(toUnityMessage)
    }

    override fun changeTargetsAppearance() {

    }
}