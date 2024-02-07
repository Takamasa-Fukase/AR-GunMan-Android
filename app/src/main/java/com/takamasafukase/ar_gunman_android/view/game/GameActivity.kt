package com.takamasafukase.ar_gunman_android.view.game

import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.takamasafukase.ar_gunman_android.manager.AudioManager
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.manager.CurrentWeapon
import com.takamasafukase.ar_gunman_android.manager.ScoreCounter
import com.takamasafukase.ar_gunman_android.manager.TimeCounter
import com.takamasafukase.ar_gunman_android.model.WeaponType
import com.takamasafukase.ar_gunman_android.repository.TutorialPreferencesRepository
import com.takamasafukase.ar_gunman_android.utility.ScoreCalculator
import com.takamasafukase.ar_gunman_android.utility.TimeCountUtil
import com.takamasafukase.ar_gunman_android.viewModel.GameViewModel
import com.unity3d.player.UnityPlayer

class GameActivity : ComponentActivity() {
    private var unityPlayer: UnityPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unityPlayer = UnityPlayer(this)

        setContentView(R.layout.activity_game)

        // FrameLayoutにUnityViewを追加
        val frameLayout = findViewById<FrameLayout>(R.id.unity)
        frameLayout.addView(unityPlayer?.rootView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        val timeCountUtil = TimeCountUtil()
        val audioManager = AudioManager(context = application)
        // ComposeViewを作成してFrameLayoutに追加
        val composeView = ComposeView(this).apply {
            setContent {
                GameScreen(
                    viewModel = GameViewModel(
                        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager,
                        tutorialPreferencesRepository = TutorialPreferencesRepository(this@GameActivity),
                        audioManager = audioManager,
                        timeCounter = TimeCounter(
                            timeCountUtil = timeCountUtil
                        ),
                        timeCountUtil = timeCountUtil,
                        currentWeapon = CurrentWeapon(
                            initialType = WeaponType.PISTOL,
                            audioManager = audioManager,
                        ),
                        scoreCounter = ScoreCounter(
                            scoreCalculator = ScoreCalculator()
                        ),
                    ),
                    toResult = { totalScore: Double ->
                        // 通知を送信して、MainActivity内のNavHostでresult画面に切り替える
                        val intent = Intent("NAVIGATION_EVENT")
                        intent.putExtra("destination", "result")
                        intent.putExtra("totalScore", totalScore.toString())
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

                        // 上記だけだとこのActivityがMainActivity上に被さったままでresult画面が見えないので終了させる
                        finish()
                    }
                )
            }
        }
        frameLayout.addView(composeView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

        // UnityPlayerにフォーカスを合わせる
        unityPlayer?.requestFocus()
    }

    // Notify Unity of the focus change.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        unityPlayer?.windowFocusChanged(hasFocus)
    }

    override fun onResume() {
        super.onResume()
        unityPlayer?.resume()
    }

    override fun onDestroy() {
        unityPlayer?.destroy()
        super.onDestroy()
    }
}