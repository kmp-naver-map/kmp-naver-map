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

    private var _lastOptions: InfoWindowOptions? = null

    actual internal fun applyOptions(options: InfoWindowOptions) {
        val prev = _lastOptions
        // text/color/size 등의 setter는 매번 invalidate()로 뷰를 다시 그리므로,
        // 변경된 속성만 적용해 불필요한 재렌더를 막는다.
        if (prev == null || prev.position != options.position) position = options.position
        if (prev == null || prev.text != options.text) text = options.text
        if (prev == null || prev.alpha != options.alpha) alpha = options.alpha
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.anchor != options.anchor) anchor = options.anchor
        if (prev == null || prev.offsetX != options.offsetX) offsetX = options.offsetX
        if (prev == null || prev.offsetY != options.offsetY) offsetY = options.offsetY
        if (prev == null || prev.textColor != options.textColor) textColor = options.textColor
        if (prev == null || prev.textSize != options.textSize) textSize = options.textSize
        if (prev == null || prev.backgroundColor != options.backgroundColor) backgroundColor = options.backgroundColor
        if (prev == null || prev.cornerRadiusDp != options.cornerRadiusDp) cornerRadiusDp = options.cornerRadiusDp
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        tag = options.tag
        _lastOptions = options
    }

    actual fun close() {
        nativeInfoWindow.close()
    }

    internal fun remove() {
        close()
        nativeInfoWindow.touchHandler = null
    }
}
