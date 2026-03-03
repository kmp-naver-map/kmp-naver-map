package io.github.kmp.maps.naver.compose.overlay

import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.PathOverlay as NaverPathOverlay
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

    actual var width: Float
        get() = nativePathOverlay.width.toFloat() // in Px
        set(value) {
            nativePathOverlay.width = value.toInt()
        }

    actual var outlineWidth: Float
        get() = nativePathOverlay.outlineWidth.toFloat()
        set(value) {
            nativePathOverlay.outlineWidth = value.toInt()
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
        set(value) {
            nativePathOverlay.patternInterval = value.toInt()
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

    actual internal fun applyOptions(options: PathOptions) {
        coords = options.coords
        width = options.width
        outlineWidth = options.outlineWidth
        color = options.color
        outlineColor = options.outlineColor
        passedColor = options.passedColor
        passedOutlineColor = options.passedOutlineColor
        progress = options.progress
        patternInterval = options.patternInterval
        isHideCollidedSymbols = options.isHideCollidedSymbols
        isHideCollidedMarkers = options.isHideCollidedMarkers
        isHideCollidedCaptions = options.isHideCollidedCaptions
        zIndex = options.zIndex
        isVisible = options.isVisible
        tag = options.tag
    }

    actual fun remove() {
        nativePathOverlay.map = null
    }


}
