@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMFOverlay
import cocoapods.NMapsMap.NMFPolylineOverlay
import cocoapods.NMapsMap.NMGLineString
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toCommonLineCap
import io.github.kmp.maps.naver.compose.internal.toCommonLineJoin
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.internal.toUIColor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin
import io.github.kmp.maps.naver.compose.options.PolylineOptions
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNumber

actual open class PolylineOverlay(internal val nativePolyline: NMFPolylineOverlay) {
    actual var coords: List<LatLng>
        get() = nativePolyline.line.points().map { (it as cocoapods.NMapsMap.NMGLatLng).toCommon() }
        set(value) {
            nativePolyline.line = NMGLineString.lineStringWithPoints(value.map { it.toNaver() })
        }

    actual var color: Int = 0
        set(value) {
            field = value
            nativePolyline.color = value.toUIColor()
        }

    actual var width: Float
        get() = nativePolyline.width.toFloat()
        set(value) {
            nativePolyline.width = value.toDouble()
        }

    actual var capType: LineCap
        get() = nativePolyline.capType.value.toInt().toCommonLineCap()
        set(value) {
            nativePolyline.capType = value.toNaver()
        }

    actual var joinType: LineJoin
        get() = nativePolyline.joinType.value.toInt().toCommonLineJoin()
        set(value) {
            nativePolyline.joinType = value.toNaver()
        }

    actual var zIndex: Int
        get() = nativePolyline.zIndex.toInt()
        set(value) {
            nativePolyline.zIndex = value.toLong()
        }

    actual var isVisible: Boolean
        get() = nativePolyline.hidden.not()
        set(value) {
            nativePolyline.hidden = value.not()
        }

    actual fun onClick(listener: (PolylineOverlay) -> Boolean) {
        val self = this
        nativePolyline.touchHandler = { _: NMFOverlay? ->
            listener(self)
        }
    }

    actual internal fun applyOptions(options: PolylineOptions) {
        coords = options.coords
        color = options.color
        width = options.width
        capType = options.capType
        joinType = options.joinType
        zIndex = options.zIndex
        isVisible = options.isVisible
        tag = options.tag
    }

    actual fun remove() {
        nativePolyline.mapView = null
        nativePolyline.touchHandler = null
    }

    actual var tag: Any? = null

    actual val pattern: List<Float>
        get() = nativePolyline.pattern?.map { (it as NSNumber).floatValue } ?: emptyList()
}
