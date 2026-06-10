package io.github.kmp.maps.naver.compose.overlay

import com.naver.maps.map.overlay.ArrowheadPathOverlay as NaverArrowheadPathOverlay
import com.naver.maps.map.overlay.Overlay
import io.github.kmp.maps.naver.compose.internal.dpToPx
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

    // 입력은 dp(마커와 통일). 네이티브는 px를 받으므로 dpToPx 변환.
    actual var width: Float
        get() = nativeArrowheadPathOverlay.width.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.width = value.dpToPx().toInt()
        }

    actual var outlineWidth: Float
        get() = nativeArrowheadPathOverlay.outlineWidth.toFloat()
        set(value) {
            nativeArrowheadPathOverlay.outlineWidth = value.dpToPx().toInt()
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
        // 입력은 dp(마커와 통일). 네이티브는 px를 받으므로 dpToPx 변환.
        set(value) {
            nativeArrowheadPathOverlay.elevation = value.dpToPx().toInt()
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

    private var _lastOptions: ArrowheadPathOptions? = null

    actual internal fun applyOptions(options: ArrowheadPathOptions) {
        val prev = _lastOptions
        if (prev == null || prev.coords != options.coords) coords = options.coords
        if (prev == null || prev.width != options.width) width = options.width
        if (prev == null || prev.outlineWidth != options.outlineWidth) outlineWidth = options.outlineWidth
        if (prev == null || prev.color != options.color) color = options.color
        if (prev == null || prev.outlineColor != options.outlineColor) outlineColor = options.outlineColor
        if (prev == null || prev.elevation != options.elevation) elevation = options.elevation
        if (prev == null || prev.headSizeRatio != options.headSizeRatio) headSizeRatio = options.headSizeRatio
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        tag = options.tag
        _lastOptions = options
    }

    actual fun remove() {
        nativeArrowheadPathOverlay.map = null
    }
}
