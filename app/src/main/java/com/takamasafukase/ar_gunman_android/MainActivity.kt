package com.takamasafukase.ar_gunman_android

import android.content.Intent
import android.os.Bundle
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
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = Factory(activity = this)

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
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uriString.toUri())
        startActivity(intent)
    }
}

@Composable
fun RootCompose(
    factory: Factory,
    showDeviceSetting: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Top.route,
    ) {
        composable(NavigationRoute.Top.route) {
            TopViewBuilder(
                factory = factory,
                showGameView = {
                    navController.navigate(NavigationRoute.Game.route)
                },
                showTutorialView = {
                    navController.navigate(NavigationRoute.Tutorial.route)
                },
                showSettingsView = {
                    navController.navigate(NavigationRoute.Settings.route)
                },
                showDeviceSettings = {
                    showDeviceSetting()
                },
            )
        }
        dialog(NavigationRoute.Tutorial.route) {
            TutorialView(
                onClose = {
                    navController
                        .getBackStackEntry(NavigationRoute.Game.route)
                        .savedStateHandle[SavedStateHandleKeys.TUTORIAL_ENDED_EVENT] = true
                    navController.popBackStack()
                }
            )
        }
        composable(NavigationRoute.Settings.route) {
            SettingsViewBuilder(
                showRankingView = {
                    navController.navigate(NavigationRoute.Ranking.route)
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
        dialog(NavigationRoute.Ranking.route) {
            RankingViewBuilder(
                factory = factory,
                onClose = {
                    navController.popBackStack()
                }
            )
        }
        composable(NavigationRoute.Game.route) { navBackStackEntry ->
            GameViewBuilder(
                factory = factory,
                navBackStackEntry = navBackStackEntry,
                showTutorialView = {
                    navController.navigate(NavigationRoute.Tutorial.route)
                },
                showWeaponSelectView = {
                    navController.navigate(NavigationRoute.WeaponSelect.route)
                },
                closeWeaponSelectView = {
                    // TODO: 挙動を検証する
                    // ここで単純にpopしたらどうなるのか
                    // 明示的に今のGameを残してそれより上があれば消す　とかの方がいいかも？
                },
                showResultView = { score ->
                    navController.navigate(NavigationRoute.Result.createRoute(score))
                }
            )
        }
        dialog(NavigationRoute.WeaponSelect.route) {
            WeaponSelectView(
                onClose = { result ->
                    navController
                        .getBackStackEntry(NavigationRoute.Game.route)
                        .savedStateHandle[SavedStateHandleKeys.WEAPON_SELECT_RESULT] = result
                    navController.popBackStack()
                }
            )
        }
        composable(NavigationRoute.Result.route) { navBackStackEntry ->
            ResultViewBuilder(
                factory = factory,
                navBackStackEntry = navBackStackEntry,
                score = NavigationRoute.getScore(navBackStackEntry),
                showNameRegisterView = { score ->
                    navController.navigate(NavigationRoute.NameRegister.createRoute(score))
                },
                onReplay = {
                    navController.navigate(NavigationRoute.Game.route) {
                        popUpTo(NavigationRoute.Top.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                toHome = {
                    navController.navigate(NavigationRoute.Top.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        dialog(NavigationRoute.NameRegister.route) { navBackStackEntry ->
            NameRegisterViewBuilder(
                factory = factory,
                score = NavigationRoute.getScore(navBackStackEntry),
                onClose = { result ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SavedStateHandleKeys.NAME_REGISTER_RESULT, result)
                    navController.popBackStack()
                }
            )
        }
    }
}