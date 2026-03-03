package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 폴리라인 오버레이의 표시 옵션을 정의하는 데이터 클래스입니다.
 *
 * Data class defining display options for a polyline overlay,
 * including color, width, dash pattern, cap type, and join type.
 */
data class PolylineOptions(
    val coords: List<LatLng>,
    val color: Int = OverlayDefaults.DEFAULT_LINE_COLOR,
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

/**
 * 선의 끝 모양(캡) 유형을 정의합니다.
 *
 * Defines the cap style applied to the endpoints of a polyline.
 */
enum class LineCap { Butt, Round, Square }

/**
 * 선의 꺾이는 부분(조인) 유형을 정의합니다.
 *
 * Defines the join style applied at the vertices of a polyline.
 */
enum class LineJoin { Bevel, Miter, Round }
