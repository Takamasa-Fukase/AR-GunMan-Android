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
import com.takamasafukase.ar_gunman_android.view.tutorial.TutorialScreen
import com.takamasafukase.ar_gunman_android.viewModel.TopViewModel

@Composable
fun TopScreen(
    viewModel: TopViewModel,
    toGame: () -> Unit,
    toSetting: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val state = viewModel.state.collectAsState()
    val showGameEvent = viewModel.showGame.collectAsState(initial = null)
    val showSettingEvent = viewModel.showSetting.collectAsState(initial = null)

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

    // UIの構築
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.goldLeaf)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .padding(horizontal = 60.dp, vertical = 40.dp)
        ) {
            TitleImage()
            Row {
                Column {
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
                PistolImage()
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
    }
}

@Composable
fun SettingButton(
    screenHeight: Int,
    onTap: () -> Unit,
) {
    TextButton(onClick = {
        onTap()
    }) {
        Text(
            text = "Settings",
            fontSize = (screenHeight * 0.05).sp, // iOSだと固定で28
            fontWeight = FontWeight.Bold,
            fontFamily = copperplate,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            color = colorResource(id = R.color.blackSteel)
        )
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
fun TitleImage() {
    Image(
        painter = painterResource(id = R.drawable.ar_gunman_title_image),
        contentDescription = "AR-GunMan. This is title of this app.",
        modifier = Modifier
            .size(width = 560.dp, height = 80.dp)
    )
}

@Composable
fun PistolImage() {
    Image(
        painter = painterResource(id = R.drawable.top_page_gun_icon),
        contentDescription = "Automatic Pistol Icon",
        modifier = Modifier
            .size(width = 300.dp, height = 200.dp)
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

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun TopScreenPreview() {
    TopScreen(
        viewModel = TopViewModel(AudioManager(Application())),
        toGame = {},
        toSetting = {}
    )
}