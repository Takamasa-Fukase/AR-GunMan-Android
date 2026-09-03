package com.takamasafukase.ar_gunman_android.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.ui.theme.copperplate

@Composable
fun SettingsView(
    viewModel: SettingsViewModel,
    showRankingView: () -> Unit,
    onClose: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.outputEvent.collect { eventType ->
            when (eventType) {
                SettingsViewModel.OutputEventType.ShowRankingView -> {
                    showRankingView()
                }
                is SettingsViewModel.OutputEventType.OpenUrl -> {
                    uriHandler.openUri(eventType.urlString)
                }
                SettingsViewModel.OutputEventType.Close -> {
                    onClose()
                }
            }
        }
    }

    Surface(
        color = colorResource(id = R.color.goldLeaf),
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 32.dp,
                    top = (screenHeight * 0.1).dp,
                    end = 32.dp,
                    bottom = (screenHeight * 0.08).dp
                )
        ) {
            Text(
                text = "Settings",
                color = colorResource(id = R.color.blackSteel),
                fontSize = (screenHeight * 0.08).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = copperplate,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextButton(onClick = {
                    viewModel.onTapRankingButton()
                }) {
                    Text(
                        text = "World Ranking",
                        color = colorResource(id = R.color.blackSteel),
                        fontSize = (screenHeight * 0.075).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = copperplate,
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
                TextButton(onClick = {
                    viewModel.onTapPrivacyPolicyButton()
                }) {
                    Text(
                        text = "Privacy Policy",
                        color = colorResource(id = R.color.blackSteel),
                        fontSize = (screenHeight * 0.075).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = copperplate,
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
                TextButton(onClick = {
                    viewModel.onTapContactDeveloperButton()
                }) {
                    Text(
                        text = "Contact Developer",
                        color = colorResource(id = R.color.blackSteel),
                        fontSize = (screenHeight * 0.07875).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = copperplate,
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
            }
            TextButton(onClick = {
                viewModel.onTapBackButton()
            }) {
                Text(
                    text = "Back",
                    color = colorResource(id = R.color.blackSteel),
                    fontSize = (screenHeight * 0.0675).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = copperplate,
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
            }
        }
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 640, heightDp = 360)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 730, heightDp = 410)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 864, heightDp = 359)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 869, heightDp = 411)
@Composable
fun SettingsViewPreview() {
    SettingsView(
        viewModel = SettingsViewModel(),
        showRankingView = {},
        onClose = {},
    )
}