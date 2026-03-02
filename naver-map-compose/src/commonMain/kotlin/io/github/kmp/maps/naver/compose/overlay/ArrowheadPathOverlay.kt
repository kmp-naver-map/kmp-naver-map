package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng

/**
 * 화살표 경로 오버레이(ArrowheadPathOverlay)를 나타내는 클래스입니다.
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
}
