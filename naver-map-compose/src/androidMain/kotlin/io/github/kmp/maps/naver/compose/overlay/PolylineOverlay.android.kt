package io.github.kmp.maps.naver.compose.overlay

import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin

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


    actual val pattern: List<Float>
        get() = nativePolyline.pattern.map { it.toFloat() } ?: emptyList()

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

    actual fun remove() {
        nativePolyline.map = null
    }
}
