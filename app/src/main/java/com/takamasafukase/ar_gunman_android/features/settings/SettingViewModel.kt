package com.takamasafukase.ar_gunman_android.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takamasafukase.ar_gunman_android.constants.URLConst

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
            openUrlInBrowserFlow.emit(URLConst.privacyPolicyUrl)
        }
    }

    fun onTapContactDeveloperButton() {
        viewModelScope.launch {
            openUrlInBrowserFlow.emit(URLConst.developerContactUrl)
        }
    }

    fun onTapBackButton() {
        viewModelScope.launch {
            closePageFlow.emit(Unit)
        }
    }
}