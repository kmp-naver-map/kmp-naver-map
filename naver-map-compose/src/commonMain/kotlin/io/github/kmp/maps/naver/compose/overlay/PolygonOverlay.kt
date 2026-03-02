package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng

expect open class PolygonOverlay {
    var coords: List<LatLng>
    var holes: List<List<LatLng>>
    var fillColor: Int
    var outlineColor: Int
    var outlineWidth: Float
    var zIndex: Int
    var isVisible: Boolean
    var tag: Any?

    fun onClick(listener: (PolygonOverlay) -> Boolean)
    fun remove()
}