package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng

expect class CircleOverlay {
    var center: LatLng
    var radius: Double        // 미터
    var fillColor: Int
    var outlineColor: Int
    var outlineWidth: Float
    var zIndex: Int
    var isVisible: Boolean
    var tag: Any?

    fun onClick(listener: (CircleOverlay) -> Boolean)
    fun remove()
}