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

    actual internal fun applyOptions(options: PolygonOptions) {
        coords = options.coords
        holes = options.holes
        fillColor = options.fillColor
        outlineColor = options.outlineColor
        outlineWidth = options.outlineWidth
        zIndex = options.zIndex
        isVisible = options.isVisible
        tag = options.tag
    }

    actual fun remove() {
        nativePolygon.mapView = null
        nativePolygon.touchHandler = null
    }
}
