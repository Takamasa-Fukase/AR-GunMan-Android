package com.takamasafukase.ar_gunman_android.features.nameRegister

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.takamasafukase.ar_gunman_android.factories.Factory

@Composable
fun NameRegisterViewBuilder(
    factory: Factory,
    score: Double,
    onClose: (result: NameRegisterResult) -> Unit
) {
    val vmFactory = viewModelFactory {
        initializer {
            NameRegisterViewModel(
                score = score,
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