package io.github.kmp.maps.naver.compose.model

import androidx.compose.runtime.Immutable

@Immutable
data class Symbol(
    val caption: String,
    val position: LatLng
)
