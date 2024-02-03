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
import com.takamasafukase.ar_gunman_android.view.ranking.RankingScreen
import com.takamasafukase.ar_gunman_android.view.result.ResultScreen
import com.takamasafukase.ar_gunman_android.viewModel.RankingViewModel
import com.takamasafukase.ar_gunman_android.viewModel.ResultViewModel
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
            modifier = Modifier
                .padding(horizontal = 60.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingButton(
                    screenHeight = screenHeight,
                    onTap = {
                        viewModel.onTapSettingButton()
                    })
                Spacer(modifier = Modifier.weight(1f))
                TitleText(screenHeight = screenHeight)
            }
            Row {
                Column(
                    modifier = Modifier
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
                        title = "Ranking",
                        iconResourceId = state.value.rankingButtonImageResourceId,
                        onTap = {
                            viewModel.onTapRankingButton()
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

        // ランキングダイアログ
        if (state.value.isShowRankingDialog) {
            val rankingViewModel = RankingViewModel(app = Application())
            RankingScreen(
                viewModel = rankingViewModel,
                onClose = {
                    viewModel.onCloseRankingDialog()
                }
            )
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
fun TitleText(screenHeight: Int) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "AR",
            fontSize = (screenHeight * 0.1875).sp, // iOSだと固定で100
            fontWeight = FontWeight.Bold,
            fontFamily = copperplate,
        )
        Text(
            text = "-GunMan",
            fontSize = (screenHeight * 0.15).sp,  // iOSだと固定で80
            fontWeight = FontWeight.Bold,
            fontFamily = copperplate,
        )
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