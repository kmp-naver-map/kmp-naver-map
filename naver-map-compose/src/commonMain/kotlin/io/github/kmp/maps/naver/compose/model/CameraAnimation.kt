package io.github.kmp.maps.naver.compose.model

/**
 * 카메라 이동 시 사용할 애니메이션 유형을 정의하는 열거형입니다.
 *
 * Defines the animation type used for camera transitions.
 */
enum class CameraAnimation {
    /** 감속 곡선을 사용하는 애니메이션. / Easing animation with deceleration curve. */
    Easing,

    /** 줌 아웃 후 줌 인하며 이동하는 플라이 애니메이션. / Fly animation that zooms out, moves, then zooms in. */
    Fly,

    /** 일정 속도로 이동하는 선형 애니메이션. / Linear animation moving at constant speed. */
    Linear,

    /** 애니메이션 없이 즉시 이동. / No animation; camera moves instantly. */
    None
}