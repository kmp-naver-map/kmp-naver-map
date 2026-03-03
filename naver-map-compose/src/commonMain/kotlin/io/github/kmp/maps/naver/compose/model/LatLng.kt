package io.github.kmp.maps.naver.compose.model

/**
 * 위도(latitude)와 경도(longitude)를 나타내는 좌표 모델입니다.
 *
 * Represents a geographic coordinate with latitude and longitude.
 */
data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        /**
         * 초기화되지 않은 위치를 나타내는 센티널 값입니다.
         * 참고: (0.0, 0.0)은 기니만의 유효한 지리 좌표입니다.
         * 이 값은 "값이 설정되지 않음"을 내부적으로 나타내는 데만 사용됩니다.
         *
         * Sentinel value representing an unset/uninitialized position.
         */
        val UNSET = LatLng(0.0, 0.0)

        @Deprecated("Use UNSET instead. INVALID is misleading as (0,0) is a valid coordinate.", ReplaceWith("UNSET"))
        val INVALID = UNSET
    }

    operator fun plus(other: LatLng) = LatLng(
        latitude + other.latitude,
        longitude + other.longitude
    )
}