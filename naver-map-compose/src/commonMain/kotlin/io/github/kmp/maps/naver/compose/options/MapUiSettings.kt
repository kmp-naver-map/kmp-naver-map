package io.github.kmp.maps.naver.compose.options

import androidx.compose.runtime.Immutable

/**
 * 지도 UI 요소의 표시 여부를 설정합니다.
 *
 * Controls the visibility of map UI elements such as zoom controls, compass, and scale bar.
 */
@Immutable
data class MapUiSettings(
    val isScrollGesturesEnabled: Boolean = true,
    val isZoomGesturesEnabled: Boolean = true,
    val isTiltGesturesEnabled: Boolean = true,
    val isRotateGesturesEnabled: Boolean = true,
    val isStopGesturesEnabled: Boolean = true,
    val isCompassEnabled: Boolean = true,
    val isScaleBarEnabled: Boolean = true,
    val isZoomControlEnabled: Boolean = true,
    val isIndoorLevelPickerEnabled: Boolean = false,
    val isLocationButtonEnabled: Boolean = false,
    val isLogoClickEnabled: Boolean = true,
    /**
     * 로고 위치. 기본값은 [LogoAlign.LeftBottom].
     * 로고는 반드시 노출되어야 하며 의도적으로 가릴 수 없습니다.
     */
    val logoAlign: LogoAlign = LogoAlign.LeftBottom,
    /** 로고 왼쪽 마진 (dp) */
    val logoMarginLeft: Int = 0,
    /** 로고 상단 마진 (dp) */
    val logoMarginTop: Int = 0,
    /** 로고 오른쪽 마진 (dp) */
    val logoMarginRight: Int = 0,
    /** 로고 하단 마진 (dp) */
    val logoMarginBottom: Int = 0,
    // 레이어 그룹 설정
    val isBuildingLayerGroupEnabled: Boolean = true,
    val isTransitLayerGroupEnabled: Boolean = false,
    val isBicycleLayerGroupEnabled: Boolean = false,
    val isTrafficLayerGroupEnabled: Boolean = false,
    val isMountainLayerGroupEnabled: Boolean = false,
    val isCadastralLayerGroupEnabled: Boolean = false,
)
