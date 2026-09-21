package com.takamasafukase.ar_gunman_android

import androidx.navigation.NavBackStackEntry

sealed class NavigationRoute(
    val route: String
) {
    object Top : NavigationRoute("top")
    object Tutorial : NavigationRoute("tutorial")
    object Settings : NavigationRoute("settings")
    object Ranking : NavigationRoute("ranking")
    object Game : NavigationRoute("game")
    object WeaponSelect : NavigationRoute("weaponSelect")
    object Result : NavigationRoute("result/{$SCORE}") {
        fun createRoute(score: Double): String {
            return "result/$score"
        }
    }
    object NameRegister : NavigationRoute("nameRegister/{$SCORE}") {
        fun createRoute(score: Double): String {
            return "nameRegister/$score"
        }
    }

    companion object {
        const val SCORE = "score"

        fun getScore(entry: NavBackStackEntry): Double {
            return entry.arguments?.getString(SCORE)?.toDoubleOrNull() ?: 0.0
        }
    }
}