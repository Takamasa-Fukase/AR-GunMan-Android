package com.takamasafukase.ar_gunman_android.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.takamasafukase.ar_gunman_android.factories.Factory

@Composable
fun GameViewBuilder(
    factory: Factory,
    toResult: (score: Double) -> Unit,
) {
    val rememberCoroutineScope = rememberCoroutineScope()
    val vmFactory = viewModelFactory {
        initializer {
            val weaponReloadUseCase = factory.createWeaponReloadUseCase(
                scope = rememberCoroutineScope
            )
            GameViewModel(
                arShootingEngineHandler = factory.createARShootingEngineHandler(),
                motionSensorHandler = factory.createMotionSensorHandler(),
                soundPlayer = factory.createSoundPlayer(),
                gameStore = factory.createGameStore(),
                weaponStore = factory.createWeaponStore(),
                weaponFireUseCase = factory.createWeaponFireUseCase(
                    weaponReloadUseCase = weaponReloadUseCase
                ),
                weaponReloadUseCase = weaponReloadUseCase,
                weaponChangeUseCase = factory.createWeaponChangeUseCase(
                    weaponReloadUseCase = weaponReloadUseCase
                ),
                gameFlowDriveUseCase = factory.createGameFlowDriveUseCase(
                    scope = rememberCoroutineScope
                ),
                scoreAddUseCase = factory.createScoreAddUseCase(),
                reloadingMotionCountUpdateUseCase = factory.createReloadingMotionCountUpdateUseCase(),
                weaponControlMotionDetectUseCase = factory.createWeaponControlMotionDetectUseCase(),
            )
        }
    }
    val viewModel: GameViewModel = viewModel(factory = vmFactory)
    GameView(
        viewModel = viewModel,
        toResult = toResult,
    )
}