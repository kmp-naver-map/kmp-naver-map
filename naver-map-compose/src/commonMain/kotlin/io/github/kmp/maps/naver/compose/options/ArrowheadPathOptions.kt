package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng

/**
 * 화살표 경로 오버레이(ArrowheadPathOverlay)를 설정하기 위한 옵션 데이터 클래스입니다.
 */
data class ArrowheadPathOptions(
    val coords: List<LatLng>,
    val width: Float = 10f,               // dp
    val outlineWidth: Float = 2f,         // dp
    val color: Int = 0xFFFFFFFF.toInt(),   // 경로 선 색상 (기본 흰색)
    val outlineColor: Int = 0xFF000000.toInt(), // 테두리 색상 (기본 검정)
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
