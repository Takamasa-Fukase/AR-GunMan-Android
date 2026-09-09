package com.takamasafukase.ar_gunman_android.utility

import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


// カスタムダイアログの作り方参考：https://stackoverflow.com/questions/69160300/how-to-change-the-width-of-dialog-in-android-compose
// 透明背景の作り方参考：https://stackoverflow.com/questions/66942587/how-to-make-a-surface-background-half-transparent-in-jetpack-compose-but-not-th
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
    size: DpSize,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties.let {
            DialogProperties(
                dismissOnBackPress = it.dismissOnBackPress,
                dismissOnClickOutside = it.dismissOnClickOutside,
                securePolicy = it.securePolicy,
                usePlatformDefaultWidth = false,
            )
        },
        content = {
            // ここでもSurfaceを設置しないと領域外タップでのcloseが発動しない（一旦こう書いている）
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .size(size),
                content = content
            )
        }
    )
}
