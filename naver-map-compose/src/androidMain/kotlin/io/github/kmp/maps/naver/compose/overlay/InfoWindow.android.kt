package io.github.kmp.maps.naver.compose.overlay

import android.graphics.PointF
import com.naver.maps.map.overlay.InfoWindow as NaverInfoWindow
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.LatLng

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

    actual var anchor: Pair<Float, Float>
        get() = Pair(nativeInfoWindow.anchor.x, nativeInfoWindow.anchor.y)
        set(value) { nativeInfoWindow.anchor = PointF(value.first, value.second) }

    actual var offsetX: Int
        get() = nativeInfoWindow.offsetX
        set(value) { nativeInfoWindow.offsetX = value }

    actual var offsetY: Int
        get() = nativeInfoWindow.offsetY
        set(value) { nativeInfoWindow.offsetY = value }

    actual var tag: Any?
        get() = nativeInfoWindow.tag
        set(value) { nativeInfoWindow.tag = value }

    // 텍스트 및 스타일 속성 (안드로이드에서는 Adapter를 통해 처리해야 하므로 상태만 유지하거나 Adapter를 갱신함)
    actual var text: String = ""
    actual var textColor: Int = 0xFF000000.toInt()
    actual var textSize: Float = 14f
    actual var backgroundColor: Int = 0xFFFFFFFF.toInt()
    actual var cornerRadiusDp: Float = 0f

    actual fun onClick(listener: (InfoWindow) -> Boolean) {
        nativeInfoWindow.setOnClickListener {
            listener(this)
        }
    }

    actual fun close() {
        nativeInfoWindow.close()
    }
}
