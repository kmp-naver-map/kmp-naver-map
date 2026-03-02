package io.github.kmp.maps.naver.compose.options

import androidx.compose.runtime.Immutable
import io.github.kmp.maps.naver.compose.overlay.Marker

/**
 * 위치 오버레이(LocationOverlay)의 설정을 정의하는 데이터 클래스입니다.
 */
@Immutable
data class LocationOverlayOptions(
    val isVisible: Boolean = true,
    val icon: Any? = null, // OverlayImage 또는 DrawableResource
    val width: Float = Marker.MarkerSize.AUTO,
    val height: Float = Marker.MarkerSize.AUTO,
    val anchor: Pair<Float, Float> = Pair(0.5f, 0.5f),
    val bearing: Float = 0f,
    val globalZIndex: Int = 300000, // NMF_LOCATION_OVERLAY_GLOBAL_Z_INDEX
    
    // 원근 효과
    val isIconPerspectiveEnabled: Boolean = false,
    
    // 정확도 원 설정
    val circleRadius: Double = 0.0,
    val circleColor: Int = 0x00000000,
    val circleOutlineWidth: Float = 0f,
    val circleOutlineColor: Int = 0x00000000,
    
    // 보조 아이콘 설정 (방향 표시 등)
    val subIcon: Any? = null, 
    val subIconWidth: Float = Marker.MarkerSize.AUTO,
    val subIconHeight: Float = Marker.MarkerSize.AUTO,
    val subIconAnchor: Pair<Float, Float> = Pair(0.5f, 0.5f),
    val isSubIconVisible: Boolean = true,
)
