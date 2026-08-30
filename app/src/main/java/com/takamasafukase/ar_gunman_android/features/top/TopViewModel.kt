package com.takamasafukase.ar_gunman_android.features.top

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ar_gunman_android.device.cameraPermission.CameraPermissionHandlerInterface
import com.ar_gunman_android.device.sound.SoundPlayerInterface
import com.ar_gunman_android.device.sound.SoundType
import com.takamasafukase.ar_gunman_android.R
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TopViewState(
    val startButtonImageResourceId: Int,
    val settingsButtonImageResourceId: Int,
    val howToPlayButtonImageResourceId: Int,
    val isShowTutorialDialog: Boolean,
    val isShowPermissionDescriptionDialog: Boolean,
)

class TopViewModel(
    app: Application,
    private val cameraPermissionHandler: CameraPermissionHandlerInterface,
    private val soundPlayer: SoundPlayerInterface,
) : AndroidViewModel(app) {

    sealed class IconButtonType {
        object Start : IconButtonType()
        object Settings : IconButtonType()
        object HowToPlay : IconButtonType()
    }

    private val _state = MutableStateFlow(
        TopViewState(
            startButtonImageResourceId = R.drawable.target_icon,
            settingsButtonImageResourceId = R.drawable.target_icon,
            howToPlayButtonImageResourceId = R.drawable.target_icon,
            isShowTutorialDialog = false,
            isShowPermissionDescriptionDialog = false,
        )
    )
    val state = _state.asStateFlow()

    private val _showGame = MutableSharedFlow<Unit>()
    val showGame = _showGame.asSharedFlow()
    private val _showSetting = MutableSharedFlow<Unit>()
    val showSetting = _showSetting.asSharedFlow()
    private val _showDeviceSetting = MutableSharedFlow<Unit>()
    val showDeviceSetting = _showDeviceSetting.asSharedFlow()

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

    fun onCloseTutorialDialog() {
        _state.value = _state.value.copy(isShowTutorialDialog = false)
    }

    fun onTapConfirmButtonOfPermissionDescriptionDialog() {
        viewModelScope.launch {
            _showDeviceSetting.emit(Unit)
            _state.value = _state.value.copy(isShowPermissionDescriptionDialog = false)
        }
    }

    fun onClosePermissionDescriptionDialog() {
        _state.value = _state.value.copy(isShowPermissionDescriptionDialog = false)
    }

    private fun switchButtonIconAndRevert(type: IconButtonType) {
        // ウエスタン風な銃声の再生
        soundPlayer.play(SoundType.WESTERN_PISTOL_FIRE)
        // 対象のボタンに弾痕の画像を表示
        when (type) {
            IconButtonType.Start -> {
                _state.value = _state.value.copy(
                    startButtonImageResourceId = R.drawable.bullets_hole
                )
            }
            IconButtonType.Settings -> {
                _state.value = _state.value.copy(
                    settingsButtonImageResourceId = R.drawable.bullets_hole
                )
            }
            IconButtonType.HowToPlay -> {
                _state.value = _state.value.copy(
                    howToPlayButtonImageResourceId = R.drawable.bullets_hole
                )
            }
        }
        // 0.5秒後の処理
        Handler(Looper.getMainLooper()).postDelayed({
            // 画像を元の的に戻す
            _state.value = _state.value.copy(
                startButtonImageResourceId = R.drawable.target_icon,
                settingsButtonImageResourceId = R.drawable.target_icon,
                howToPlayButtonImageResourceId = R.drawable.target_icon,
            )
            // 対象のボタンごとの遷移指示を流す
            viewModelScope.launch {
                when (type) {
                    IconButtonType.Start -> {
                        val isCameraPermissionGranted = cameraPermissionHandler.getCameraUsagePermissionGrantedFlag()
                        if (isCameraPermissionGranted) {
                            viewModelScope.launch {
                                _showGame.emit(Unit)
                            }
                        } else {
                            _state.value = _state.value.copy(isShowPermissionDescriptionDialog = true)
                        }
                    }
                    IconButtonType.Settings -> {
                        _showSetting.emit(Unit)
                    }
                    IconButtonType.HowToPlay -> {
                        _state.value = _state.value.copy(isShowTutorialDialog = true)
                    }
                }
            }
        }, 500)
    }
}