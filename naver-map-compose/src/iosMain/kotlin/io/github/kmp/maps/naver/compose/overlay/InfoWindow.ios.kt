@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import cocoapods.NMapsMap.NMFInfoWindow
import cocoapods.NMapsMap.NMFOverlay
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.InfoWindowOptions
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake

actual open class InfoWindow(internal val nativeInfoWindow: NMFInfoWindow = NMFInfoWindow()) {
    actual var position: LatLng
        get() = nativeInfoWindow.position.toCommon()
        set(value) { nativeInfoWindow.position = value.toNaver() }

    actual var alpha: Float
        get() = nativeInfoWindow.alpha.toFloat()
        set(value) { nativeInfoWindow.alpha = value.toDouble() }

    actual var zIndex: Int
        get() = nativeInfoWindow.zIndex.toInt()
        set(value) { nativeInfoWindow.zIndex = value.toLong() }

    actual var isVisible: Boolean
        get() = nativeInfoWindow.hidden.not()
        set(value) { nativeInfoWindow.hidden = value.not() }

    actual var anchor: Anchor
        get() = nativeInfoWindow.anchor.useContents { Anchor(x.toFloat(), y.toFloat()) }
        set(value) { nativeInfoWindow.anchor = CGPointMake(value.x.toDouble(), value.y.toDouble()) }

    actual var offsetX: Int
        get() = nativeInfoWindow.offsetX.toInt()
        set(value) { nativeInfoWindow.offsetX = value.toLong() }

    actual var offsetY: Int
        get() = nativeInfoWindow.offsetY.toInt()
        set(value) { nativeInfoWindow.offsetY = value.toLong() }

    actual var tag: Any? = null

    actual var text: String = ""
        set(value) { field = value; nativeInfoWindow.invalidate() }
    actual var textColor: Int = 0xFF000000.toInt()
        set(value) { field = value; nativeInfoWindow.invalidate() }
    actual var textSize: Float = 14f
        set(value) { field = value; nativeInfoWindow.invalidate() }
    actual var backgroundColor: Int = 0xFFFFFFFF.toInt()
        set(value) { field = value; nativeInfoWindow.invalidate() }
    actual var cornerRadiusDp: Float = 0f
        set(value) { field = value; nativeInfoWindow.invalidate() }

    actual fun onClick(listener: (InfoWindow) -> Boolean) {
        val self = this
        nativeInfoWindow.touchHandler = { _: NMFOverlay? ->
            listener(self)
        }
    }

    actual internal fun applyOptions(options: InfoWindowOptions) {
        position = options.position
        text = options.text
        alpha = options.alpha
        zIndex = options.zIndex
        anchor = options.anchor
        offsetX = options.offsetX
        offsetY = options.offsetY
        textColor = options.textColor
        textSize = options.textSize
        backgroundColor = options.backgroundColor
        cornerRadiusDp = options.cornerRadiusDp
        isVisible = options.isVisible
        tag = options.tag
    }

    actual fun close() {
        nativeInfoWindow.close()
    }

    internal fun remove() {
        close()
        nativeInfoWindow.touchHandler = null
    }
}
