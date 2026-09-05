package com.ar_gunman_android.arshootingengine

import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ar_gunman_android.arshootingengine.models.AndroidToUnityMessage
import com.ar_gunman_android.arshootingengine.models.AndroidToUnityMessageEventType
import com.ar_gunman_android.arshootingengine.models.WeaponType
import com.unity3d.player.UnityPlayer

interface ARShootingControllerInterface {
    var targetHit: ((WeaponType) -> Unit)?
    fun run()
    fun stop()
    fun showWeapon(type: WeaponType)
    fun renderWeaponFiring()
    fun changeTargetsAppearance()
}

internal class ARShootingController(
    private val activity: ComponentActivity
) : ARShootingControllerInterface, DefaultLifecycleObserver {
    override var targetHit: ((WeaponType) -> Unit)? = null
    val rootView: android.view.View get() = unityPlayer!!.rootView

    private var unityPlayer: UnityPlayer? = UnityPlayer(activity)
    private val focusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        unityPlayer?.windowFocusChanged(hasFocus)
    }

    init {
        activity.lifecycle.addObserver(this)
        activity.window.decorView.viewTreeObserver.addOnWindowFocusChangeListener(
            focusChangeListener
        )
    }

    override fun run() {

    }

    override fun stop() {
        unityPlayer?.unload()
        unityPlayer = null
    }

    override fun showWeapon(type: WeaponType) {

    }

    override fun renderWeaponFiring() {
        // 現在の武器の射撃命令のメッセージを作成
//        val toUnityMessage = AndroidToUnityMessage(
//            eventType = AndroidToUnityMessageEventType.FIRE_WEAPON,
//            weaponType = currentWeapon.weaponTypeChanged.value,
//        )
//        UnityMessageCenter.sendMessageToUnity(toUnityMessage)
    }

    override fun changeTargetsAppearance() {

    }

    // MARK: - Observing Lifecycle Events
    override fun onResume(owner: LifecycleOwner) {
        unityPlayer?.resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        unityPlayer?.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unityPlayer?.destroy()
        activity.lifecycle.removeObserver(this)
        activity.window.decorView.viewTreeObserver.removeOnWindowFocusChangeListener(
            focusChangeListener
        )
    }
}