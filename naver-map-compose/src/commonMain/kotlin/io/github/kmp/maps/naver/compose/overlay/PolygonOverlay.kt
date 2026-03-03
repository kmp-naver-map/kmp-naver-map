package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.PolygonOptions

/**
 * 지도 위에 다각형 영역을 표시하는 오버레이입니다.
 *
 * A polygon overlay displaying a region on the map.
 */
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
    internal fun applyOptions(options: PolygonOptions)
}