package com.takamasafukase.ar_gunman_android.features.result

import androidx.compose.runtime.Composable
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.takamasafukase.ar_gunman_android.factories.Factory

@Composable
fun ResultViewBuilder(
    factory: Factory,
    onReplay: () -> Unit,
    toHome: () -> Unit,
) {
    val vmFactory = viewModelFactory {
        initializer {
            val savedStateHandle = createSavedStateHandle()
            ResultViewModel(
                savedStateHandle = savedStateHandle,
                soundPlayer = factory.createSoundPlayer(),
                rankingGetUseCase = factory.createRankingGetUseCase(),
                rankingStore = factory.createRankingStore(),
            )
        }
    }
    val viewModel: ResultViewModel = viewModel(factory = vmFactory)
    ResultView(
        viewModel = viewModel,
        onReplay = onReplay,
        toHome = toHome,
    )
}