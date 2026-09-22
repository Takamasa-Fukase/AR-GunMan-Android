package com.takamasafukase.ar_gunman_android.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import com.ar_gunman_android.domain.entities.weapon.WeaponType
import com.takamasafukase.ar_gunman_android.constants.SavedStateHandleKeys
import com.takamasafukase.ar_gunman_android.factories.Factory
import com.takamasafukase.ar_gunman_android.features.weaponSelect.WeaponSelectResult
import kotlinx.coroutines.flow.drop

@Composable
fun GameViewBuilder(
    factory: Factory,
    navBackStackEntry: NavBackStackEntry,
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

    LaunchedEffect(Unit) {
        navBackStackEntry.savedStateHandle
            .getStateFlow(SavedStateHandleKeys.TUTORIAL_ENDED_EVENT, false)
            .drop(1)
            .collect {
                viewModel.tutorialEnded()
            }
    }

    LaunchedEffect(Unit) {
        navBackStackEntry.savedStateHandle
            .getStateFlow<WeaponSelectResult?>(SavedStateHandleKeys.WEAPON_SELECT_RESULT, null)
            .drop(1)
            .collect { result ->
                result?.let {
                    if (result.isSelected) {
                        viewModel.weaponSelected(weaponType = result.weaponType)
                    } else {
                        viewModel.weaponSelected(weaponType = null)
                    }
                }
                navBackStackEntry.savedStateHandle[SavedStateHandleKeys.WEAPON_SELECT_RESULT] = null
            }
    }

    GameView(
        viewModel = viewModel,
        arView = arView,
        showTutorialView = showTutorialView,
        showWeaponSelectView = showWeaponSelectView,
        closeWeaponSelectView = closeWeaponSelectView,
        showResultView = showResultView
    )
}