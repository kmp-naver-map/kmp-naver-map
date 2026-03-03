package io.github.kmp.maps.naver.compose.model

/**
 * 위치 추적 모드를 정의하는 열거형입니다.
 *
 * Defines the location tracking mode for the map.
 */
enum class LocationTrackingMode {
    /**
     * 위치 추적을 사용하지 않습니다.
     *
     * No location tracking.
     */
    None,

    /**
     * 위치 추적을 사용하지만, 카메라는 사용자를 따라가지 않습니다.
     *
     * Tracks location but does not move the camera to follow the user.
     */
    NoFollow,

    /**
     * 위치 추적을 사용하며, 카메라가 사용자를 따라갑니다.
     *
     * Tracks location and moves the camera to follow the user.
     */
    Follow,

    /**
     * 위치 추적을 사용하며, 카메라가 사용자의 방향에 맞춰 회전합니다.
     *
     * Tracks location and rotates the camera to match the user's bearing.
     */
    Face
}
