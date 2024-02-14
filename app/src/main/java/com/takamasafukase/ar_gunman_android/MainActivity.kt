package com.takamasafukase.ar_gunman_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.Manifest
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.AppLaunchChecker
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.coroutineScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.takamasafukase.ar_gunman_android.manager.AudioManager
import com.takamasafukase.ar_gunman_android.repository.RankingRepository
import com.takamasafukase.ar_gunman_android.repository.TutorialPreferencesRepository
import com.takamasafukase.ar_gunman_android.view.game.GameActivity
import com.takamasafukase.ar_gunman_android.viewModel.TopViewModel
import com.takamasafukase.ar_gunman_android.ui.theme.ARGunManAndroidTheme
import com.takamasafukase.ar_gunman_android.utility.ErrorAlertDialog
import com.takamasafukase.ar_gunman_android.utility.RankingUtil
import com.takamasafukase.ar_gunman_android.view.result.ResultScreen
import com.takamasafukase.ar_gunman_android.view.setting.SettingScreen
import com.takamasafukase.ar_gunman_android.view.top.TopScreen
import com.takamasafukase.ar_gunman_android.viewModel.ResultViewModel
import com.takamasafukase.ar_gunman_android.viewModel.SettingViewModel
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_setting_preferences")

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioManager = AudioManager(context = application)
        val topViewModel = TopViewModel(
            app = application,
            audioManager = audioManager,
        )
        val resultViewModel = ResultViewModel(
            app = application,
            audioManager = audioManager,
            rankingRepository = RankingRepository(),
            rankingUtil = RankingUtil(),
        )
        val settingViewModel = SettingViewModel()

        setContent {
            ARGunManAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    RootCompose(
                        topViewModel = topViewModel,
                        resultViewModel = resultViewModel,
                        settingViewModel = settingViewModel,
                        showDeviceSetting = {
                            showDeviceSetting()
                        }
                    )
                }
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        // 初回起動の場合はチュートリアル通過フラグをクリアする
        if (!AppLaunchChecker.hasStartedFromLauncher(application)) {
            lifecycle.coroutineScope.launch {
                TutorialPreferencesRepository(application).clearTutorialSeenStatus()
            }
        }
        AppLaunchChecker.onActivityCreate(this)
    }

    private fun showDeviceSetting() {
        val uriString = "package:$packageName"
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(uriString))
        startActivity(intent)
    }
}

@Composable
fun RootCompose(
    topViewModel: TopViewModel,
    resultViewModel: ResultViewModel,
    settingViewModel: SettingViewModel,
    showDeviceSetting: () -> Unit,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    var receivedErrorMessage by remember { mutableStateOf<String?>(null) }

    NavHost(
        navController = navController,
        startDestination = "top",
    ) {
        composable("top") {
            TopScreen(
                viewModel = topViewModel,
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
        composable("setting") {
            SettingScreen(
                viewModel = settingViewModel,
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
            val totalScore = it.arguments?.getString("totalScore") ?: "0.0"
            ResultScreen(
                viewModel = resultViewModel,
                totalScore = totalScore.toDouble(),
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
    if (receivedErrorMessage != null) {
        ErrorAlertDialog(
            onDismissRequest = {
                // 閉じる時にエラーメッセージをリセットする
                receivedErrorMessage = null
            },
            message = receivedErrorMessage
        )
    }

    val errorNotificationHandler = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val errorMessage = intent?.getStringExtra("errorMessage")
            if (errorMessage != null) {
                // 通知で受け取ったエラーメッセージをアラートで表示させる為に格納
                receivedErrorMessage = errorMessage
            }
        }
    }
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
        LocalBroadcastManager.getInstance(context).registerReceiver(
            errorNotificationHandler, IntentFilter("ERROR_EVENT")
        )
        context.registerReceiver(
            navigationNotificationHandler, IntentFilter("com.takamasafukase.ar_gunman_android.NAVIGATION_EVENT")
        )

        // onDisposeで通知受信時の処理を解除
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(
                errorNotificationHandler
            )
            context.unregisterReceiver(
                navigationNotificationHandler
            )
        }
    }
}