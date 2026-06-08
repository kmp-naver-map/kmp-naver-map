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

    private var _lastOptions: CircleOptions? = null

    actual internal fun applyOptions(options: CircleOptions) {
        val prev = _lastOptions
        if (prev == null || prev.center != options.center) center = options.center
        if (prev == null || prev.radius != options.radius) radius = options.radius
        if (prev == null || prev.fillColor != options.fillColor) fillColor = options.fillColor
        if (prev == null || prev.outlineColor != options.outlineColor) outlineColor = options.outlineColor
        if (prev == null || prev.outlineWidth != options.outlineWidth) outlineWidth = options.outlineWidth
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        tag = options.tag
        _lastOptions = options
    }

    actual fun remove() {
        nativeCircle.map = null
    }
}

internal class AndroidCircleOverlay(
    nativeCircle: com.naver.maps.map.overlay.CircleOverlay
) : CircleOverlay(nativeCircle)