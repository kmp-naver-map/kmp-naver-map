package io.github.kmp.maps.naver.compose.overlay

import android.graphics.PointF
import io.github.kmp.maps.naver.compose.internal.dpToPx
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng

actual open class Marker internal constructor(
    internal val nativeMarker: com.naver.maps.map.overlay.Marker
) {
    actual var position: LatLng
        get() = nativeMarker.position.toCommon()
        set(value) { nativeMarker.position = value.toNaver() }

    actual var icon: OverlayImage?
        get() = OverlayImage(nativeMarker.icon)
        set(value) {
            nativeMarker.icon = value?.nativeImage ?: com.naver.maps.map.overlay.Marker.DEFAULT_ICON
        }

    actual var caption: String
        get() = nativeMarker.captionText
        set(value) { nativeMarker.captionText = value }

    actual var subCaption: String
        get() = nativeMarker.subCaptionText
        set(value) { nativeMarker.subCaptionText = value }

    actual var alpha: Float
        get() = nativeMarker.alpha
        set(value) { nativeMarker.alpha = value }

    actual var isVisible: Boolean
        get() = nativeMarker.isVisible
        set(value) { nativeMarker.isVisible = value }

    actual var isFlat: Boolean
        get() = nativeMarker.isFlat
        set(value) { nativeMarker.isFlat = value }

    actual var isForceShowCaption: Boolean
        get() = nativeMarker.isForceShowCaption
        set(value) { nativeMarker.isForceShowCaption = value }

    actual var isForceShowIcon: Boolean
        get() = nativeMarker.isForceShowIcon
        set(value) { nativeMarker.isForceShowIcon = value }

    actual var zIndex: Int
        get() = nativeMarker.zIndex
        set(value) { nativeMarker.zIndex = value }

    actual var globalZIndex: Int
        get() = nativeMarker.globalZIndex
        set(value) { nativeMarker.globalZIndex = value }

    actual var width: Float
        get() = nativeMarker.width.toFloat()
        set(value) {
            nativeMarker.width = if (value == MarkerSize.AUTO) 
                com.naver.maps.map.overlay.Marker.SIZE_AUTO 
            else value.dpToPx().toInt()
        }

    actual var height: Float
        get() = nativeMarker.height.toFloat()
        set(value) {
            nativeMarker.height = if (value == MarkerSize.AUTO) 
                com.naver.maps.map.overlay.Marker.SIZE_AUTO 
            else value.dpToPx().toInt()
        }

    actual var angle: Float
        get() = nativeMarker.angle
        set(value) { nativeMarker.angle = value }

    actual var anchor: Pair<Float, Float>
        get() = Pair(nativeMarker.anchor.x, nativeMarker.anchor.y)
        set(value) { nativeMarker.anchor = PointF(value.first, value.second) }

    actual var minZoom: Double
        get() = nativeMarker.minZoom
        set(value) { nativeMarker.minZoom = value }

    actual var maxZoom: Double
        get() = nativeMarker.maxZoom
        set(value) { nativeMarker.maxZoom = value }

    actual var isMinZoomInclusive: Boolean
        get() = nativeMarker.isMinZoomInclusive
        set(value) { nativeMarker.isMinZoomInclusive = value }

    actual var isMaxZoomInclusive: Boolean
        get() = nativeMarker.isMaxZoomInclusive
        set(value) { nativeMarker.isMaxZoomInclusive = value }

    actual var captionColor: Int
        get() = nativeMarker.captionColor
        set(value) { nativeMarker.captionColor = value }

    actual var captionHaloColor: Int
        get() = nativeMarker.captionHaloColor
        set(value) { nativeMarker.captionHaloColor = value }

    actual var captionTextSize: Float
        get() = nativeMarker.captionTextSize
        set(value) { nativeMarker.captionTextSize = value }

    actual var captionMinZoom: Double
        get() = nativeMarker.captionMinZoom
        set(value) { nativeMarker.captionMinZoom = value }

    actual var captionMaxZoom: Double
        get() = nativeMarker.captionMaxZoom
        set(value) { nativeMarker.captionMaxZoom = value }

    actual var captionRequestedWidth: Float
        get() = nativeMarker.captionRequestedWidth.toFloat()
        set(value) { nativeMarker.captionRequestedWidth = value.dpToPx().toInt() }

    actual var captionOffset: Float
        get() = nativeMarker.captionOffset.toFloat()
        set(value) { nativeMarker.captionOffset = value.dpToPx().toInt() }

    actual var captionPerspectiveEnabled: Boolean
        get() = nativeMarker.isCaptionPerspectiveEnabled
        set(value) { nativeMarker.isCaptionPerspectiveEnabled = value }

    actual var subCaptionColor: Int
        get() = nativeMarker.subCaptionColor
        set(value) { nativeMarker.subCaptionColor = value }

    actual var subCaptionHaloColor: Int
        get() = nativeMarker.subCaptionHaloColor
        set(value) { nativeMarker.subCaptionHaloColor = value }

    actual var subCaptionTextSize: Float
        get() = nativeMarker.subCaptionTextSize
        set(value) { nativeMarker.subCaptionTextSize = value }

    actual var subCaptionMinZoom: Double
        get() = nativeMarker.subCaptionMinZoom
        set(value) { nativeMarker.subCaptionMinZoom = value }

    actual var subCaptionMaxZoom: Double
        get() = nativeMarker.subCaptionMaxZoom
        set(value) { nativeMarker.subCaptionMaxZoom = value }

    actual var subCaptionRequestedWidth: Float
        get() = nativeMarker.subCaptionRequestedWidth.toFloat()
        set(value) { nativeMarker.subCaptionRequestedWidth = value.toInt() }

    actual var isHideCollidedMarkers: Boolean
        get() = nativeMarker.isHideCollidedMarkers
        set(value) { nativeMarker.isHideCollidedMarkers = value }

    actual var isHideCollidedSymbols: Boolean
        get() = nativeMarker.isHideCollidedSymbols
        set(value) { nativeMarker.isHideCollidedSymbols = value }

    actual var isHideCollidedCaptions: Boolean
        get() = nativeMarker.isHideCollidedCaptions
        set(value) { nativeMarker.isHideCollidedCaptions = value }

    actual var isIconPerspectiveEnabled: Boolean
        get() = nativeMarker.isIconPerspectiveEnabled
        set(value) { nativeMarker.isIconPerspectiveEnabled = value }

    actual var iconTintColor: Int
        get() = nativeMarker.iconTintColor
        set(value) { nativeMarker.iconTintColor = value }

    actual var tag: Any?
        get() = nativeMarker.tag
        set(value) { nativeMarker.tag = value }

    actual fun onClick(listener: (Marker) -> Boolean) {
        nativeMarker.setOnClickListener {
            listener(this)
        }
    }

    actual fun remove() {
        nativeMarker.map = null
    }

    actual object MarkerSize {
        actual val AUTO: Float = -1f
    }
}
