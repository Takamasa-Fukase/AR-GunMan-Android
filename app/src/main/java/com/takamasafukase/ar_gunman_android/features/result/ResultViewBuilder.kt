package com.takamasafukase.ar_gunman_android.features.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.takamasafukase.ar_gunman_android.constants.SavedStateHandleKeys
import com.takamasafukase.ar_gunman_android.factories.Factory
import com.takamasafukase.ar_gunman_android.features.nameRegister.NameRegisterResult
import kotlinx.coroutines.flow.drop

@Composable
fun ResultViewBuilder(
    factory: Factory,
    navBackStackEntry: NavBackStackEntry,
    score: Double,
    showNameRegisterView: (score: Double) -> Unit,
    onReplay: () -> Unit,
    toHome: () -> Unit,
) {
    val vmFactory = viewModelFactory {
        initializer {
            ResultViewModel(
                score = score,
                soundPlayer = factory.createSoundPlayer(),
                rankingGetUseCase = factory.createRankingGetUseCase(),
                rankingStore = factory.createRankingStore(),
            )
        }
    }
    val viewModel: ResultViewModel = viewModel(factory = vmFactory)

    LaunchedEffect(Unit) {
        navBackStackEntry.savedStateHandle
            .getStateFlow<NameRegisterResult?>(SavedStateHandleKeys.NAME_REGISTER_RESULT, null)
            .drop(1)
            .collect { result ->
                result?.let {
                    if (result.isRegistered) {
                        val rankingItem = RankingItem(
                            score = result.score ?: 0.0,
                            userName = result.userName ?: ""
                        )
                        viewModel.onCloseNameRegisterDialog(registeredRankingItem = rankingItem)
                    } else {
                        viewModel.onCloseNameRegisterDialog(registeredRankingItem = null)
                    }
                }
            }
    }

    ResultView(
        viewModel = viewModel,
        showNameRegisterView = showNameRegisterView,
        onReplay = onReplay,
        toHome = toHome,
    )
}