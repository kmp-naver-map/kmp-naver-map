package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng

// options/CircleOptions.kt
data class CircleOptions(
    val center: LatLng,
    val radius: Double,                          // 미터
    val fillColor: Int = 0x7F0000FF,
    val outlineColor: Int = 0xFF0000FF.toInt(),
    val outlineWidth: Float = 3f,               // dp
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val tag: Any? = null
) {
    init {
        require(radius > 0) { "radius must be greater than 0" }
    }
}