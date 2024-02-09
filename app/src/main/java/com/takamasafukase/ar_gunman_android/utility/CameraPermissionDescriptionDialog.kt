package com.takamasafukase.ar_gunman_android.utility

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CameraPermissionDescriptionDialog(
    onTapConfirmButton: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = "Camera Permission Required")
        },
        text = {
            Text(text = "Camera Permission is required to play this game.\nDo you want to change your settings?")
        },
        confirmButton = {
            TextButton(onClick = {
                onTapConfirmButton()
            }) {
                Text(
                    text = "Yes",
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(
                    text = "Not now",
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
    )
}