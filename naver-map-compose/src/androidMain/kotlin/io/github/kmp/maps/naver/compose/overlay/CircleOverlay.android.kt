package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.CircleOptions

actual open class CircleOverlay internal constructor(
    internal val nativeCircle: com.naver.maps.map.overlay.CircleOverlay
) {
    actual var center: LatLng
        get() = nativeCircle.center.toCommon()
        set(value) { nativeCircle.center = value.toNaver() }

    actual var radius: Double
        get() = nativeCircle.radius
        set(value) { nativeCircle.radius = value }

    actual var fillColor: Int
        get() = nativeCircle.color
        set(value) { nativeCircle.color = value }

    actual var outlineColor: Int
        get() = nativeCircle.outlineColor
        set(value) { nativeCircle.outlineColor = value }

    actual var outlineWidth: Float
        get() = nativeCircle.outlineWidth.toFloat()
        set(value) { nativeCircle.outlineWidth = value.toInt() }

    actual var zIndex: Int
        get() = nativeCircle.zIndex
        set(value) { nativeCircle.zIndex = value }

    actual var isVisible: Boolean
        get() = nativeCircle.isVisible
        set(value) { nativeCircle.isVisible = value }

    actual var tag: Any?
        get() = nativeCircle.tag
        set(value) { nativeCircle.tag = value }

    actual fun onClick(listener: (CircleOverlay) -> Boolean) {
        nativeCircle.setOnClickListener {
            listener(this)
        }
    }

    actual internal fun applyOptions(options: CircleOptions) {
        center = options.center
        radius = options.radius
        fillColor = options.fillColor
        outlineColor = options.outlineColor
        outlineWidth = options.outlineWidth
        zIndex = options.zIndex
        isVisible = options.isVisible
        tag = options.tag
    }

    actual fun remove() {
        nativeCircle.map = null
    }
}

internal class AndroidCircleOverlay(
    nativeCircle: com.naver.maps.map.overlay.CircleOverlay
) : CircleOverlay(nativeCircle)