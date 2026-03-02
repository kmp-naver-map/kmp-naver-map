package io.github.kmp.maps.naver.compose.controller

import io.github.kmp.maps.naver.compose.model.CameraAnimation
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.MapType

// controller/INaverMapController.kt
interface INaverMapController {
    val cameraPosition: CameraPosition
    val contentBounds: LatLngBounds
    val coveringTileIds: List<Long>
    
    fun moveCamera(
        position: CameraPosition,
        animation: CameraAnimation = CameraAnimation.Easing,
        durationMs: Int = 800,
        onFinish: (() -> Unit)? = null
    )
    
    fun fitBounds(
        bounds: LatLngBounds,
        paddingDp: Int = 48,
        animation: CameraAnimation = CameraAnimation.Easing
    )
    
    fun setMapType(mapType: MapType)
    fun setNightMode(enabled: Boolean)
    fun setIndoorEnabled(enabled: Boolean)
    fun setBuildingHeight(height: Float) // 0.0 ~ 1.0
    
    // 스크린 좌표 변환
    fun latLngToScreen(latLng: LatLng): Pair<Float, Float>?
    fun screenToLatLng(x: Float, y: Float): LatLng?
}