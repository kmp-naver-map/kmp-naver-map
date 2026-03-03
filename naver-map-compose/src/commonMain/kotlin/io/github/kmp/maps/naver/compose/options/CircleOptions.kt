package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 원형 오버레이의 표시 옵션을 정의하는 데이터 클래스입니다.
 *
 * Data class defining display options for a circle overlay,
 * including center position, radius, fill color, and outline.
 */
data class CircleOptions(
    val center: LatLng,
    val radius: Double,                          // 미터
    val fillColor: Int = OverlayDefaults.DEFAULT_FILL_COLOR,
    val outlineColor: Int = OverlayDefaults.DEFAULT_LINE_COLOR,
    val outlineWidth: Float = 3f,               // dp
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val tag: Any? = null
) {
    init {
        require(radius > 0) { "radius must be greater than 0" }
    }
}
