package com.takamasafukase.ar_gunman_android.view.ranking

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.entity.Ranking
import com.takamasafukase.ar_gunman_android.ui.theme.copperplate
import kotlinx.coroutines.delay

@Composable
fun RankingItem(
    rankIndex: Int,
    ranking: Ranking,
    isHighlighted: Boolean = false,
) {
    Box(
        // ベースとなる領域を確保
        modifier = Modifier
            .fillMaxSize()
            .height(height = 56.dp)
            .background(Color.Transparent)
    ) {
        // 背景1
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .background(
                    color = colorResource(id = R.color.customBrown1),
                )
                .border(
                    width = 4.dp,
                    color = colorResource(id = R.color.goldLeaf),
                    shape = RoundedCornerShape(3.dp)
                )
        )
        // 四隅の白い点
        whiteCornerSquares()
        // 背景2
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp, vertical = 10.dp)
                .background(colorResource(id = R.color.customBrown2))
        )
        // ハイライト時に表示
        if (isHighlighted) {
            AnimatedAlphaView()
        }
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            // 順位
            Text(
                rankIndex.toString(),
                color = colorResource(id = R.color.paper),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = copperplate,
                modifier = Modifier
                    .padding(start = 24.dp, bottom = 6.dp)
                    .size(width = 32.dp, height = 22.dp)
                    .background(colorResource(id = R.color.goldLeaf))
                    .wrapContentSize(align = Alignment.Center, unbounded = true)
            )
            // スコア
            Text(
                text = "%.3f".format(ranking.score),
                color = colorResource(id = R.color.paper),
                fontSize = 16.sp,
                fontFamily = copperplate,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 10.dp)
            )
            // ユーザー名
            Text(
                text = ranking.user_name,
                color = colorResource(id = R.color.paper),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = copperplate,
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 10.dp)
            )
        }
    }
}

@Composable
fun whiteCornerSquares() {
    Box(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(width = 4.dp, height = 2.dp)
                .offset(x = 18.dp, y = 10.dp)
                .background(
                    color = colorResource(id = R.color.paper),
                )
        )
        Spacer(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 4.dp, height = 2.dp)
                .offset(x = (-18).dp, y = 10.dp)
                .background(
                    color = colorResource(id = R.color.paper),
                )
        )
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(width = 4.dp, height = 2.dp)
                .offset(x = 18.dp, y = (-10).dp)
                .background(
                    color = colorResource(id = R.color.paper),
                )
        )
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(width = 4.dp, height = 2.dp)
                .offset(x = (-18).dp, y = (-10).dp)
                .background(
                    color = colorResource(id = R.color.paper),
                )
        )
    }
}

@Composable
fun AnimatedAlphaView() {
    var startAnimation by remember { mutableStateOf(false) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 0.7f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "",
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(800)
        startAnimation = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .alpha(animatedAlpha)
            .background(colorResource(id = R.color.goldLeaf))
    )
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun RankingItemPreview() {
    val dummyRanking = Ranking(score = 98.765, user_name = "ウルトラ深瀬")
    RankingItem(rankIndex = 1, ranking = dummyRanking)
}