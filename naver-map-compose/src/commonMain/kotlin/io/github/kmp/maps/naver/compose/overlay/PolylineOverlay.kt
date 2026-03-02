package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin

expect class PolylineOverlay {
    var coords: List<LatLng>
    var color: Int
    var width: Float
    val pattern: List<Float>
    var capType: LineCap
    var joinType: LineJoin
    var zIndex: Int
    var isVisible: Boolean
    var tag: Any?

    fun onClick(listener: (PolylineOverlay) -> Boolean)
    fun remove()
}
