package io.github.kmp.maps.naver.compose.options

import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.Marker

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
    val globalZIndex: Int = 200000, // NMF_MARKER_GLOBAL_Z_INDEX
    val width: Float = Marker.MarkerSize.AUTO,
    val height: Float = Marker.MarkerSize.AUTO,
    val angle: Float = 0f,
    val anchor: Pair<Float, Float> = Pair(0.5f, 1f),
    val minZoom: Double = 0.0,
    val maxZoom: Double = 21.0,
    val isMinZoomInclusive: Boolean = true,
    val isMaxZoomInclusive: Boolean = true,

    // 캡션 상세 설정
    val captionColor: Int = 0xFF000000.toInt(),
    val captionHaloColor: Int = 0xFFFFFFFF.toInt(),
    val captionTextSize: Float = 12f,
    val captionMinZoom: Double = 0.0,
    val captionMaxZoom: Double = 21.0,
    val captionRequestedWidth: Float = 0f,
    val captionOffset: Float = 0f,
    val captionPerspectiveEnabled: Boolean = false,

    // 서브 캡션 상세 설정
    val subCaptionColor: Int = 0xFF000000.toInt(),
    val subCaptionHaloColor: Int = 0xFFFFFFFF.toInt(),
    val subCaptionTextSize: Float = 10f,
    val subCaptionMinZoom: Double = 0.0,
    val subCaptionMaxZoom: Double = 21.0,
    val subCaptionRequestedWidth: Float = 0f,

    // 중첩 관리
    val isHideCollidedMarkers: Boolean = false,
    val isHideCollidedSymbols: Boolean = false,
    val isHideCollidedCaptions: Boolean = false,

    // 효과
    val isIconPerspectiveEnabled: Boolean = false,
    val iconTintColor: Int = 0x00000000,

    val tag: Any? = null,
)
