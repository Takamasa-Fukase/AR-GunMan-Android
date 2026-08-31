package com.takamasafukase.ar_gunman_android.features.top

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.takamasafukase.ar_gunman_android.factories.Factory

@Composable
fun TopViewBuilder(
    factory: Factory,
    toGame: () -> Unit,
    toSetting: () -> Unit,
    showDeviceSetting: () -> Unit
) {
    val vmFactory = viewModelFactory {
        initializer {
            TopViewModel(
                cameraPermissionHandler = factory.createCameraPermissionHandler(),
                soundPlayer = factory.createSoundPlayer(),
            )
        }
    }
    val viewModel: TopViewModel = viewModel(factory = vmFactory)
    TopView(
        viewModel = viewModel,
        toGame = toGame,
        toSetting = toSetting,
        showDeviceSetting = showDeviceSetting,
    )
}