package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 화살표 경로 오버레이(ArrowheadPathOverlay)를 설정하기 위한 옵션 데이터 클래스입니다.
 *
 * Data class defining display options for an arrowhead path overlay,
 * including line/outline colors, head size ratio, and elevation.
 */
data class ArrowheadPathOptions(
    val coords: List<LatLng>,
    val width: Float = 10f,               // dp
    val outlineWidth: Float = 2f,         // dp
    val color: Int = OverlayDefaults.COLOR_WHITE,
    val outlineColor: Int = OverlayDefaults.COLOR_BLACK,
    val elevation: Float = 0f,            // 고도 (Android 전용)
    val headSizeRatio: Float = 2.5f,      // 머리 크기 배율
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val tag: Any? = null
) {
    init {
        require(coords.size >= 2) { "coords must have at least 2 points" }
    }
}
