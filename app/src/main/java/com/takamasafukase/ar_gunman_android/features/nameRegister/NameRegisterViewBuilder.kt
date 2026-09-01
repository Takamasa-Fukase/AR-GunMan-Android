package com.takamasafukase.ar_gunman_android.features.nameRegister

import androidx.compose.runtime.Composable
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.takamasafukase.ar_gunman_android.factories.Factory

@Composable
fun NameRegisterViewBuilder(
    factory: Factory,
    onClose: (registeredRankingItem: RankingItem?) -> Unit
) {
    val vmFactory = viewModelFactory {
        initializer {
            val savedStateHandle = createSavedStateHandle()
            NameRegisterViewModel(
                savedStateHandle = savedStateHandle,
                rankingRegisterUseCase = factory.createRankingRegisterUseCase(),
                rankingStore = factory.createRankingStore(),
            )
        }
    }
    val viewModel: NameRegisterViewModel = viewModel(factory = vmFactory)
    NameRegisterView(
        viewModel = viewModel,
        onClose = onClose,
    )
}