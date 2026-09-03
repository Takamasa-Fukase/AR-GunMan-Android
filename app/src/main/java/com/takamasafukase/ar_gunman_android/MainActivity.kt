package com.takamasafukase.ar_gunman_android

import android.content.Intent
import android.os.Bundle
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.takamasafukase.ar_gunman_android.constants.SavedStateHandleKeys
import com.takamasafukase.ar_gunman_android.factories.Factory
import com.takamasafukase.ar_gunman_android.features.game.GameViewBuilder
import com.takamasafukase.ar_gunman_android.features.nameRegister.NameRegisterViewBuilder
import com.takamasafukase.ar_gunman_android.features.ranking.RankingViewBuilder
import com.takamasafukase.ar_gunman_android.ui.theme.ARGunManAndroidTheme
import com.takamasafukase.ar_gunman_android.features.result.ResultViewBuilder
import com.takamasafukase.ar_gunman_android.features.settings.SettingsViewBuilder
import com.takamasafukase.ar_gunman_android.features.top.TopViewBuilder
import com.takamasafukase.ar_gunman_android.features.tutorial.TutorialView
import com.takamasafukase.ar_gunman_android.features.weaponSelect.WeaponSelectView

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
    factory: Factory,
    showDeviceSetting: () -> Unit,
) {
    val navController = rememberNavController()

    // TODO: route文字列をConstにする
    NavHost(
        navController = navController,
        startDestination = "top",
    ) {
        composable("top") {
            TopViewBuilder(
                factory = factory,
                showGameView = {
                    navController.navigate("game")
                },
                showTutorialView = {
                    navController.navigate("tutorial")
                },
                showSettingsView = {
                    navController.navigate("settings")
                },
                showDeviceSettings = {
                    showDeviceSetting()
                },
            )
        }
        dialog("tutorial") {
            TutorialView(
                onClose = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SavedStateHandleKeys.TUTORIAL_ENDED_EVENT, Unit)
                    navController.popBackStack()
                }
            )
        }
        composable("settings") {
            SettingsViewBuilder(
                showRankingView = {
                    navController.navigate("ranking")
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
        dialog("ranking") {
            RankingViewBuilder(
                factory = factory,
                onClose = {
                    navController.popBackStack()
                }
            )
        }
        composable("game") {
            GameViewBuilder(
                factory = factory,
                showTutorialView = {
                    navController.navigate("tutorial")
                },
                showWeaponSelectView = {
                    navController.navigate("weaponSelect")
                },
                closeWeaponSelectView = {
                    // TODO: 挙動を検証する
                    // ここで単純にpopしたらどうなるのか
                    // 明示的に今のGameを残してそれより上があれば消す　とかの方がいいかも？
                },
                showResultView = { score ->
                    navController.navigate("result/$score")
                }
            )
        }
        dialog("weaponSelect") {
            WeaponSelectView(
                onClose = {
                    navController.popBackStack()
                },
                onSelectWeapon = { weaponType ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SavedStateHandleKeys.SELECTED_WEAPON_TYPE, weaponType)

                    // TODO: onCloseの方も自動で呼ばれるのか、こっちでもpopが必要かを実際に確認する
                    // TODO: 二重でpopされないかも確認したい
                    navController.popBackStack()
                }
            )
        }
        composable("result/{score}") {
            ResultViewBuilder(
                factory = factory,
                showNameRegisterView = { score ->
                    navController.navigate("nameRegister/$score")
                },
                onReplay = {
                    navController.navigate("game") {
                        popUpTo("top") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                toHome = {
                    navController.navigate("top") {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        dialog("nameRegister/{score}") {
            NameRegisterViewBuilder(
                factory = factory,
                onClose = { rankingItem ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SavedStateHandleKeys.REGISTERED_RANKING_ITEM, rankingItem)
                    navController.popBackStack()
                }
            )
        }
    }
}