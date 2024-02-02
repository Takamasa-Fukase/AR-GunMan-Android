package com.takamasafukase.ar_gunman_android.view.tutorial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.const.TutorialConst
import com.takamasafukase.ar_gunman_android.ui.theme.copperplate
import com.takamasafukase.ar_gunman_android.utility.CustomDialog
import com.takamasafukase.ar_gunman_android.utility.ImageSwitcherAnimation
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialScreen(
    onClose: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val pagerViewHeight = screenHeight * 0.65
    val pageIndicatorHeight = screenHeight * 0.072
    val buttonHeight = screenHeight * 0.15
    val dialogHeight = pagerViewHeight + pageIndicatorHeight + buttonHeight
    val dialogWidth = pagerViewHeight * 1.33
    val pagerState = rememberPagerState()
    val rememberCoroutineScope = rememberCoroutineScope()

    CustomDialog(
        onDismissRequest = onClose,
        size = DpSize(
            width = dialogWidth.dp,
            height = dialogHeight.dp,
        ),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.Transparent)
            ) {

                // ページャービュー
                HorizontalPager(
                    pageCount = TutorialConst.pageContents.size,
                    state = pagerState,
                    modifier = Modifier
                        .height((screenHeight * 0.6).dp)
                        .border(
                            width = 5.dp,
                            color = colorResource(id = R.color.goldLeaf),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        // 見た目上の角丸に合わせて、コンテンツ描画の切り取り領域も同じ角丸の値に設定する
                        // これをやらないとスクロールした時に角丸部分に中身のコンテンツがはみ出して見えてしまう。
                        .clip(RoundedCornerShape(20.dp))
                ) { pageIndex ->
                    val content = TutorialConst.pageContents[pageIndex]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                    ) {
                        ImageSwitcherAnimation(
                            imageResourceIds = content.imageResourceIds,
                            duration = 400,
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 4.dp)
                        )
                        Text(
                            text = content.titleText,
                            color = colorResource(id = R.color.blackSteel),
                            fontSize = (screenHeight * 0.05).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = copperplate,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = content.descriptionText,
                            color = colorResource(id = R.color.blackSteel),
                            fontSize = (screenHeight * 0.03).sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = copperplate,
                        )
                    }
                }

                // ページインジケーター
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(pageIndicatorHeight.dp)
                ) {
                    repeat(TutorialConst.pageContents.size) { index ->
                        val color = if (pagerState.currentPage == index)
                            colorResource(id = R.color.paper)
                        else
                            Color.Gray

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color = color)
                                .size(8.dp)
                        )
                    }
                }

                // ボタン
                TextButton(
                    onClick = {
                        if (pagerState.currentPage == 2) {
                            // ダイアログを閉じる
                            onClose()
                        } else {
                            // ButtonClickイベント内など、Composableではない通常のUIスレッドとして扱われる部分でCoroutineを使う場合にこれを使う。
                            rememberCoroutineScope.launch {
                                // 次のページへスクロールさせる
                                pagerState.animateScrollToPage(
                                    page = pagerState.currentPage + 1
                                )
                            }
                        }
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = (buttonHeight * 2.3).dp,
                                height = buttonHeight.dp
                            )
                            .background(
                                color = colorResource(id = R.color.goldLeaf),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = colorResource(id = R.color.customBrown1),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        val buttonText = if (pagerState.currentPage == 2) "OK" else "NEXT"
                        Text(
                            text = buttonText,
                            color = colorResource(id = R.color.blackSteel),
                            fontSize = (screenHeight * 0.042).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = copperplate,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    )
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun TutorialScreenPreview() {
    TutorialScreen(
        onClose = {},
    )
}