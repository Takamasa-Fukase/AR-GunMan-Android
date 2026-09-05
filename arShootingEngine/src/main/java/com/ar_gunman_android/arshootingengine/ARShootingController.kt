package com.ar_gunman_android.arshootingengine

import com.ar_gunman_android.arshootingengine.models.WeaponType

interface ARShootingControllerInterface {
    var targetHit: ((WeaponType) -> Unit)?
    fun run()
    fun stop()
    fun showWeapon(type: WeaponType)
    fun renderWeaponFiring()
    fun changeTargetsAppearance()
}

internal class ARShootingController : ARShootingControllerInterface {
    override var targetHit: ((WeaponType) -> Unit)? = null

    override fun run() {

    }

    override fun stop() {

    }

    override fun showWeapon(type: WeaponType) {

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