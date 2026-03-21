package io.github.kmp.maps.naver.compose.overlay

import android.graphics.PointF
import com.naver.maps.map.overlay.InfoWindow as NaverInfoWindow
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.InfoWindowOptions

/**
 * Android용 InfoWindow 구현체입니다.
 */
actual open class InfoWindow(internal val nativeInfoWindow: NaverInfoWindow) {
    actual var position: LatLng
        get() = nativeInfoWindow.position.toCommon()
        set(value) { nativeInfoWindow.position = value.toNaver() }

    actual var alpha: Float
        get() = nativeInfoWindow.alpha
        set(value) { nativeInfoWindow.alpha = value }

    actual var zIndex: Int
        get() = nativeInfoWindow.zIndex
        set(value) { nativeInfoWindow.zIndex = value }

    actual var isVisible: Boolean
        get() = nativeInfoWindow.isVisible
        set(value) { nativeInfoWindow.isVisible = value }

    actual var anchor: Anchor
        get() = Anchor(nativeInfoWindow.anchor.x, nativeInfoWindow.anchor.y)
        set(value) { nativeInfoWindow.anchor = PointF(value.x, value.y) }

    actual var offsetX: Int
        get() = nativeInfoWindow.offsetX
        set(value) { nativeInfoWindow.offsetX = value }

    actual var offsetY: Int
        get() = nativeInfoWindow.offsetY
        set(value) { nativeInfoWindow.offsetY = value }

    actual var tag: Any?
        get() = nativeInfoWindow.tag
        set(value) { nativeInfoWindow.tag = value }

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
        nativeInfoWindow.setOnClickListener {
            listener(this)
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
}
