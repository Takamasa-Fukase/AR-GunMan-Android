package com.takamasafukase.ar_gunman_android.utility

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.takamasafukase.ar_gunman_android.constants.TutorialConst
import kotlinx.coroutines.delay

@Composable
fun ImageSwitcherAnimation(
    imageResourceIds: List<Int>,
    duration: Long,
    modifier: Modifier,
) {
    var currentImageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            // 指定された時間分遅延させる
            delay(duration)
            // 次の画像の番号にインクリメント
            currentImageIndex++

            // もしリストの最後に達したら最初の画像の番号に戻す
            if (currentImageIndex >= imageResourceIds.size) {
                currentImageIndex = 0
            }
        }
    }

    Image(
        painter = painterResource(id = imageResourceIds[currentImageIndex]),
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun ImageSwitcherAnimationPreview() {
    ImageSwitcherAnimation(
        imageResourceIds = TutorialConst.pageContents.first().imageResourceIds,
        duration = 400,
        modifier = Modifier
    )
}