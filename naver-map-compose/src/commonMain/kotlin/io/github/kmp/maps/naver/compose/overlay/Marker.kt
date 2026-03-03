package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.MarkerOptions

/**
 * 지도 위의 특정 위치에 표시되는 마커입니다.
 *
 * A marker displayed at a specific position on the map.
 */
expect open class Marker {
    var position: LatLng
    var icon: OverlayImage?
    var caption: String
    var subCaption: String
    var alpha: Float
    var isVisible: Boolean
    var isFlat: Boolean
    var isForceShowCaption: Boolean
    var isForceShowIcon: Boolean
    var zIndex: Int
    var globalZIndex: Int
    var width: Float
    var height: Float
    var angle: Float
    var anchor: Anchor
    var minZoom: Double
    var maxZoom: Double
    var isMinZoomInclusive: Boolean
    var isMaxZoomInclusive: Boolean
    
    // 캡션 상세
    var captionColor: Int
    var captionHaloColor: Int
    var captionTextSize: Float
    var captionMinZoom: Double
    var captionMaxZoom: Double
    var captionRequestedWidth: Float
    var captionOffset: Float
    var captionPerspectiveEnabled: Boolean
    
    // 서브 캡션 상세
    var subCaptionColor: Int
    var subCaptionHaloColor: Int
    var subCaptionTextSize: Float
    var subCaptionMinZoom: Double
    var subCaptionMaxZoom: Double
    var subCaptionRequestedWidth: Float
    
    // 충돌 관리
    var isHideCollidedMarkers: Boolean
    var isHideCollidedSymbols: Boolean
    var isHideCollidedCaptions: Boolean
    
    // 효과
    var isIconPerspectiveEnabled: Boolean
    var iconTintColor: Int
    var tag: Any?
    
    fun onClick(listener: (Marker) -> Boolean)
    fun remove()
    internal fun applyOptions(options: MarkerOptions)

    object MarkerSize {
        val AUTO: Float
    }
}
