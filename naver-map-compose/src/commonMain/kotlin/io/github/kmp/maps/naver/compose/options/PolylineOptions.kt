package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng

// options/PolylineOptions.kt
data class PolylineOptions(
    val coords: List<LatLng>,
    val color: Int = 0xFF0000FF.toInt(),  // ARGB
    val width: Float = 5f,               // dp
    val pattern: List<Float> = emptyList(), // 점선 패턴 [선 길이, 공백 길이]
    val capType: LineCap = LineCap.Round,
    val joinType: LineJoin = LineJoin.Round,
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val tag: Any? = null
) {
    init {
        require(coords.size >= 2) { "coords must have at least 2 points" }
    }
}

enum class LineCap { Butt, Round, Square }
enum class LineJoin { Bevel, Miter, Round }