package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 경로 오버레이(PathOverlay)를 설정하기 위한 옵션 데이터 클래스입니다.
 *
 * Data class defining display options for a path overlay,
 * including line/outline colors, passed-path styling, and progress.
 */
data class PathOptions(
    val coords: List<LatLng>,
    val width: Float = 5f,                // dp
    val outlineWidth: Float = 1f,         // dp
    val color: Int = OverlayDefaults.COLOR_WHITE,
    val outlineColor: Int = OverlayDefaults.COLOR_BLACK,
    val passedColor: Int = OverlayDefaults.DEFAULT_PASSED_COLOR,
    val passedOutlineColor: Int = OverlayDefaults.DEFAULT_PASSED_OUTLINE_COLOR,
    val progress: Double = 0.0,           // 진행률 (0.0 ~ 1.0)
    val patternInterval: Float = 0f,      // 패턴 간격 (0이면 없음)
    val isHideCollidedSymbols: Boolean = false, // 심볼과 겹칠 때 숨김 여부
    val isHideCollidedMarkers: Boolean = false, // 마커와 겹칠 때 숨김 여부
    val isHideCollidedCaptions: Boolean = false, // 캡션과 겹칠 때 숨김 여부
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val tag: Any? = null
) {
    init {
        require(coords.size >= 2) { "coords must have at least 2 points" }
        require(progress in 0.0..1.0) { "progress must be between 0.0 and 1.0" }
    }
}
