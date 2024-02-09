package com.takamasafukase.ar_gunman_android.view.top

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.manager.AudioManager
import com.takamasafukase.ar_gunman_android.ui.theme.copperplate
import com.takamasafukase.ar_gunman_android.utility.CameraPermissionDescriptionDialog
import com.takamasafukase.ar_gunman_android.view.tutorial.TutorialScreen
import com.takamasafukase.ar_gunman_android.viewModel.TopViewModel

@Composable
fun TopScreen(
    viewModel: TopViewModel,
    toGame: () -> Unit,
    toSetting: () -> Unit,
    showDeviceSetting: () -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val state = viewModel.state.collectAsState()
    val showGameEvent = viewModel.showGame.collectAsState(initial = null)
    val showSettingEvent = viewModel.showSetting.collectAsState(initial = null)
    val showDeviceSettingEvent = viewModel.showDeviceSetting.collectAsState(initial = null)

    LaunchedEffect(showGameEvent.value) {
        showGameEvent.value?.let {
            toGame()
        }
    }

    LaunchedEffect(showSettingEvent.value) {
        showSettingEvent.value?.let {
            toSetting()
        }
    }

    LaunchedEffect(showDeviceSettingEvent.value) {
        showDeviceSettingEvent.value?.let {
            showDeviceSetting()
        }
    }

    // UIの構築
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.goldLeaf)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(
                    horizontal = (screenWidth * 0.06).dp,
                )
        ) {
            TitleImage(screenWidth, screenHeight)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    CustomIconButton(
                        screenHeight = screenHeight,
                        title = "Start",
                        iconResourceId = state.value.startButtonImageResourceId,
                        onTap = {
                            viewModel.onTapStartButton()
                        }
                    )
                    CustomIconButton(
                        screenHeight = screenHeight,
                        title = "Settings",
                        iconResourceId = state.value.settingsButtonImageResourceId,
                        onTap = {
                            viewModel.onTapSettingsButton()
                        }
                    )
                    CustomIconButton(
                        screenHeight = screenHeight,
                        title = "HowToPlay",
                        iconResourceId = state.value.howToPlayButtonImageResourceId,
                        onTap = {
                            viewModel.onTapHowToPlayButton()
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                PistolImage(screenWidth, screenHeight)
            }
        }

        // チュートリアルダイアログ
        if (state.value.isShowTutorialDialog) {
            TutorialScreen(
                onClose = {
                    viewModel.onCloseTutorialDialog()
                }
            )
        }

        // カメラ権限の再設定を促すダイアログ
        if (state.value.isShowPermissionDescriptionDialog) {
            CameraPermissionDescriptionDialog(
                onTapConfirmButton = {
                    viewModel.onTapConfirmButtonOfPermissionDescriptionDialog()
                },
                onDismissRequest = {
                    viewModel.onClosePermissionDescriptionDialog()
                }
            )
        }
    }
}

@Composable
fun CustomIconButton(
    screenHeight: Int,
    title: String,
    iconResourceId: Int,
    onTap: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TargetImage(
            resourceId = iconResourceId,
            screenHeight = screenHeight,
        )
        TextButton(onClick = {
            onTap()
        }) {
            Text(
                text = title,
                fontSize = (screenHeight * 0.09375).sp, // iOSだと固定で50
                fontWeight = FontWeight.Bold,
                fontFamily = copperplate,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                color = colorResource(id = R.color.blackSteel)
            )
        }
    }
}

@Composable
fun TitleImage(screenWidth: Int, screenHeight: Int) {
    Image(
        painter = painterResource(id = R.drawable.ar_gunman_title_image),
        contentDescription = "AR-GunMan. This is title of this app.",
        modifier = Modifier
            .size(
                width = (screenWidth * 0.7).dp,
                height = (screenHeight * 0.24).dp
            )
    )
}

@Composable
fun PistolImage(screenWidth: Int, screenHeight: Int) {
    Image(
        painter = painterResource(id = R.drawable.top_page_gun_icon),
        contentDescription = "Automatic Pistol Icon",
        modifier = Modifier
            .size(
                width = (screenWidth * 0.36).dp,
                height = (screenHeight * 0.6).dp
            )
    )
}

@Composable
fun TargetImage(resourceId: Int, screenHeight: Int) {
    val size = (screenHeight * 0.12).dp
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = "Target icon",
        modifier = Modifier
            .size(size = size)
    )
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 640, heightDp = 360)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 730, heightDp = 410)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 864, heightDp = 359)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 869, heightDp = 411)
@Composable
fun TopScreenPreview() {
    TopScreen(
        viewModel = TopViewModel(
            app = Application(),
            audioManager = AudioManager(Application()),
        ),
        toGame = {},
        toSetting = {},
        showDeviceSetting = {},
    )
}