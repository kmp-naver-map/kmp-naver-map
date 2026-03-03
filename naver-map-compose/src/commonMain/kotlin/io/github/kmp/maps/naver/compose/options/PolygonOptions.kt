package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 폴리곤 오버레이의 표시 옵션을 정의하는 데이터 클래스입니다.
 *
 * Data class defining display options for a polygon overlay,
 * including fill color, outline, and optional holes.
 */
data class PolygonOptions(
    val coords: List<LatLng>,
    val holes: List<List<LatLng>> = emptyList(), // 구멍 뚫기
    val fillColor: Int = OverlayDefaults.DEFAULT_FILL_COLOR,
    val outlineColor: Int = OverlayDefaults.DEFAULT_LINE_COLOR,
    val outlineWidth: Float = 3f,               // dp
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val tag: Any? = null
) {
    init {
        require(coords.size >= 3) { "coords must have at least 3 points" }
    }
}
