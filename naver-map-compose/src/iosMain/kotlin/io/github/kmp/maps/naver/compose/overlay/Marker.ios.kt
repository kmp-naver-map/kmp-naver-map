@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMFMarker
import cocoapods.NMapsMap.NMFOverlay
import cocoapods.NMapsMap.NMF_MARKER_IMAGE_DEFAULT
import io.github.kmp.maps.naver.compose.internal.toCommon
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
        get() = 0
        set(value) {
            nativeMarker.captionColor = value.toUIColor()
        }

    actual var captionHaloColor: Int
        get() = 0
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
        get() = 0
        set(value) {
            nativeMarker.subCaptionColor = value.toUIColor()
        }

    actual var subCaptionHaloColor: Int
        get() = 0
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
        get() = 0
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

    actual internal fun applyOptions(options: MarkerOptions) {
        position = options.position
        (options.icon as? OverlayImage)?.let { icon = it }
        caption = options.caption
        subCaption = options.subCaption
        alpha = options.alpha
        isVisible = options.isVisible
        isFlat = options.isFlat
        isForceShowCaption = options.isForceShowCaption
        isForceShowIcon = options.isForceShowIcon
        zIndex = options.zIndex
        globalZIndex = options.globalZIndex
        width = options.width
        height = options.height
        angle = options.angle
        anchor = options.anchor
        minZoom = options.minZoom
        maxZoom = options.maxZoom
        isMinZoomInclusive = options.isMinZoomInclusive
        isMaxZoomInclusive = options.isMaxZoomInclusive
        captionColor = options.captionColor
        captionHaloColor = options.captionHaloColor
        captionTextSize = options.captionTextSize
        captionMinZoom = options.captionMinZoom
        captionMaxZoom = options.captionMaxZoom
        captionRequestedWidth = options.captionRequestedWidth
        captionOffset = options.captionOffset
        captionPerspectiveEnabled = options.captionPerspectiveEnabled
        subCaptionColor = options.subCaptionColor
        subCaptionHaloColor = options.subCaptionHaloColor
        subCaptionTextSize = options.subCaptionTextSize
        subCaptionMinZoom = options.subCaptionMinZoom
        subCaptionMaxZoom = options.subCaptionMaxZoom
        subCaptionRequestedWidth = options.subCaptionRequestedWidth
        isHideCollidedMarkers = options.isHideCollidedMarkers
        isHideCollidedSymbols = options.isHideCollidedSymbols
        isHideCollidedCaptions = options.isHideCollidedCaptions
        isIconPerspectiveEnabled = options.isIconPerspectiveEnabled
        iconTintColor = options.iconTintColor
        tag = options.tag
    }

    actual object MarkerSize {
        actual val AUTO: Float = -1f
    }
}
