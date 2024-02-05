package com.takamasafukase.ar_gunman_android.view.weaponChange

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.model.WeaponType
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.utility.CustomDialog

@Composable
fun WeaponChangeScreen(
    onClose: () -> Unit,
    onSelectWeapon: (selectedWeapon: WeaponType) -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    CustomDialog(
        onDismissRequest = onClose,
        size = DpSize(
            width = screenWidth.dp,
            height = screenHeight.dp
        ),
        content = {
            Surface(
                color = Color.Transparent
            ) {
                Box {
                    WeaponListView(onSelectWeapon)
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                        onClick = {
                            onClose()
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(end = 24.dp)
                        ) {
                            Image(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close Icon",
                                colorFilter = ColorFilter.tint(colorResource(id = R.color.paper)),
                                modifier = Modifier
                                    .size(32.dp)
                            )
                            Text(
                                text = "CLOSE",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.paper)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun WeaponListView(
    onSelectWeapon: (selectedWeapon: WeaponType) -> Unit
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(WeaponType.values()) {
            WeaponListItem(
                type = it,
                onTapItem = {
                    onSelectWeapon(it)
                }
            )
        }
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 640, heightDp = 360)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 730, heightDp = 410)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 864, heightDp = 359)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 869, heightDp = 411)
@Composable
fun WeaponChangeScreenPreview() {
    WeaponChangeScreen(
        onClose = {},
        onSelectWeapon = {}
    )
}