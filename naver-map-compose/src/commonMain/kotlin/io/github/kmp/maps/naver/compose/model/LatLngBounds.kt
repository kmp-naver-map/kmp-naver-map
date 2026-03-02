package io.github.kmp.maps.naver.compose.model

// model/LatLngBounds.kt
data class LatLngBounds(
    val southwest: LatLng,
    val northeast: LatLng
) {
    val center: LatLng
        get() = LatLng(
            (southwest.latitude + northeast.latitude) / 2,
            (southwest.longitude + northeast.longitude) / 2
        )

    fun contains(latLng: LatLng): Boolean {
        return latLng.latitude in southwest.latitude..northeast.latitude &&
                latLng.longitude in southwest.longitude..northeast.longitude
    }

    companion object {
        fun from(vararg points: LatLng): LatLngBounds {
            require(points.isNotEmpty()) { "points must not be empty" }
            return LatLngBounds(
                southwest = LatLng(
                    latitude = points.minOf { it.latitude },
                    longitude = points.minOf { it.longitude }
                ),
                northeast = LatLng(
                    latitude = points.maxOf { it.latitude },
                    longitude = points.maxOf { it.longitude }
                )
            )
        }
    }
}