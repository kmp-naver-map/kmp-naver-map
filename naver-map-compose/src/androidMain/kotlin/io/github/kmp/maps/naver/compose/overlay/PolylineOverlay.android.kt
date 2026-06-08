package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin
import io.github.kmp.maps.naver.compose.options.PolylineOptions

actual open class PolylineOverlay internal constructor(
    internal val nativePolyline: com.naver.maps.map.overlay.PolylineOverlay
) {
    actual var coords: List<LatLng>
        get() = nativePolyline.coords.map { it.toCommon() }
        set(value) { nativePolyline.coords = value.map { it.toNaver() } }

    actual var color: Int
        get() = nativePolyline.color
        set(value) { nativePolyline.color = value }

    actual var width: Float
        get() = nativePolyline.width.toFloat()
        set(value) { nativePolyline.width = value.toInt() }


    // Android SDK의 pattern은 val(read-only)이므로 생성 시에만 적용 가능
    private var _pattern: List<Float> = emptyList()
    actual var pattern: List<Float>
        get() = _pattern
        set(value) { _pattern = value }

    actual var capType: LineCap
        get() = nativePolyline.capType.toCommon()
        set(value) { nativePolyline.capType = value.toNaver() }

    actual var joinType: LineJoin
        get() = nativePolyline.joinType.toCommon()
        set(value) { nativePolyline.joinType = value.toNaver() }

    actual var zIndex: Int
        get() = nativePolyline.zIndex
        set(value) { nativePolyline.zIndex = value }

    actual var isVisible: Boolean
        get() = nativePolyline.isVisible
        set(value) { nativePolyline.isVisible = value }

    actual var tag: Any?
        get() = nativePolyline.tag
        set(value) { nativePolyline.tag = value }

    actual fun onClick(listener: (PolylineOverlay) -> Boolean) {
        nativePolyline.setOnClickListener { listener(this) }
    }

    private var _lastOptions: PolylineOptions? = null

    actual internal fun applyOptions(options: PolylineOptions) {
        val prev = _lastOptions
        if (prev == null || prev.coords != options.coords) coords = options.coords
        if (prev == null || prev.color != options.color) color = options.color
        if (prev == null || prev.width != options.width) width = options.width
        if (prev == null || prev.pattern != options.pattern) {
            if (options.pattern.isNotEmpty()) pattern = options.pattern
        }
        if (prev == null || prev.capType != options.capType) capType = options.capType
        if (prev == null || prev.joinType != options.joinType) joinType = options.joinType
        if (prev == null || prev.zIndex != options.zIndex) zIndex = options.zIndex
        if (prev == null || prev.isVisible != options.isVisible) isVisible = options.isVisible
        tag = options.tag
        _lastOptions = options
    }

    actual fun remove() {
        nativePolyline.map = null
    }
}
