package com.example.domain.serviceInterfaces

interface CameraPermissionHandlerInterface {
    fun getCameraUsagePermissionGrantedFlag(): Boolean
    fun requestCameraUsagePermission()
}