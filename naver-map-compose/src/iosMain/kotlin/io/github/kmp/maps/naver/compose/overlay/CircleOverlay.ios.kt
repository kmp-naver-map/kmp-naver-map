@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMFCircleOverlay
import cocoapods.NMapsMap.NMFOverlay
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.internal.toUIColor
import io.github.kmp.maps.naver.compose.model.LatLng
import kotlinx.cinterop.ExperimentalForeignApi

actual class CircleOverlay internal constructor(
    internal val nativeCircle: NMFCircleOverlay
) {
    actual var tag: Any? = null

    actual var center: LatLng
        get() = nativeCircle.center.toCommon()
        set(value) {
            nativeCircle.center = value.toNaver()
        }

    actual var radius: Double
        get() = nativeCircle.radius
        set(value) {
            nativeCircle.radius = value
        }

    actual var fillColor: Int = 0
        set(value) {
            field = value
            nativeCircle.fillColor = value.toUIColor()
        }

    actual var outlineColor: Int = 0
        set(value) {
            field = value
            nativeCircle.outlineColor = value.toUIColor()
        }

    actual var outlineWidth: Float
        get() = nativeCircle.outlineWidth.toFloat()
        set(value) {
            nativeCircle.outlineWidth = value.toDouble()
        }

    actual var zIndex: Int
        get() = nativeCircle.zIndex.toInt()
        set(value) {
            nativeCircle.zIndex = value.toLong()
        }

    actual var isVisible: Boolean
        get() = nativeCircle.hidden.not()
        set(value) {
            nativeCircle.hidden = value.not()
        }

    actual fun onClick(listener: (CircleOverlay) -> Boolean) {
        val self = this
        nativeCircle.touchHandler = { _: NMFOverlay? ->
            listener(self)
        }
    }

    actual fun remove() {
        nativeCircle.mapView = null
        nativeCircle.touchHandler = null
    }
}
