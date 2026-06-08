@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMGLatLng
import cocoapods.NMapsMap.NMFOverlay
import cocoapods.NMapsMap.NMFArrowheadPath
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.internal.toUIColor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.ArrowheadPathOptions
import kotlinx.cinterop.ExperimentalForeignApi
actual class ArrowheadPathOverlay(
    internal val nativeArrowheadPathOverlay: NMFArrowheadPath = NMFArrowheadPath()
) {
    actual var coords: List<LatLng>
        get() = (nativeArrowheadPathOverlay.points()).map { (it as NMGLatLng).toCommon() }
        set(value) {
            nativeArrowheadPathOverlay.points = value.map { it.toNaver() }
        }

    actual var width: Float
        get() = nativeArrowheadPathOverlay.width.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.width = value.toDouble()
        }

    actual var outlineWidth: Float
        get() = nativeArrowheadPathOverlay.outlineWidth.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.outlineWidth = value.toDouble()
        }

    actual var color: Int = 0
        set(value) {
            field = value
            nativeArrowheadPathOverlay.color = value.toUIColor()
        }

    actual var outlineColor: Int = 0
        set(value) {
            field = value
            nativeArrowheadPathOverlay.outlineColor = value.toUIColor()
        }

    actual var elevation: Float
        get() = nativeArrowheadPathOverlay.elevation.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.elevation = value.toDouble()
        }

    actual var headSizeRatio: Float
        get() = nativeArrowheadPathOverlay.headSizeRatio.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.headSizeRatio = value.toDouble()
        }

    actual var zIndex: Int
        get() = nativeArrowheadPathOverlay.zIndex.toInt()
        set(value) {
            nativeArrowheadPathOverlay.zIndex = value.toLong()
        }

    actual var isVisible: Boolean
        get() = !nativeArrowheadPathOverlay.hidden
        set(value) {
            nativeArrowheadPathOverlay.hidden = !value
        }

    actual var tag: Any? = null

    private var onClickListener: (ArrowheadPathOverlay) -> Boolean = { false }

    init {
        nativeArrowheadPathOverlay.touchHandler = { _: NMFOverlay? ->
            onClickListener(this)
            true
        }
    }

    actual fun onClick(listener: (ArrowheadPathOverlay) -> Boolean) {
        onClickListener = listener
    }

    private var _lastOptions: ArrowheadPathOptions? = null

    actual internal fun applyOptions(options: ArrowheadPathOptions) {
        val prev = _lastOptions
        if (prev == null || prev.coords != options.coords) coords = options.coords
        if (prev == null || prev.width != options.width) width = options.width
        if (prev == null || prev.outlineWidth != options.outlineWidth) outlineWidth = options.outlineWidth
        if (prev == null || prev.color != options.color) color = options.color
        if (prev == null || prev.outlineColor != options.outlineColor) outlineColor = options.outlineColor
        if (prev == null || prev.elevation != options.elevation) elevation = options.elevation
        if (prev == null || prev.headSizeRatio != options.headSizeRatio) headSizeRatio = options.headSizeRatio
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        tag = options.tag
        _lastOptions = options
    }

    actual fun remove() {
        nativeArrowheadPathOverlay.mapView = null
        nativeArrowheadPathOverlay.touchHandler = null
    }
}
