package com.ar_gunman_android.device.cameraPermission

interface CameraPermissionHandlerInterface {
    fun getCameraUsagePermissionGrantedFlag(): Boolean
    fun requestCameraUsagePermission()
}

class CameraPermissionHandler {
}