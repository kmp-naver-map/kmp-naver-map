package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.model.LatLng

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
    var anchor: Pair<Float, Float>
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

    object MarkerSize {
        val AUTO: Float
    }
}
