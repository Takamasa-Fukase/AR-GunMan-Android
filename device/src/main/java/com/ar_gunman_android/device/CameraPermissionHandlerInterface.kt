package com.ar_gunman_android.device

interface CameraPermissionHandlerInterface {
    fun getCameraUsagePermissionGrantedFlag(): Boolean
    fun requestCameraUsagePermission()
}