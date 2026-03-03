package io.github.kmp.maps.naver.compose.overlay

import com.naver.maps.map.overlay.ArrowheadPathOverlay as NaverArrowheadPathOverlay
import com.naver.maps.map.overlay.Overlay
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.ArrowheadPathOptions

actual class ArrowheadPathOverlay(
    internal val nativeArrowheadPathOverlay: NaverArrowheadPathOverlay = NaverArrowheadPathOverlay()
) {
    actual var coords: List<LatLng>
        get() = nativeArrowheadPathOverlay.coords.map { LatLng(it.latitude, it.longitude) }
        set(value) {
            nativeArrowheadPathOverlay.coords = value.map { it.toNaver() }
        }

    actual var width: Float
        get() = nativeArrowheadPathOverlay.width.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.width = value.toInt()
        }

    actual var outlineWidth: Float
        get() = nativeArrowheadPathOverlay.outlineWidth.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.outlineWidth = value.toInt()
        }

    actual var color: Int
        get() = nativeArrowheadPathOverlay.color
        set(value) {
            nativeArrowheadPathOverlay.color = value
        }

    actual var outlineColor: Int
        get() = nativeArrowheadPathOverlay.outlineColor
        set(value) {
            nativeArrowheadPathOverlay.outlineColor = value
        }

    actual var elevation: Float
        get() = nativeArrowheadPathOverlay.elevation.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.elevation = value.toInt()
        }

    actual var headSizeRatio: Float
        get() = nativeArrowheadPathOverlay.headSizeRatio
        set(value) {
            nativeArrowheadPathOverlay.headSizeRatio = value
        }

    actual var zIndex: Int
        get() = nativeArrowheadPathOverlay.zIndex
        set(value) {
            nativeArrowheadPathOverlay.zIndex = value
        }

    actual var isVisible: Boolean
        get() = nativeArrowheadPathOverlay.isVisible
        set(value) {
            nativeArrowheadPathOverlay.isVisible = value
        }

    actual var tag: Any?
        get() = nativeArrowheadPathOverlay.tag
        set(value) {
            nativeArrowheadPathOverlay.tag = value
        }

    private var onClickListener: (ArrowheadPathOverlay) -> Boolean = { false }

    init {
        nativeArrowheadPathOverlay.onClickListener = Overlay.OnClickListener {
            onClickListener(this)
            true
        }
    }

    actual fun onClick(listener: (ArrowheadPathOverlay) -> Boolean) {
        onClickListener = listener
    }

    actual internal fun applyOptions(options: ArrowheadPathOptions) {
        coords = options.coords
        width = options.width
        outlineWidth = options.outlineWidth
        color = options.color
        outlineColor = options.outlineColor
        elevation = options.elevation
        headSizeRatio = options.headSizeRatio
        zIndex = options.zIndex
        isVisible = options.isVisible
        tag = options.tag
    }

    actual fun remove() {
        nativeArrowheadPathOverlay.map = null
    }
}
