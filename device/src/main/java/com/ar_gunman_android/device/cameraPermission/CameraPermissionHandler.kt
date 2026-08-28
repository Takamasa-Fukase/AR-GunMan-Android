package com.ar_gunman_android.device.cameraPermission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

interface CameraPermissionHandlerInterface {
    fun getCameraUsagePermissionGrantedFlag(): Boolean
    fun requestCameraUsagePermission()
}

class CameraPermissionHandler(
    private val context: Context
) : CameraPermissionHandlerInterface {
    private var launcher: ActivityResultLauncher<String>? = null

    fun register(activity: ComponentActivity) {
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}
    }

    override fun getCameraUsagePermissionGrantedFlag(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestCameraUsagePermission() {
        launcher?.launch(Manifest.permission.CAMERA)
    }
}