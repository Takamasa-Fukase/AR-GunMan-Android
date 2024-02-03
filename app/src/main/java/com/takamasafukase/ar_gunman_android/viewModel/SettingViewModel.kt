package com.takamasafukase.ar_gunman_android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takamasafukase.ar_gunman_android.const.UrlConst
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel: ViewModel() {
    private val isShowRankingDialogFlow = MutableStateFlow(false)
    val isShowRankingDialog = isShowRankingDialogFlow.asStateFlow()
    private val openUrlInBrowserFlow = MutableSharedFlow<String>()
    val openUrlInBrowserEvent = openUrlInBrowserFlow.asSharedFlow()
    private val closePageFlow = MutableSharedFlow<Unit>()
    val closePageEvent = closePageFlow.asSharedFlow()

    fun onTapRankingButton() {
        viewModelScope.launch {
            isShowRankingDialogFlow.emit(true)
        }
    }

    fun onCloseRankingDialog() {
        viewModelScope.launch {
            isShowRankingDialogFlow.emit(false)
        }
    }

    fun onTapPrivacyPolicyButton() {
        viewModelScope.launch {
            openUrlInBrowserFlow.emit(UrlConst.privacyPolicyUrl)
        }
    }

    fun onTapContactDeveloperButton() {
        viewModelScope.launch {
            openUrlInBrowserFlow.emit(UrlConst.developerContactUrl)
        }
    }

    fun onTapBackButton() {
        viewModelScope.launch {
            closePageFlow.emit(Unit)
        }
    }
}