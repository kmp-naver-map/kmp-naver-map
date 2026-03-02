package io.github.kmp.maps.naver.compose.options

import androidx.compose.runtime.Immutable
import io.github.kmp.maps.naver.compose.model.LatLng

/**
 * 정보 창(InfoWindow)의 설정을 정의하는 데이터 클래스입니다.
 */
@Immutable
data class InfoWindowOptions(
    val position: LatLng = LatLng(0.0, 0.0),
    val alpha: Float = 1f,
    val zIndex: Int = 400000,
    val anchor: Pair<Float, Float> = Pair(0.5f, 1f),
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val text: String = "",
    val textColor: Int = 0xFF000000.toInt(),
    val textSize: Float = 14f,
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val cornerRadiusDp: Float = 0f,
    val isVisible: Boolean = true,
    val tag: Any? = null,
)
