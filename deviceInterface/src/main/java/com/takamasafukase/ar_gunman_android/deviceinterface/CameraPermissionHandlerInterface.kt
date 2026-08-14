package com.takamasafukase.ar_gunman_android.deviceinterface

interface CameraPermissionHandlerInterface {
    fun getCameraUsagePermissionGrantedFlag(): Boolean
    fun requestCameraUsagePermission()
}