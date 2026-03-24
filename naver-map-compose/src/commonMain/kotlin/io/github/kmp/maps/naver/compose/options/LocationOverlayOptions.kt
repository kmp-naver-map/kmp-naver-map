package io.github.kmp.maps.naver.compose.options

import androidx.compose.runtime.Immutable
import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.overlay.Marker
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 위치 오버레이(LocationOverlay)의 표시 옵션을 정의하는 데이터 클래스입니다.
 *
 * Data class defining display options for the location overlay,
 * including icon, accuracy circle, bearing, and sub-icon settings.
 */
@Immutable
data class LocationOverlayOptions(
    val isVisible: Boolean = false,
    val icon: Any? = null,
    val width: Float = Marker.MarkerSize.AUTO,
    val height: Float = Marker.MarkerSize.AUTO,
    val anchor: Anchor = Anchor.Center,
    val bearing: Float = 0f,
    val globalZIndex: Int = OverlayDefaults.LOCATION_OVERLAY_GLOBAL_Z_INDEX,

    // 원근 효과
    val isIconPerspectiveEnabled: Boolean = false,

    // 정확도 원 설정
    // ⚠️ 단위 주의: Android = meters(미터), iOS = pt(포인트). 동일한 값이 플랫폼마다 다른 크기로 표시됩니다.
    val circleRadius: Double = 0.0,
    val circleColor: Int = OverlayDefaults.COLOR_TRANSPARENT,
    val circleOutlineWidth: Float = 0f,
    val circleOutlineColor: Int = OverlayDefaults.COLOR_TRANSPARENT,

    // 보조 아이콘 설정 (방향 표시 등)
    val subIcon: Any? = null,
    val subIconWidth: Float = Marker.MarkerSize.AUTO,
    val subIconHeight: Float = Marker.MarkerSize.AUTO,
    val subIconAnchor: Anchor = Anchor.Center,
    val isSubIconVisible: Boolean = true,
)
