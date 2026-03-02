package io.github.kmp.maps.naver.compose.options

import androidx.compose.runtime.Immutable

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
    // 레이어 그룹 설정
    val isBuildingLayerGroupEnabled: Boolean = true,
    val isTransitLayerGroupEnabled: Boolean = false,
    val isBicycleLayerGroupEnabled: Boolean = false,
    val isTrafficLayerGroupEnabled: Boolean = false,
    val isMountainLayerGroupEnabled: Boolean = false,
    val isCadastralLayerGroupEnabled: Boolean = false,
)
