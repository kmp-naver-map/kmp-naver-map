package io.github.kmp.maps.naver.compose.options

import androidx.compose.runtime.Immutable
import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 정보 창(InfoWindow)의 표시 옵션을 정의하는 데이터 클래스입니다.
 *
 * Data class defining display options for an info window overlay,
 * including text content, colors, positioning, and corner radius.
 */
@Immutable
data class InfoWindowOptions(
    val position: LatLng = LatLng(0.0, 0.0),
    val alpha: Float = 1f,
    val zIndex: Int = OverlayDefaults.INFO_WINDOW_GLOBAL_Z_INDEX,
    val anchor: Anchor = Anchor.CenterBottom,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val text: String = "",
    val textColor: Int = OverlayDefaults.COLOR_BLACK,
    val textSize: Float = 14f,
    val backgroundColor: Int = OverlayDefaults.COLOR_WHITE,
    val cornerRadiusDp: Float = 0f,
    val isVisible: Boolean = true,
    val tag: Any? = null,
)
