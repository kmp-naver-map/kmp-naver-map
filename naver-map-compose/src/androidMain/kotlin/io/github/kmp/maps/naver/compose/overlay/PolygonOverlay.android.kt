package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.PolygonOptions

// overlay/PolygonOverlay.android.kt
actual open class PolygonOverlay internal constructor(
    internal val nativePolygon: com.naver.maps.map.overlay.PolygonOverlay
) {
    actual var coords: List<LatLng>
        get() = nativePolygon.coords.map { it.toCommon() }
        set(value) { nativePolygon.coords = value.map { it.toNaver() } }

    actual var holes: List<List<LatLng>>
        get() = nativePolygon.holes.map { hole -> hole.map { it.toCommon() } }
        set(value) { nativePolygon.holes = value.map { hole -> hole.map { it.toNaver() } } }

    actual var fillColor: Int
        get() = nativePolygon.color
        set(value) { nativePolygon.color = value }

    actual var outlineColor: Int
        get() = nativePolygon.outlineColor
        set(value) { nativePolygon.outlineColor = value }

    actual var outlineWidth: Float
        get() = nativePolygon.outlineWidth.toFloat()
        set(value) { nativePolygon.outlineWidth = value.toInt() }

    actual var zIndex: Int
        get() = nativePolygon.zIndex
        set(value) { nativePolygon.zIndex = value }

    actual var isVisible: Boolean
        get() = nativePolygon.isVisible
        set(value) { nativePolygon.isVisible = value }

    actual var tag: Any?
        get() = nativePolygon.tag
        set(value) { nativePolygon.tag = value }

    actual fun onClick(listener: (PolygonOverlay) -> Boolean) {
        nativePolygon.setOnClickListener {
            listener(this)
        }
    }

    private var _lastOptions: PolygonOptions? = null

    actual internal fun applyOptions(options: PolygonOptions) {
        val prev = _lastOptions
        if (prev == null || prev.coords != options.coords) coords = options.coords
        if (prev == null || prev.holes != options.holes) holes = options.holes
        if (prev == null || prev.fillColor != options.fillColor) fillColor = options.fillColor
        if (prev == null || prev.outlineColor != options.outlineColor) outlineColor = options.outlineColor
        if (prev == null || prev.outlineWidth != options.outlineWidth) outlineWidth = options.outlineWidth
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        tag = options.tag
        _lastOptions = options
    }

    actual fun remove() {
        nativePolygon.map = null
    }
}

internal class AndroidPolygonOverlay(
    nativePolygon: com.naver.maps.map.overlay.PolygonOverlay
) : PolygonOverlay(nativePolygon)
