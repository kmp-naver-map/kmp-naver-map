package io.github.kmp.maps.naver.compose.options

/**
 * 네이버 지도 로고의 위치를 지정합니다.
 *
 * 네이버 지도 사용 시 로고는 반드시 노출되어야 하며, 의도적으로 가려서는 안 됩니다.
 * 단, 서비스 UI와 겹치지 않도록 위치와 마진을 조정할 수 있습니다.
 */
enum class LogoAlign {
    /** 왼쪽 하단 (기본값) */
    LeftBottom,

    /** 오른쪽 하단 */
    RightBottom,

    /** 왼쪽 상단 */
    LeftTop,

    /** 오른쪽 상단 */
    RightTop,
}
