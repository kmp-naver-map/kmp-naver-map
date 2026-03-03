package io.github.kmp.maps.naver.compose.model

import androidx.compose.runtime.Immutable

/**
 * 지도 위에 표시되는 심볼(마커 캡션 + 좌표)을 나타내는 모델입니다.
 *
 * Represents a symbol on the map, consisting of a caption and a geographic position.
 */
@Immutable
data class Symbol(
    val caption: String,
    val position: LatLng
)
