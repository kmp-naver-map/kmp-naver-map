package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.Marker
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults

/**
 * 마커 오버레이의 표시 옵션을 정의하는 데이터 클래스입니다.
 *
 * Data class defining display options for a marker overlay,
 * including position, icon, captions, zoom range, and collision handling.
 */
data class MarkerOptions(
    val position: LatLng,
    val icon: Any? = null,
    val caption: String = "",
    val subCaption: String = "",
    val alpha: Float = 1f,
    val isVisible: Boolean = true,
    val isFlat: Boolean = false,
    val isForceShowCaption: Boolean = false,
    val isForceShowIcon: Boolean = false,
    val zIndex: Int = 0,
    val globalZIndex: Int = OverlayDefaults.MARKER_GLOBAL_Z_INDEX,
    val width: Float = Marker.MarkerSize.AUTO,
    val height: Float = Marker.MarkerSize.AUTO,
    val angle: Float = 0f,
    val anchor: Anchor = Anchor.CenterBottom,
    val minZoom: Double = OverlayDefaults.MIN_ZOOM,
    val maxZoom: Double = OverlayDefaults.MAX_ZOOM,
    val isMinZoomInclusive: Boolean = true,
    val isMaxZoomInclusive: Boolean = true,

    // 캡션 상세 설정
    val captionColor: Int = OverlayDefaults.COLOR_BLACK,
    val captionHaloColor: Int = OverlayDefaults.COLOR_WHITE,
    val captionTextSize: Float = 12f,
    val captionMinZoom: Double = OverlayDefaults.MIN_ZOOM,
    val captionMaxZoom: Double = OverlayDefaults.MAX_ZOOM,
    val captionRequestedWidth: Float = 0f,
    val captionOffset: Float = 0f,
    val captionPerspectiveEnabled: Boolean = false,

    // 서브 캡션 상세 설정
    val subCaptionColor: Int = OverlayDefaults.COLOR_BLACK,
    val subCaptionHaloColor: Int = OverlayDefaults.COLOR_WHITE,
    val subCaptionTextSize: Float = 10f,
    val subCaptionMinZoom: Double = OverlayDefaults.MIN_ZOOM,
    val subCaptionMaxZoom: Double = OverlayDefaults.MAX_ZOOM,
    val subCaptionRequestedWidth: Float = 0f,

    // 중첩 관리
    val isHideCollidedMarkers: Boolean = false,
    val isHideCollidedSymbols: Boolean = false,
    val isHideCollidedCaptions: Boolean = false,

    // 효과
    val isIconPerspectiveEnabled: Boolean = false,
    val iconTintColor: Int = OverlayDefaults.COLOR_TRANSPARENT,

    val tag: Any? = null,
)
