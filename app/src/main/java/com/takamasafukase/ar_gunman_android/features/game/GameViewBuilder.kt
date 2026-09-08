package com.takamasafukase.ar_gunman_android.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.takamasafukase.ar_gunman_android.factories.Factory

@Composable
fun GameViewBuilder(
    factory: Factory,
    savedStateHandle: SavedStateHandle,
    showTutorialView: () -> Unit,
    showWeaponSelectView: () -> Unit,
    closeWeaponSelectView: () -> Unit,
    showResultView: (score: Double) -> Unit,
) {
    val (arShootingEngineHandler, arView) = remember(factory) {
        factory.createARShootingEngineHandler()
    }

    val vmFactory = viewModelFactory {
        initializer {
            val gameFlowDriveUseCase = factory.createGameFlowDriveUseCase()
            val weaponReloadUseCase = factory.createWeaponReloadUseCase()
            GameViewModel(
                savedStateHandle = savedStateHandle,
                arShootingEngineHandler = arShootingEngineHandler,
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
                gameFlowDriveUseCase = gameFlowDriveUseCase,
                scoreAddUseCase = factory.createScoreAddUseCase(),
                reloadingMotionCountUpdateUseCase = factory.createReloadingMotionCountUpdateUseCase(),
                weaponControlMotionDetectUseCase = factory.createWeaponControlMotionDetectUseCase(),
            )
        }
    }
    val viewModel: GameViewModel = viewModel(factory = vmFactory)
    GameView(
        viewModel = viewModel,
        arView = arView,
        showTutorialView = showTutorialView,
        showWeaponSelectView = showWeaponSelectView,
        closeWeaponSelectView = closeWeaponSelectView,
        showResultView = showResultView
    )
}