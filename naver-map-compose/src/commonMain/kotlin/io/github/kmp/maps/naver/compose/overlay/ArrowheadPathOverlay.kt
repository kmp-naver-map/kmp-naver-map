package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.ArrowheadPathOptions

/**
 * 화살표 경로 오버레이(ArrowheadPathOverlay)를 나타내는 클래스입니다.
 * 경로의 끝부분에 화살표 머리가 표시되어 방향을 나타냅니다.
 *
 * An arrowhead path overlay that displays a route with an arrowhead at the end,
 * indicating direction of travel.
 */
expect class ArrowheadPathOverlay {
    var coords: List<LatLng>
    var width: Float
    var outlineWidth: Float
    var color: Int
    var outlineColor: Int
    var elevation: Float
    var headSizeRatio: Float
    var zIndex: Int
    var isVisible: Boolean
    var tag: Any?

    fun onClick(listener: (ArrowheadPathOverlay) -> Boolean)
    fun remove()
    internal fun applyOptions(options: ArrowheadPathOptions)
}
