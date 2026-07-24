package io.github.kmp.maps.naver.compose.overlay

import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.PathOverlay as NaverPathOverlay
import io.github.kmp.maps.naver.compose.internal.dpToPx
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.PathOptions

actual class PathOverlay(
    internal val nativePathOverlay: NaverPathOverlay = NaverPathOverlay()
) {
    actual var coords: List<LatLng>
        get() = nativePathOverlay.coords.map { LatLng(it.latitude, it.longitude) }
        set(value) {
            nativePathOverlay.coords = value.map { it.toNaver() }
        }

    // 입력은 dp(마커와 통일). getter는 네이티브 px를 그대로 반환.
    actual var width: Float
        get() = nativePathOverlay.width.toFloat() // in Px
        set(value) {
            nativePathOverlay.width = value.dpToPx().toInt()
        }

    actual var outlineWidth: Float
        get() = nativePathOverlay.outlineWidth.toFloat()
        set(value) {
            nativePathOverlay.outlineWidth = value.dpToPx().toInt()
        }

    actual var color: Int
        get() = nativePathOverlay.color
        set(value) {
            nativePathOverlay.color = value
        }

    actual var outlineColor: Int
        get() = nativePathOverlay.outlineColor
        set(value) {
            nativePathOverlay.outlineColor = value
        }

    actual var passedColor: Int
        get() = nativePathOverlay.passedColor
        set(value) {
            nativePathOverlay.passedColor = value
        }

    actual var passedOutlineColor: Int
        get() = nativePathOverlay.passedOutlineColor
        set(value) {
            nativePathOverlay.passedOutlineColor = value
        }

    actual var progress: Double
        get() = nativePathOverlay.progress
        set(value) {
            nativePathOverlay.progress = value
        }

    actual var patternInterval: Float
        get() = nativePathOverlay.patternInterval.toFloat()
        // 입력은 dp(마커와 통일). 네이티브는 px를 받으므로 dpToPx 변환.
        set(value) {
            nativePathOverlay.patternInterval = value.dpToPx().toInt()
        }

    actual var patternImage: OverlayImage? = null
        set(value) {
            field = value
            nativePathOverlay.patternImage = value?.nativeImage
        }

    actual var isHideCollidedSymbols: Boolean
        get() = nativePathOverlay.isHideCollidedSymbols
        set(value) {
            nativePathOverlay.isHideCollidedSymbols = value
        }

    actual var isHideCollidedMarkers: Boolean
        get() = nativePathOverlay.isHideCollidedMarkers
        set(value) {
            nativePathOverlay.isHideCollidedMarkers = value
        }

    actual var isHideCollidedCaptions: Boolean
        get() = nativePathOverlay.isHideCollidedCaptions
        set(value) {
            nativePathOverlay.isHideCollidedCaptions = value
        }

    actual var zIndex: Int
        get() = nativePathOverlay.zIndex
        set(value) {
            nativePathOverlay.zIndex = value
        }

    actual var isVisible: Boolean
        get() = nativePathOverlay.isVisible
        set(value) {
            nativePathOverlay.isVisible = value
        }

    actual var tag: Any?
        get() = nativePathOverlay.tag
        set(value) {
            nativePathOverlay.tag = value
        }

    private var onClickListener: (PathOverlay) -> Boolean = { false }

    init {
        nativePathOverlay.onClickListener = Overlay.OnClickListener {
            onClickListener(this)
            true
        }
    }
    actual fun onClick(listener: (PathOverlay) -> Boolean) {
        onClickListener = listener
    }

    private var _lastOptions: PathOptions? = null

    actual internal fun applyOptions(options: PathOptions) {
        val prev = _lastOptions
        // coords 재매핑은 네이티브 지오메트리를 재구성하므로 가장 비싼 작업.
        // progress 등 일부 속성만 바뀔 때 좌표 재매핑을 건너뛰어 성능을 보호한다.
        if (prev == null || prev.coords != options.coords) coords = options.coords
        if (prev == null || prev.width != options.width) width = options.width
        if (prev == null || prev.outlineWidth != options.outlineWidth) outlineWidth = options.outlineWidth
        if (prev == null || prev.color != options.color) color = options.color
        if (prev == null || prev.outlineColor != options.outlineColor) outlineColor = options.outlineColor
        if (prev == null || prev.passedColor != options.passedColor) passedColor = options.passedColor
        if (prev == null || prev.passedOutlineColor != options.passedOutlineColor) passedOutlineColor = options.passedOutlineColor
        if (prev == null || prev.progress != options.progress) progress = options.progress
        if (prev == null || prev.patternInterval != options.patternInterval) patternInterval = options.patternInterval
        if (prev == null || prev.patternImage != options.patternImage) patternImage = options.patternImage
        if (prev == null || prev.isHideCollidedSymbols != options.isHideCollidedSymbols) isHideCollidedSymbols = options.isHideCollidedSymbols
        if (prev == null || prev.isHideCollidedMarkers != options.isHideCollidedMarkers) isHideCollidedMarkers = options.isHideCollidedMarkers
        if (prev == null || prev.isHideCollidedCaptions != options.isHideCollidedCaptions) isHideCollidedCaptions = options.isHideCollidedCaptions
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        tag = options.tag
        _lastOptions = options
    }

    actual fun remove() {
        nativePathOverlay.map = null
    }


}
