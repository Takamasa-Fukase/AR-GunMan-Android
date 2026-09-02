package com.takamasafukase.ar_gunman_android.features.top

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar_gunman_android.device.cameraPermission.CameraPermissionHandlerInterface
import com.ar_gunman_android.device.sound.SoundPlayerInterface
import com.ar_gunman_android.device.sound.SoundType
import com.takamasafukase.ar_gunman_android.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TopViewModel(
    private val cameraPermissionHandler: CameraPermissionHandlerInterface,
    private val soundPlayer: SoundPlayerInterface,
) : ViewModel() {
    data class UIState(
        val startButtonImageId: Int = 0,
        val settingsButtonImageId: Int = 0,
        val howToPlayButtonImageId: Int = 0,
        val isPermissionDescriptionDialogPresented: Boolean = false,
    )
    enum class OutputEventType {
        SHOW_GAME_VIEW,
        SHOW_TUTORIAL_VIEW,
        SHOW_SETTINGS_VIEW,
        SHOW_DEVICE_SETTINGS;
    }
    sealed class IconButtonType {
        object Start : IconButtonType()
        object Settings : IconButtonType()
        object HowToPlay : IconButtonType()
    }

    val uiState get() = _uiState.asStateFlow()
    val outputEvent get() = _outputEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(UIState())
    private val _outputEvent = MutableSharedFlow<OutputEventType>()

    fun onViewAppear() {
        cameraPermissionHandler.requestCameraUsagePermission()
    }

    fun onTapStartButton() {
        switchButtonIconAndRevert(type = IconButtonType.Start)
    }

    fun onTapSettingsButton() {
        switchButtonIconAndRevert(type = IconButtonType.Settings)
    }

    fun onTapHowToPlayButton() {
        switchButtonIconAndRevert(type = IconButtonType.HowToPlay)
    }

    fun onTapConfirmButtonOfPermissionDescriptionDialog() {
        _uiState.value = _uiState.value.copy(isPermissionDescriptionDialogPresented = false)
        viewModelScope.launch {
            _outputEvent.emit(OutputEventType.SHOW_DEVICE_SETTINGS)
        }
    }

    fun onClosePermissionDescriptionDialog() {
        _uiState.value = _uiState.value.copy(isPermissionDescriptionDialogPresented = false)
    }

    private fun switchButtonIconAndRevert(type: IconButtonType) {
        // ウエスタン風な銃声の再生
        soundPlayer.play(SoundType.WESTERN_PISTOL_FIRE)
        // 対象のボタンに弾痕の画像を表示
        when (type) {
            IconButtonType.Start -> {
                _uiState.value = _uiState.value.copy(
                    startButtonImageId = R.drawable.bullets_hole
                )
            }
            IconButtonType.Settings -> {
                _uiState.value = _uiState.value.copy(
                    settingsButtonImageId = R.drawable.bullets_hole
                )
            }
            IconButtonType.HowToPlay -> {
                _uiState.value = _uiState.value.copy(
                    howToPlayButtonImageId = R.drawable.bullets_hole
                )
            }
        }
        viewModelScope.launch {
            // 0.5秒待機
            delay(timeMillis = 500)

            // 画像を元の的に戻す
            _uiState.value = _uiState.value.copy(
                startButtonImageId = R.drawable.target_icon,
                settingsButtonImageId = R.drawable.target_icon,
                howToPlayButtonImageId = R.drawable.target_icon,
            )

            // 対象のボタンごとの遷移指示を流す
            when (type) {
                IconButtonType.Start -> {
                    val isCameraPermissionGranted = cameraPermissionHandler.getCameraUsagePermissionGrantedFlag()
                    if (isCameraPermissionGranted) {
                        _outputEvent.emit(OutputEventType.SHOW_GAME_VIEW)
                    } else {
                        _uiState.value = _uiState.value.copy(isPermissionDescriptionDialogPresented = true)
                    }
                }
                IconButtonType.Settings -> {
                    _outputEvent.emit(OutputEventType.SHOW_SETTINGS_VIEW)
                }
                IconButtonType.HowToPlay -> {
                    _outputEvent.emit(OutputEventType.SHOW_TUTORIAL_VIEW)
                }
            }
        }
    }
}