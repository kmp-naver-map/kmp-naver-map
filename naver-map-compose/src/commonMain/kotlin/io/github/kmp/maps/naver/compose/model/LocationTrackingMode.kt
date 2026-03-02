package io.github.kmp.maps.naver.compose.model

/**
 * 위치 추적 모드를 정의하는 열거형입니다.
 */
enum class LocationTrackingMode {
    /**
     * 위치 추적을 사용하지 않습니다.
     */
    None,

    /**
     * 위치 추적을 사용하지만, 카메라는 사용자를 따라가지 않습니다.
     */
    NoFollow,

    /**
     * 위치 추적을 사용하며, 카메라가 사용자를 따라갑니다.
     */
    Follow,

    /**
     * 위치 추적을 사용하며, 카메라가 사용자의 방향에 맞춰 회전합니다.
     */
    Face
}
