package io.github.kmp.maps.naver.compose.model

data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        val INVALID = LatLng(0.0, 0.0)
    }
    
    operator fun plus(other: LatLng) = LatLng(
        latitude + other.latitude,
        longitude + other.longitude
    )
}