package com.takamasafukase.ar_gunman_android.features.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.takamasafukase.ar_gunman_android.features.game.GameViewModel

@Composable
fun SettingsViewBuilder(
    onClose: () -> Unit
) {
    val vmFactory = viewModelFactory {
        initializer {
            SettingsViewModel()
        }
    }
    val viewModel: SettingsViewModel = viewModel(factory = vmFactory)
    SettingsView(
        viewModel = viewModel,
        onClose = onClose,
    )
}