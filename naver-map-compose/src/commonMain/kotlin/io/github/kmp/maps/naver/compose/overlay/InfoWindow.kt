package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.InfoWindowOptions

/**
 * 지도 위에 표시되는 정보 창을 나타내는 클래스입니다.
 * 마커에 연결하거나 독립적으로 특정 좌표에 표시할 수 있습니다.
 *
 * An info window displayed on the map.
 * Can be attached to a marker or displayed independently at a specific coordinate.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect open class InfoWindow {
    var position: LatLng
    var text: String
    var alpha: Float
    var zIndex: Int
    var anchor: Anchor
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
    internal fun applyOptions(options: InfoWindowOptions)
}
