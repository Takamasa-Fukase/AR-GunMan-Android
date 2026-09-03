package com.takamasafukase.ar_gunman_android.features.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.R

@Composable
fun GameView(
    viewModel: GameViewModel,
    showTutorialView: () -> Unit,
    showWeaponSelectView: () -> Unit,
    closeWeaponSelectView: () -> Unit,
    showResultView: (score: Double) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onViewAppear()
    }

    LaunchedEffect(Unit) {
        viewModel.outputEvent.collect { eventType ->
            when (eventType) {
                GameViewModel.OutputEventType.ShowTutorialView -> {
                    showTutorialView()
                }
                GameViewModel.OutputEventType.ShowWeaponSelectView -> {
                    showWeaponSelectView()
                }
                GameViewModel.OutputEventType.CloseWeaponSelectView -> {
                    closeWeaponSelectView()
                }
                is GameViewModel.OutputEventType.ShowResultView -> {
                    showResultView(eventType.score)
                }
            }
        }
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                    text = uiState.timeCountText,
                    color = colorResource(id = R.color.paper),
                    fontSize = (screenHeight * 0.09).sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            // 中央の照準アイコン
            Image(
                painter = painterResource(id = R.drawable.pistol_sight),
                colorFilter = ColorFilter.tint(Color.Red),
                contentDescription = "Pistol sight",
                modifier = Modifier
                    .size(size = (screenHeight / 4).dp)
            )
            // 弾数表示の画像
            Image(
                painter = painterResource(
                    id = LocalContext.current.resources.getIdentifier(
                        uiState.bulletsCountImageName,
                        "drawable",
                        LocalContext.current.packageName,
                    )
                ),
                contentDescription = "Pistol bullets",
                modifier = Modifier
                    .size(width = (screenWidth / 4.28).dp, height = (screenHeight / 5.71).dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (screenWidth / 45).dp, y = (-(screenHeight / 12)).dp)
            )

            // TODO: isEnabledを繋ぎこむ
            // 武器切り替えボタン
            IconButton(
                onClick = {
                    viewModel.weaponChangeButtonTapped()
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
}