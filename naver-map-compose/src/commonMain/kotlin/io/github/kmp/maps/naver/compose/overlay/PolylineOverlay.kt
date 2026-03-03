package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin
import io.github.kmp.maps.naver.compose.options.PolylineOptions

/**
 * 지도 위에 다수의 좌표를 연결하는 선을 표시하는 오버레이입니다.
 *
 * A polyline overlay connecting multiple coordinates on the map.
 */
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
    internal fun applyOptions(options: PolylineOptions)
}
