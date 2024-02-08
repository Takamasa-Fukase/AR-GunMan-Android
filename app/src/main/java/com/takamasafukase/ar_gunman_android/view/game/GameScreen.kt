package com.takamasafukase.ar_gunman_android.view.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.view.tutorial.TutorialScreen
import com.takamasafukase.ar_gunman_android.view.weaponChange.WeaponChangeScreen
import com.takamasafukase.ar_gunman_android.viewModel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    toResult: (totalScore: Double) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val state by viewModel.state.collectAsState()
    val showResultEvent = viewModel.showResult.collectAsState(initial = null)

    LaunchedEffect(showResultEvent.value) {
        showResultEvent.value?.let {
            toResult(it)
        }
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(id = R.color.blackSteel))
            ) {
                CircularProgressIndicator(color = colorResource(id = R.color.paper))
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
        ) {
            // タイマービュー
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(width = (screenWidth / 7.5).dp, height = (screenHeight / 8).dp)
                    .offset(x = (screenWidth / 20).dp, y = (screenHeight / 13.3).dp)
                    .background(
                        color = colorResource(id = R.color.goldLeaf).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = colorResource(id = R.color.customBrown1).copy(0.6F),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {
                Text(
                    text = state.timeCountText,
                    color = colorResource(id = R.color.paper),
                    fontSize = (screenHeight * 0.09).sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            // ローディング中はインジケータと被って見ずらいので非表示
            if (!state.isLoading) {
                // 中央の照準アイコン
                Image(
                    painter = painterResource(id = R.drawable.pistol_sight),
                    colorFilter = ColorFilter.tint(Color.Red),
                    contentDescription = "Pistol sight",
                    modifier = Modifier
                        .size(size = (screenHeight / 4).dp)
                )
            }
            // 弾数表示の画像
            Image(
                painter = painterResource(id = state.bulletsCountImageResourceId),
                contentDescription = "Pistol bullets",
                modifier = Modifier
                    .size(width = (screenWidth / 4.28).dp, height = (screenHeight / 5.71).dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (screenWidth / 45).dp, y = (-(screenHeight / 12)).dp)
            )
            // 武器切り替えボタン
            IconButton(
                onClick = {
                    // ローディング中は押せなくする
                    if (!state.isLoading) {
                        viewModel.onTapWeaponChangeButton()
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(size = (screenHeight / 4).dp)
                    .offset(x = -(screenWidth / 15).dp, y = (screenHeight / 13.3).dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.weapon_switch_icon),
                    contentDescription = "Weapon change icon",
                    modifier = Modifier
                )
            }
        }
    }

    // チュートリアルダイアログ
    if (state.isShowTutorialDialog) {
        TutorialScreen(
            onClose = {
                viewModel.onCloseTutorialDialog()
            }
        )
    }

    // 武器選択画面ダイアログ
    if (state.isShowWeaponChangeDialog) {
        WeaponChangeScreen(
            onClose = {
                viewModel.onCloseWeaponChangeDialog()
            },
            onSelectWeapon = {
                viewModel.onSelectWeapon(it)
            }
        )
    }
}