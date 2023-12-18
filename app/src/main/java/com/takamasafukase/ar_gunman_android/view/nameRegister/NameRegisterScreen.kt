package com.takamasafukase.ar_gunman_android.view.nameRegister

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takamasafukase.ar_gunman_android.utility.CustomDialog
import com.takamasafukase.ar_gunman_android.R
import kotlinx.coroutines.launch

@Composable
fun NameRegisterScreen(
    totalScore: Double,
    onClose: () -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val rememberCoroutineScope = rememberCoroutineScope()
    var textState by remember { mutableStateOf("") }

    CustomDialog(
        onDismissRequest = onClose,
        size = DpSize(
            width = (screenWidth * 0.54).dp,
            height = (screenHeight * 0.64).dp,
        ),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = colorResource(id = R.color.paper),
                        shape = RoundedCornerShape(5)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 4.dp)
                        .background(
                            color = colorResource(id = R.color.goldLeaf),
                            shape = RoundedCornerShape(5)
                        )
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.paper),
                            shape = RoundedCornerShape(5)
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 12.dp,)
                    ) {
                        Text(
                            text = "CONGRATULATIONS!",
                            color = colorResource(id = R.color.paper),
                            fontSize = (screenHeight * 0.058).sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "YOU'RE RANKED AT 12 / 105 IN",
                            color = colorResource(id = R.color.paper),
                            fontSize = (screenHeight * 0.046).sp,
                        )
                        Text(
                            text = "THE WORLD!",
                            color = colorResource(id = R.color.paper),
                            fontSize = (screenHeight * 0.046).sp,
                        )
                        Text(
                            text = "SCORE: $totalScore",
                            color = colorResource(id = R.color.paper),
                            fontSize = (screenHeight * 0.074).sp,
                            fontWeight = FontWeight.Black,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                        ) {
                            Text(
                                text = "NAME:",
                                color = colorResource(id = R.color.paper),
                                fontSize = (screenHeight * 0.046).sp,
                            )
                            TextField(
                                value = textState,
                                onValueChange = {
                                    textState = it
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = colorResource(id = R.color.paper),
                                    backgroundColor = colorResource(id = R.color.customBrown1),
                                    cursorColor = colorResource(id = R.color.paper),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                ),
                                shape = RoundedCornerShape(12),
                                singleLine = true,
                                trailingIcon = {
                                    if (textState.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                textState = ""
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Close,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(color = colorResource(id = R.color.blackSteel))
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    onClose()
                                }
                            ) {
                                Text(
                                    text = "NO, THANKS",
                                    color = Color.DarkGray,
                                    fontSize = (screenHeight * 0.048).sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .wrapContentSize(align = Alignment.Center, unbounded = true)
                                        .padding(bottom = 4.dp)
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                                    .background(color = colorResource(id = R.color.blackSteel))
                            )
                            TextButton(
                                onClick = {
                                    rememberCoroutineScope.launch {
                                        // TODO: Firestoreに名前とスコアの登録。それが終わったらonClose()
                                        onClose()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxHeight()
                            ) {
                                val buttonColor = if (textState.isEmpty())
                                    Color.LightGray
                                else
                                    colorResource(id = R.color.blackSteel)
                                Text(
                                    text = "REGISTER!",
                                    color = buttonColor,
                                    fontSize = (screenHeight * 0.064).sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .wrapContentSize(align = Alignment.Center, unbounded = true)
                                        .padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun NameRegisterScreenPreview() {
    NameRegisterScreen(
        totalScore = 78.753,
        onClose = {},
    )
}