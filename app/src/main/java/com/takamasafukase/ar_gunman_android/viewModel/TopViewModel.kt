package com.takamasafukase.ar_gunman_android.viewModel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takamasafukase.ar_gunman_android.manager.AudioManager
import com.takamasafukase.ar_gunman_android.R
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TopViewState(
    val startButtonImageResourceId: Int,
    val settingsButtonImageResourceId: Int,
    val howToPlayButtonImageResourceId: Int,
    val isShowTutorialDialog: Boolean,
)

class TopViewModel(
    private val audioManager: AudioManager
) : ViewModel() {

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
        )
    )
    val state = _state.asStateFlow()

    private val _showGame = MutableSharedFlow<Unit>()
    val showGame = _showGame.asSharedFlow()
    private val _showSetting = MutableSharedFlow<Unit>()
    val showSetting = _showSetting.asSharedFlow()

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

    private fun switchButtonIconAndRevert(type: IconButtonType) {
        // ウエスタン風な銃声の再生
        audioManager.playSound(R.raw.western_pistol_shoot)
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
                        _showGame.emit(Unit)
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