package com.takamasafukase.ar_gunman_android.features.ranking

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.takamasafukase.ar_gunman_android.factories.Factory

@Composable
fun RankingViewBuilder(
    factory: Factory,
    onClose: () -> Unit
) {
    val vmFactory = viewModelFactory {
        initializer {
            RankingViewModel(
                rankingGetUseCase = factory.createRankingGetUseCase(),
                rankingStore = factory.createRankingStore(),
            )
        }
    }
    val viewModel: RankingViewModel = viewModel(factory = vmFactory)
    RankingView(
        viewModel = viewModel,
        onClose = onClose,
    )
}