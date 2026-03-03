package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.PathOptions

/**
 * 경로 오버레이(PathOverlay)를 나타내는 클래스입니다.
 * 지도 위에 두 개 이상의 좌표를 연결하는 경로를 표시하며,
 * 진행률(progress)에 따라 지나간 구간의 색상을 변경할 수 있습니다.
 *
 * A path overlay that displays a route connecting two or more coordinates on the map.
 * Supports progress-based coloring to distinguish passed and remaining segments.
 */
expect class PathOverlay {
    var coords: List<LatLng>
    var width: Float
    var outlineWidth: Float
    var color: Int
    var outlineColor: Int
    var passedColor: Int
    var passedOutlineColor: Int
    var progress: Double
    var patternInterval: Float
    var isHideCollidedSymbols: Boolean
    var isHideCollidedMarkers: Boolean
    var isHideCollidedCaptions: Boolean
    var zIndex: Int
    var isVisible: Boolean
    var tag: Any?

    fun onClick(listener: (PathOverlay) -> Boolean)
    fun remove()
    internal fun applyOptions(options: PathOptions)
}
