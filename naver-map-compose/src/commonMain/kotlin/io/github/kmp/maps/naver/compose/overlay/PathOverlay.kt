package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng

/**
 * 경로 오버레이(PathOverlay)를 나타내는 클래스입니다.
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
}
