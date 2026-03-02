package io.github.kmp.maps.naver.compose.model

data class CameraPosition(
    val target: LatLng,
    val zoom: Double,
    val tilt: Double = 0.0,
    val bearing: Double = 0.0
) {
    companion object {
        val DEFAULT = CameraPosition(
            target = LatLng(37.5666102, 126.9783881), // 서울 시청
            zoom = 14.0
        )
    }
}