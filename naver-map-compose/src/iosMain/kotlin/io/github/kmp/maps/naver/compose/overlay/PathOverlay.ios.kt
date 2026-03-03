@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMGLatLng
import cocoapods.NMapsMap.NMFOverlay
import cocoapods.NMapsMap.NMFPath
import cocoapods.NMapsMap.NMGLineString
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.internal.toUIColor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.PathOptions
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class PathOverlay(
    internal val nativePathOverlay: NMFPath = NMFPath()
) {
    actual var coords: List<LatLng>
        get() = (nativePathOverlay.path.points()).map { (it as NMGLatLng).toCommon() }
        set(value) {
            nativePathOverlay.path = NMGLineString.lineStringWithPoints(value.map { it.toNaver() })
        }

    actual var width: Float
        get() = nativePathOverlay.width.toFloat()
        set(value) {
            nativePathOverlay.width = value.toDouble()
        }

    actual var outlineWidth: Float
        get() = nativePathOverlay.outlineWidth.toFloat()
        set(value) {
            nativePathOverlay.outlineWidth = value.toDouble()
        }

    actual var color: Int = 0
        set(value) {
            field = value
            nativePathOverlay.color = value.toUIColor()
        }

    actual var outlineColor: Int = 0
        set(value) {
            field = value
            nativePathOverlay.outlineColor = value.toUIColor()
        }

    actual var passedColor: Int = 0
        set(value) {
            field = value
            nativePathOverlay.passedColor = value.toUIColor()
        }

    actual var passedOutlineColor: Int = 0
        set(value) {
            field = value
            nativePathOverlay.passedOutlineColor = value.toUIColor()
        }

    actual var progress: Double
        get() = nativePathOverlay.progress
        set(value) {
            nativePathOverlay.progress = value
        }

    actual var patternInterval: Float
        get() = nativePathOverlay.patternInterval.toFloat()
        set(value) {
            nativePathOverlay.patternInterval = value.toULong()
        }

    actual var isHideCollidedSymbols: Boolean
        get() = nativePathOverlay.isHideCollidedSymbols
        set(value) {
            nativePathOverlay.isHideCollidedSymbols = value
        }

    actual var isHideCollidedMarkers: Boolean
        get() = nativePathOverlay.isHideCollidedMarkers
        set(value) {
            nativePathOverlay.isHideCollidedMarkers = value
        }

    actual var isHideCollidedCaptions: Boolean
        get() = nativePathOverlay.isHideCollidedCaptions
        set(value) {
            nativePathOverlay.isHideCollidedCaptions = value
        }

    actual var zIndex: Int
        get() = nativePathOverlay.zIndex.toInt()
        set(value) {
            nativePathOverlay.zIndex = value.toLong()
        }

    actual var isVisible: Boolean
        get() = !nativePathOverlay.hidden
        set(value) {
            nativePathOverlay.hidden = !value
        }

    actual var tag: Any? = null

    private var onClickListener: (PathOverlay) -> Boolean = { false }

    init {
        nativePathOverlay.touchHandler = { _: NMFOverlay? ->
            onClickListener(this)
            true
        }
    }

    actual fun onClick(listener: (PathOverlay) -> Boolean) {
        onClickListener = listener
    }

    actual internal fun applyOptions(options: PathOptions) {
        coords = options.coords
        width = options.width
        outlineWidth = options.outlineWidth
        color = options.color
        outlineColor = options.outlineColor
        passedColor = options.passedColor
        passedOutlineColor = options.passedOutlineColor
        progress = options.progress
        patternInterval = options.patternInterval
        isHideCollidedSymbols = options.isHideCollidedSymbols
        isHideCollidedMarkers = options.isHideCollidedMarkers
        isHideCollidedCaptions = options.isHideCollidedCaptions
        zIndex = options.zIndex
        isVisible = options.isVisible
        tag = options.tag
    }

    actual fun remove() {
        nativePathOverlay.mapView = null
        nativePathOverlay.touchHandler = null
    }
}
