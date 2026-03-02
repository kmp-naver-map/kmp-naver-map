package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng

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

    actual fun remove() {
        nativePolygon.map = null
    }
}

internal class AndroidPolygonOverlay(
    nativePolygon: com.naver.maps.map.overlay.PolygonOverlay
) : PolygonOverlay(nativePolygon)
