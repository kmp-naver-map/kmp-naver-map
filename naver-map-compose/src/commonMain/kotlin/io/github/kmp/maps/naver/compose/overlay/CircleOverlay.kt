package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.CircleOptions

/**
 * 지도 위에 원형 영역을 표시하는 오버레이입니다.
 *
 * A circle overlay displayed on the map.
 */
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
    internal fun applyOptions(options: CircleOptions)
}