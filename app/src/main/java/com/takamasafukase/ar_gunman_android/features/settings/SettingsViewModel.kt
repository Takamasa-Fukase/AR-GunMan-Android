package com.takamasafukase.ar_gunman_android.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takamasafukase.ar_gunman_android.constants.URLConst
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {
    sealed interface OutputEventType {
        object ShowRankingView : OutputEventType
        data class OpenUrl(val urlString: String) : OutputEventType
        object Close : OutputEventType
    }

    val outputEvent get() = _outputEvent.asSharedFlow()

    private val _outputEvent = MutableSharedFlow<OutputEventType>()

    fun onTapRankingButton() {
        viewModelScope.launch {
            _outputEvent.emit(OutputEventType.ShowRankingView)
        }
    }

    fun onTapPrivacyPolicyButton() {
        viewModelScope.launch {
            _outputEvent.emit(OutputEventType.OpenUrl(URLConst.PRIVACY_POLICY))
        }
    }

    fun onTapContactDeveloperButton() {
        viewModelScope.launch {
            _outputEvent.emit(OutputEventType.OpenUrl(URLConst.DEVELOPER_CONTACT))
        }
    }

    fun onTapBackButton() {
        viewModelScope.launch {
            _outputEvent.emit(OutputEventType.Close)
        }
    }
}