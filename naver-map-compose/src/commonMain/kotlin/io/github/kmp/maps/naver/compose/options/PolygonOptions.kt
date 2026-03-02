package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng

// options/PolygonOptions.kt
data class PolygonOptions(
    val coords: List<LatLng>,
    val holes: List<List<LatLng>> = emptyList(), // 구멍 뚫기
    val fillColor: Int = 0x7F0000FF,
    val outlineColor: Int = 0xFF0000FF.toInt(),
    val outlineWidth: Float = 3f,               // dp
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val tag: Any? = null
) {
    init {
        require(coords.size >= 3) { "coords must have at least 3 points" }
    }
}