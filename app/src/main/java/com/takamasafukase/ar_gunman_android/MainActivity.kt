package com.takamasafukase.ar_gunman_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.app.Application
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.takamasafukase.ar_gunman_android.factories.Factory
import com.takamasafukase.ar_gunman_android.features.game.GameActivity
import com.takamasafukase.ar_gunman_android.ui.theme.ARGunManAndroidTheme
import com.takamasafukase.ar_gunman_android.features.result.ResultView
import com.takamasafukase.ar_gunman_android.features.result.ResultViewModel
import com.takamasafukase.ar_gunman_android.features.settings.SettingsViewModel
import com.takamasafukase.ar_gunman_android.features.settings.SettingsView
import com.takamasafukase.ar_gunman_android.features.settings.SettingsViewBuilder
import com.takamasafukase.ar_gunman_android.features.top.TopViewBuilder
import com.takamasafukase.ar_gunman_android.features.tutorial.TutorialView
import kotlinx.coroutines.flow.MutableSharedFlow

class MainApplication : Application() {
    lateinit var factory: Factory

    override fun onCreate() {
        super.onCreate()
        factory = Factory(this)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = (application as MainApplication).factory

        setContent {
            ARGunManAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    RootCompose(
                        application = application,
                        factory = factory,
                        showDeviceSetting = {
                            showDeviceSetting()
                        }
                    )
                }
            }
        }
    }

    private fun showDeviceSetting() {
        val uriString = "package:$packageName"
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(uriString))
        startActivity(intent)
    }
}

@Composable
fun RootCompose(
    application: Application,
    factory: Factory,
    showDeviceSetting: () -> Unit,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
//    var receivedErrorMessage by remember { mutableStateOf<String?>(null) }

    NavHost(
        navController = navController,
        startDestination = "top",
    ) {
        composable("top") {
            TopViewBuilder(
                factory = factory,
                toGame = {
                    navController.navigate("game")
                },
                toSetting = {
                    navController.navigate("setting")
                },
                showDeviceSetting = {
                    showDeviceSetting()
                },
            )
        }
        dialog("tutorial") {
            TutorialView(
                onClose = {
//                    viewModel.onCloseTutorialDialog()
                }
            )
        }
        composable("setting") {
            SettingsViewBuilder(
                onClose = {
                    navController.navigate("top")
                }
            )
        }
        activity(route = "game") {
            activityClass = GameActivity::class
        }
        composable(
            route = "result/{totalScore}",
            arguments = listOf(
                navArgument("totalScore") {
                    type = NavType.StringType
                }
            )
        ) {
            val score = it.arguments?.getString("totalScore") ?: "0.0"
            val resultViewModel = ResultViewModel(
                app = application,
                score = score.toDouble(),
                soundPlayer = factory.createSoundPlayer(),
                rankingGetUseCase = factory.createRankingGetUseCase(),
                rankingStore = factory.createRankingStore(),
            )
            ResultView(
                factory = factory,
                viewModel = resultViewModel,
                onReplay = {
                    navController.navigate("game")
                },
                toHome = {
                    navController.navigate("top")
                }
            )
        }
    }

    // 未表示のエラーメッセージがあればアラートで表示
//    if (receivedErrorMessage != null) {
//        ErrorAlertDialog(
//            onDismissRequest = {
//                // 閉じる時にエラーメッセージをリセットする
//                receivedErrorMessage = null
//            },
//            message = receivedErrorMessage
//        )
//    }

    val navigationNotificationHandler = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Android", "ログAndroid: MainActivity onReceive navigationNotificationHandler")
            val destinationNameText = intent?.getStringExtra("destination")
            if (destinationNameText != null) {
                if (destinationNameText == "result") {
                    val totalScore = intent.getStringExtra("totalScore")
                    Log.d("Android", "ログAndroid: MainActivity onReceive navController.navigate($destinationNameText/$totalScore)を実行します")
                    // 受け取ったスコアと一緒に遷移指示を流す
                    navController.navigate("$destinationNameText/$totalScore")
                }else {
                    Log.d("Android", "ログAndroid: MainActivity onReceive navController.navigate($destinationNameText)を実行します")
                    // 通知で受け取ったdestinationに遷移
                    navController.navigate(destinationNameText)
                }
            }
        }
    }
    DisposableEffect(Unit) {
        // 通知受信時の処理を登録
        context.registerReceiver(
            navigationNotificationHandler, IntentFilter("com.takamasafukase.ar_gunman_android.NAVIGATION_EVENT")
        )

        // onDisposeで通知受信時の処理を解除
        onDispose {
            context.unregisterReceiver(
                navigationNotificationHandler
            )
        }
    }
}