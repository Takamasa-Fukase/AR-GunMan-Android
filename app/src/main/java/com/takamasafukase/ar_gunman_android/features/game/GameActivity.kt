package com.takamasafukase.ar_gunman_android.features.game

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.factories.Factory
import com.unity3d.player.UnityPlayer

//class GameActivity : ComponentActivity() {
//    private var unityPlayer: UnityPlayer? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        unityPlayer = UnityPlayer(this)
//
//        setContentView(R.layout.activity_game)
//
//        // FrameLayoutにUnityViewを追加
//        val frameLayout = findViewById<FrameLayout>(R.id.unity)
//        frameLayout.addView(unityPlayer?.rootView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//
//        // TODO: とりあえずビルド通すための仮
//        val factory = Factory(this)
//        val weaponReloadUseCase = factory.createWeaponReloadUseCase(
//            scope = lifecycleScope
//        )
//
//        // ComposeViewを作成してFrameLayoutに追加
//        val composeView = ComposeView(this).apply {
//            setContent {
//                GameView(
//                    viewModel = GameViewModel(
//                        arShootingEngineHandler = factory.createARShootingEngineHandler(),
//                        motionSensorHandler = factory.createMotionSensorHandler(),
//                        soundPlayer = factory.createSoundPlayer(),
//                        gameStore = factory.createGameStore(),
//                        weaponStore = factory.createWeaponStore(),
//                        weaponFireUseCase = factory.createWeaponFireUseCase(
//                            weaponReloadUseCase = weaponReloadUseCase
//                        ),
//                        weaponReloadUseCase = weaponReloadUseCase,
//                        weaponChangeUseCase = factory.createWeaponChangeUseCase(
//                            weaponReloadUseCase = weaponReloadUseCase
//                        ),
//                        gameFlowDriveUseCase = factory.createGameFlowDriveUseCase(
//                            scope = lifecycleScope
//                        ),
//                        scoreAddUseCase = factory.createScoreAddUseCase(),
//                        reloadingMotionCountUpdateUseCase = factory.createReloadingMotionCountUpdateUseCase(),
//                        weaponControlMotionDetectUseCase = factory.createWeaponControlMotionDetectUseCase(),
//                    ),
//                    toResult = { totalScore: Double ->
//                        // 通知を送信して、MainActivity内のNavHostでresult画面に切り替える
//                        val intent = Intent("com.takamasafukase.ar_gunman_android.NAVIGATION_EVENT")
//                        intent.putExtra("destination", "result")
//                        intent.putExtra("totalScore", totalScore.toString())
//                        // MEMO: UnityPlayerのバグの対策でこのGameActivityは別プロセスで起動している為、
//                        // LocalBroadcastが使えないのでBroadcastを使用している
//                        sendBroadcast(intent)
//
//                        // 上記だけだとこのActivityがMainActivity上に被さったままでresult画面が見えないので終了させる
//                        finish()
//                    }
//                )
//            }
//        }
//        frameLayout.addView(composeView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
//
//        // UnityPlayerにフォーカスを合わせる
//        unityPlayer?.requestFocus()
//    }
//
//    // Notify Unity of the focus change.
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        unityPlayer?.windowFocusChanged(hasFocus)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        unityPlayer?.resume()
//    }
//
//    override fun onDestroy() {
//        unityPlayer?.destroy()
//        super.onDestroy()
//    }
//}