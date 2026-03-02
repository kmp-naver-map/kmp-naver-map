package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng

/**
 * 지도 위에 표시되는 정보 창을 나타내는 클래스입니다.
 */
expect open class InfoWindow {
    var position: LatLng
    var text: String
    var alpha: Float
    var zIndex: Int
    var anchor: Pair<Float, Float>
    var offsetX: Int
    var offsetY: Int
    var textColor: Int
    var textSize: Float
    var backgroundColor: Int
    var cornerRadiusDp: Float
    var isVisible: Boolean
    var tag: Any?

    /**
     * 정보 창에 클릭 리스너를 설정합니다.
     */
    fun onClick(listener: (InfoWindow) -> Boolean)

    /**
     * 정보 창을 닫습니다.
     */
    fun close()
}
