@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMFMarker
import cocoapods.NMapsMap.NMFOverlay
import cocoapods.NMapsMap.NMF_MARKER_IMAGE_DEFAULT
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toArgbInt
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.internal.toUIColor
import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.MarkerOptions
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake


actual open class Marker internal constructor(
    internal val nativeMarker: NMFMarker
) {
    actual var tag: Any?
        get() = nativeMarker.userInfo?.get("tag")
        set(value) {
            val userInfo = nativeMarker.userInfo?.toMutableMap() ?: mutableMapOf()
            if (value == null) {
                userInfo.remove("tag")
            } else {
                userInfo["tag"] = value
            }
            nativeMarker.userInfo = userInfo.toMap()
        }

    actual var position: LatLng
        get() = nativeMarker.position.toCommon()
        set(value) {
            nativeMarker.position = value.toNaver()
        }

    actual var icon: OverlayImage?
        get() = nativeMarker.iconImage?.let { OverlayImage(it) }
        set(value) {
            nativeMarker.iconImage = value?.nativeImage ?: NMF_MARKER_IMAGE_DEFAULT
        }

    actual var caption: String
        get() = nativeMarker.captionText
        set(value) {
            nativeMarker.captionText = value
        }

    actual var subCaption: String
        get() = nativeMarker.subCaptionText
        set(value) {
            nativeMarker.subCaptionText = value
        }

    actual var alpha: Float
        get() = nativeMarker.alpha.toFloat()
        set(value) {
            nativeMarker.alpha = value.toDouble()
        }

    actual var isVisible: Boolean
        get() = nativeMarker.hidden.not()
        set(value) {
            nativeMarker.hidden = value.not()
        }

    actual var isFlat: Boolean
        get() = nativeMarker.flat
        set(value) {
            nativeMarker.flat = value
        }

    actual var isForceShowCaption: Boolean
        get() = nativeMarker.isForceShowCaption
        set(value) {
            nativeMarker.isForceShowCaption = value
        }

    actual var isForceShowIcon: Boolean
        get() = nativeMarker.isForceShowIcon
        set(value) {
            nativeMarker.isForceShowIcon = value
        }

    actual var zIndex: Int
        get() = nativeMarker.zIndex.toInt()
        set(value) {
            nativeMarker.zIndex = value.toLong()
        }

    actual var globalZIndex: Int
        get() = nativeMarker.globalZIndex.toInt()
        set(value) {
            nativeMarker.globalZIndex = value.toLong()
        }

    actual var width: Float
        get() = nativeMarker.width.toFloat()
        set(value) {
            nativeMarker.width = if (value == MarkerSize.AUTO) 0.0 else value.toDouble()
        }

    actual var height: Float
        get() = nativeMarker.height.toFloat()
        set(value) {
            nativeMarker.height = if (value == MarkerSize.AUTO) 0.0 else value.toDouble()
        }

    actual var angle: Float
        get() = nativeMarker.angle.toFloat()
        set(value) {
            nativeMarker.angle = value.toDouble()
        }

    actual var anchor: Anchor
        get() = nativeMarker.anchor.useContents { Anchor(x.toFloat(), y.toFloat()) }
        set(value) {
            nativeMarker.anchor = CGPointMake(value.x.toDouble(), value.y.toDouble())
        }

    actual var minZoom: Double
        get() = nativeMarker.minZoom
        set(value) {
            nativeMarker.minZoom = value
        }

    actual var maxZoom: Double
        get() = nativeMarker.maxZoom
        set(value) {
            nativeMarker.maxZoom = value
        }

    actual var isMinZoomInclusive: Boolean
        get() = nativeMarker.isMinZoomInclusive
        set(value) {
            nativeMarker.isMinZoomInclusive = value
        }

    actual var isMaxZoomInclusive: Boolean
        get() = nativeMarker.isMaxZoomInclusive
        set(value) {
            nativeMarker.isMaxZoomInclusive = value
        }

    actual var captionColor: Int
        get() = nativeMarker.captionColor.toArgbInt()
        set(value) {
            nativeMarker.captionColor = value.toUIColor()
        }

    actual var captionHaloColor: Int
        get() = nativeMarker.captionHaloColor.toArgbInt()
        set(value) {
            nativeMarker.captionHaloColor = value.toUIColor()
        }

    actual var captionTextSize: Float
        get() = nativeMarker.captionTextSize.toFloat()
        set(value) {
            nativeMarker.captionTextSize = value.toDouble()
        }

    actual var captionMinZoom: Double
        get() = nativeMarker.captionMinZoom
        set(value) {
            nativeMarker.captionMinZoom = value
        }

    actual var captionMaxZoom: Double
        get() = nativeMarker.captionMaxZoom
        set(value) {
            nativeMarker.captionMaxZoom = value
        }

    actual var captionRequestedWidth: Float
        get() = nativeMarker.captionRequestedWidth.toFloat()
        set(value) {
            nativeMarker.captionRequestedWidth = value.toDouble()
        }

    actual var captionOffset: Float
        get() = nativeMarker.captionOffset.toFloat()
        set(value) {
            nativeMarker.captionOffset = value.toDouble()
        }

    actual var captionPerspectiveEnabled: Boolean
        get() = nativeMarker.captionPerspectiveEnabled
        set(value) {
            nativeMarker.captionPerspectiveEnabled = value
        }

    actual var subCaptionColor: Int
        get() = nativeMarker.subCaptionColor.toArgbInt()
        set(value) {
            nativeMarker.subCaptionColor = value.toUIColor()
        }

    actual var subCaptionHaloColor: Int
        get() = nativeMarker.subCaptionHaloColor.toArgbInt()
        set(value) {
            nativeMarker.subCaptionHaloColor = value.toUIColor()
        }

    actual var subCaptionTextSize: Float
        get() = nativeMarker.subCaptionTextSize.toFloat()
        set(value) {
            nativeMarker.subCaptionTextSize = value.toDouble()
        }

    actual var subCaptionMinZoom: Double
        get() = nativeMarker.subCaptionMinZoom
        set(value) {
            nativeMarker.subCaptionMinZoom = value
        }

    actual var subCaptionMaxZoom: Double
        get() = nativeMarker.subCaptionMaxZoom
        set(value) {
            nativeMarker.subCaptionMaxZoom = value
        }

    actual var subCaptionRequestedWidth: Float
        get() = nativeMarker.subCaptionRequestedWidth.toFloat()
        set(value) {
            nativeMarker.subCaptionRequestedWidth = value.toDouble()
        }

    actual var isHideCollidedMarkers: Boolean
        get() = nativeMarker.isHideCollidedMarkers
        set(value) {
            nativeMarker.isHideCollidedMarkers = value
        }

    actual var isHideCollidedSymbols: Boolean
        get() = nativeMarker.isHideCollidedSymbols
        set(value) {
            nativeMarker.isHideCollidedSymbols = value
        }

    actual var isHideCollidedCaptions: Boolean
        get() = nativeMarker.isHideCollidedCaptions
        set(value) {
            nativeMarker.isHideCollidedCaptions = value
        }

    actual var isIconPerspectiveEnabled: Boolean
        get() = nativeMarker.iconPerspectiveEnabled
        set(value) {
            nativeMarker.iconPerspectiveEnabled = value
        }

    actual var iconTintColor: Int
        get() = nativeMarker.iconTintColor.toArgbInt()
        set(value) {
            nativeMarker.iconTintColor = value.toUIColor()
        }

    actual fun onClick(listener: (Marker) -> Boolean) {
        val self = this
        nativeMarker.touchHandler = { _: NMFOverlay? ->
            listener(self)
        }
    }

    actual fun remove() {
        nativeMarker.mapView = null
        nativeMarker.touchHandler = null
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
