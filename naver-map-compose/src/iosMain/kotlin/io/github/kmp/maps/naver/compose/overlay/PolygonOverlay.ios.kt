@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMGLatLng
import cocoapods.NMapsMap.NMGPolygon
import cocoapods.NMapsMap.NMGLineString
import cocoapods.NMapsMap.NMFOverlay
import cocoapods.NMapsMap.NMFPolygonOverlay
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.internal.toUIColor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.PolygonOptions
import kotlinx.cinterop.ExperimentalForeignApi

actual open class PolygonOverlay internal constructor(
    internal val nativePolygon: NMFPolygonOverlay
) {
    actual var tag: Any? = null

    actual var coords: List<LatLng>
        get() = nativePolygon.polygon.exteriorRing().points().map { (it as NMGLatLng).toCommon() }
        set(value) {
            updatePolygon(value, holes)
        }

    actual var holes: List<List<LatLng>>
        get() = nativePolygon.polygon.interiorRings().map { ring ->
            (ring as NMGLineString).points().map { (it as NMGLatLng).toCommon() }
        }
        set(value) {
            updatePolygon(coords, value)
        }

    private fun updatePolygon(outer: List<LatLng>, inners: List<List<LatLng>>) {
        val exteriorRing = NMGLineString.lineStringWithPoints(outer.map { it.toNaver() })
        val interiorRings = inners.map { ring ->
            NMGLineString.lineStringWithPoints(ring.map { it.toNaver() })
        }
        nativePolygon.polygon = NMGPolygon.polygonWithRing(exteriorRing, interiorRings = interiorRings)
    }

    actual var fillColor: Int = 0
        set(value) {
            field = value
            nativePolygon.fillColor = value.toUIColor()
        }

    actual var outlineColor: Int = 0
        set(value) {
            field = value
            nativePolygon.outlineColor = value.toUIColor()
        }

    actual var outlineWidth: Float
        get() = nativePolygon.outlineWidth.toFloat()
        set(value) {
            nativePolygon.outlineWidth = value.toLong().toULong()
        }

    actual var zIndex: Int
        get() = nativePolygon.zIndex.toInt()
        set(value) {
            nativePolygon.zIndex = value.toLong()
        }

    actual var isVisible: Boolean
        get() = nativePolygon.hidden.not()
        set(value) {
            nativePolygon.hidden = value.not()
        }

    actual fun onClick(listener: (PolygonOverlay) -> Boolean) {
        val self = this
        nativePolygon.touchHandler = { _: NMFOverlay? ->
            listener(self)
        }
    }

    private var _lastOptions: PolygonOptions? = null

    actual internal fun applyOptions(options: PolygonOptions) {
        val prev = _lastOptions
        // coords/holes setter는 네이티브 polygon 전체를 재구성하므로, 변경된 것만 적용.
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
        nativePolygon.mapView = null
        nativePolygon.touchHandler = null
    }
}
