package com.ar_gunman_android.arshootingengine

import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ar_gunman_android.arshootingengine.models.AndroidToUnityMessage
import com.ar_gunman_android.arshootingengine.models.AndroidToUnityMessageEventType
import com.ar_gunman_android.arshootingengine.models.WeaponType
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

interface ARShootingControllerInterface {
    var onEngineReady: (() -> Unit)?
    var targetHit: ((WeaponType) -> Unit)?
    fun run()
    fun pause()
    fun showWeapon(type: WeaponType)
    fun renderWeaponFiring()
    fun changeTargetsAppearance()
}

internal object ARShootingController : ARShootingControllerInterface, DefaultLifecycleObserver {
    internal val rootView: View
        get() = unityPlayer?.rootView ?: throw IllegalStateException("UnityPlayerがまだ初期化されていないため、先にinitializeメソッドを呼ぶこと")
    override var onEngineReady: (() -> Unit)? = null
    override var targetHit: ((WeaponType) -> Unit)? = null

    private var unityPlayer: UnityPlayer? = null
    private var boundActivity: ComponentActivity? = null
    private val focusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        unityPlayer?.windowFocusChanged(hasFocus)
    }
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isFirstRun = true

    init {
        scope.launch {
            UnityMessageCenter.splashFinishedEvent
                .collect {
                    onEngineReady?.invoke()
                }
        }

        scope.launch {
            UnityMessageCenter.targetHitEvent
                .debounce(50)
                .collect {
                    targetHit?.invoke(WeaponType.PISTOL)
                }
        }
    }

    fun initialize(activity: ComponentActivity) {
        if (unityPlayer == null) {
            unityPlayer = UnityPlayer(activity)
        }

        // Activityが変わる（画面再生成など）場合に備えてバインドし直す
        bindActivity(activity)
    }

    private fun bindActivity(activity: ComponentActivity) {
        if (boundActivity == activity) return

        // 古いActivityから解除
        unbindCurrentActivity()

        boundActivity = activity
        activity.lifecycle.addObserver(this)
        activity.window.decorView.viewTreeObserver.addOnWindowFocusChangeListener(focusChangeListener)
    }

    private fun unbindCurrentActivity() {
        boundActivity?.let { activity ->
            activity.lifecycle.removeObserver(this)
            activity.window.decorView.viewTreeObserver.removeOnWindowFocusChangeListener(focusChangeListener)
        }
        boundActivity = null
    }

    @OptIn(InternalSerializationApi::class)
    override fun run() {
        unityPlayer?.resume()
        unityPlayer?.rootView?.post {
            unityPlayer?.windowFocusChanged(true)
        }

        if (!isFirstRun) {
            // Unity側のシーンをリセットさせる通知を送る（UnityPlayer自体を破棄できないため）
            val toUnityMessage = AndroidToUnityMessage(
                eventType = AndroidToUnityMessageEventType.RESET_GAME_SCENE,
                weaponType = WeaponType.PISTOL,
            )
            UnityMessageCenter.sendMessageToUnity(toUnityMessage)
        }

        isFirstRun = false
    }

    override fun pause() {
        unityPlayer?.pause()
    }

    override fun showWeapon(type: WeaponType) {
        // TODO: Unity側のリファクタ時に繋げる
    }

    @OptIn(InternalSerializationApi::class)
    override fun renderWeaponFiring() {
        // 現在の武器の射撃命令のメッセージを作成
        val toUnityMessage = AndroidToUnityMessage(
            eventType = AndroidToUnityMessageEventType.FIRE_WEAPON,
            weaponType = WeaponType.PISTOL,
        )
        UnityMessageCenter.sendMessageToUnity(toUnityMessage)
    }

    override fun changeTargetsAppearance() {
        // Android版では未実装
    }

    // MARK: - Observing Lifecycle Events
    override fun onResume(owner: LifecycleOwner) {
        unityPlayer?.resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        unityPlayer?.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unityPlayer?.pause()

        unbindCurrentActivity()
        onEngineReady = null
        targetHit = null
    }
}