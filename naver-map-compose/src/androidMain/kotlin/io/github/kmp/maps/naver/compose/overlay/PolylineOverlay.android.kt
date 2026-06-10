package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.internal.dpToPx
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
        // 입력은 dp(마커와 통일). 네이티브는 px를 받으므로 dpToPx 변환.
        set(value) { nativePolyline.width = value.dpToPx().toInt() }


    // Android SDK의 PolylineOverlay는 setPattern(int...)로 패턴 설정이 가능하다(픽셀 단위).
    // varargs setter라 Kotlin 프로퍼티(nativePolyline.pattern)는 val로 노출되므로,
    // setPattern(...)을 spread로 직접 호출한다. 입력값은 dp로 받아(다른 선 두께와 통일)
    // dpToPx 변환 후 반영하고, getter는 입력한 dp 값을 그대로 돌려주기 위해 backing field를 유지한다.
    private var _pattern: List<Float> = emptyList()
    actual var pattern: List<Float>
        get() = _pattern
        set(value) {
            _pattern = value
            nativePolyline.setPattern(*value.map { it.dpToPx().toInt() }.toIntArray())
        }

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
