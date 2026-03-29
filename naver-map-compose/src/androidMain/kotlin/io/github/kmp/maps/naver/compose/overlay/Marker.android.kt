package io.github.kmp.maps.naver.compose.overlay

import android.graphics.PointF
import io.github.kmp.maps.naver.compose.internal.dpToPx
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.MarkerOptions

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

    actual var anchor: Anchor
        get() = Anchor(nativeMarker.anchor.x, nativeMarker.anchor.y)
        set(value) { nativeMarker.anchor = PointF(value.x, value.y) }

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

    private var _lastOptions: MarkerOptions? = null

    actual internal fun applyOptions(options: MarkerOptions) {
        val prev = _lastOptions
        if (prev == null || prev.position != options.position) position = options.position
        if (prev == null || prev.icon != options.icon) (options.icon as? OverlayImage)?.let { icon = it }
        if (prev == null || prev.caption != options.caption) caption = options.caption
        if (prev == null || prev.subCaption != options.subCaption) subCaption = options.subCaption
        if (prev == null || prev.alpha != options.alpha) alpha = options.alpha
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        if (prev == null || prev.isFlat != options.isFlat) isFlat = options.isFlat
        if (prev == null || prev.isForceShowCaption != options.isForceShowCaption) isForceShowCaption = options.isForceShowCaption
        if (prev == null || prev.isForceShowIcon != options.isForceShowIcon) isForceShowIcon = options.isForceShowIcon
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.globalZIndex != options.globalZIndex) globalZIndex = options.globalZIndex
        if (prev == null || prev.width != options.width) width = options.width
        if (prev == null || prev.height != options.height) height = options.height
        if (prev == null || prev.angle != options.angle) angle = options.angle
        if (prev == null || prev.anchor != options.anchor) anchor = options.anchor
        if (prev == null || prev.minZoom != options.minZoom) minZoom = options.minZoom
        if (prev == null || prev.maxZoom != options.maxZoom) maxZoom = options.maxZoom
        if (prev == null || prev.isMinZoomInclusive != options.isMinZoomInclusive) isMinZoomInclusive = options.isMinZoomInclusive
        if (prev == null || prev.isMaxZoomInclusive != options.isMaxZoomInclusive) isMaxZoomInclusive = options.isMaxZoomInclusive
        if (prev == null || prev.captionColor != options.captionColor) captionColor = options.captionColor
        if (prev == null || prev.captionHaloColor != options.captionHaloColor) captionHaloColor = options.captionHaloColor
        if (prev == null || prev.captionTextSize != options.captionTextSize) captionTextSize = options.captionTextSize
        if (prev == null || prev.captionMinZoom != options.captionMinZoom) captionMinZoom = options.captionMinZoom
        if (prev == null || prev.captionMaxZoom != options.captionMaxZoom) captionMaxZoom = options.captionMaxZoom
        if (prev == null || prev.captionRequestedWidth != options.captionRequestedWidth) captionRequestedWidth = options.captionRequestedWidth
        if (prev == null || prev.captionOffset != options.captionOffset) captionOffset = options.captionOffset
        if (prev == null || prev.captionPerspectiveEnabled != options.captionPerspectiveEnabled) captionPerspectiveEnabled = options.captionPerspectiveEnabled
        if (prev == null || prev.subCaptionColor != options.subCaptionColor) subCaptionColor = options.subCaptionColor
        if (prev == null || prev.subCaptionHaloColor != options.subCaptionHaloColor) subCaptionHaloColor = options.subCaptionHaloColor
        if (prev == null || prev.subCaptionTextSize != options.subCaptionTextSize) subCaptionTextSize = options.subCaptionTextSize
        if (prev == null || prev.subCaptionMinZoom != options.subCaptionMinZoom) subCaptionMinZoom = options.subCaptionMinZoom
        if (prev == null || prev.subCaptionMaxZoom != options.subCaptionMaxZoom) subCaptionMaxZoom = options.subCaptionMaxZoom
        if (prev == null || prev.subCaptionRequestedWidth != options.subCaptionRequestedWidth) subCaptionRequestedWidth = options.subCaptionRequestedWidth
        if (prev == null || prev.isHideCollidedMarkers != options.isHideCollidedMarkers) isHideCollidedMarkers = options.isHideCollidedMarkers
        if (prev == null || prev.isHideCollidedSymbols != options.isHideCollidedSymbols) isHideCollidedSymbols = options.isHideCollidedSymbols
        if (prev == null || prev.isHideCollidedCaptions != options.isHideCollidedCaptions) isHideCollidedCaptions = options.isHideCollidedCaptions
        if (prev == null || prev.isIconPerspectiveEnabled != options.isIconPerspectiveEnabled) isIconPerspectiveEnabled = options.isIconPerspectiveEnabled
        if (prev == null || prev.iconTintColor != options.iconTintColor) iconTintColor = options.iconTintColor
        tag = options.tag
        _lastOptions = options
    }

    actual object MarkerSize {
        actual val AUTO: Float = -1f
    }
}
