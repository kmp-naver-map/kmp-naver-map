package io.github.kmp.maps.naver.compose.model

/**
 * 지도 카메라의 위치를 나타내는 모델입니다.
 *
 * Represents the camera position of the map, including target coordinates,
 * zoom level, tilt angle, and bearing.
 *
 * @property target 카메라가 바라보는 좌표.
 * @property zoom 줌 레벨 (0.0 ~ 21.0).
 * @property tilt 기울기 각도 (0.0 ~ 63.0).
 * @property bearing 방위각 (0.0 ~ 360.0).
 */
data class CameraPosition(
    val target: LatLng,
    val zoom: Double,
    val tilt: Double = 0.0,
    val bearing: Double = 0.0
) {
    companion object {
        /**
         * 서울시청(37.5666, 126.9784) 줌 14를 기본 카메라 위치로 사용합니다.
         *
         * Default camera position centered on Seoul City Hall at zoom level 14.
         */
        val DEFAULT = CameraPosition(
            target = LatLng(37.5666102, 126.9783881),
            zoom = 14.0
        )
    }
}