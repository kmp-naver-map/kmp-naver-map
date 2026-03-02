package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng

/**
 * 경로 오버레이(PathOverlay)를 설정하기 위한 옵션 데이터 클래스입니다.
 */
data class PathOptions(
    val coords: List<LatLng>,
    val width: Float = 5f,                // dp
    val outlineWidth: Float = 1f,         // dp
    val color: Int = 0xFFFFFFFF.toInt(),   // 경로 선 색상 (기본 흰색)
    val outlineColor: Int = 0xFF000000.toInt(), // 테두리 색상 (기본 검정)
    val passedColor: Int = 0xFF888888.toInt(), // 지나온 경로 색상
    val passedOutlineColor: Int = 0xFF444444.toInt(), // 지나온 경로 테두리 색상
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
