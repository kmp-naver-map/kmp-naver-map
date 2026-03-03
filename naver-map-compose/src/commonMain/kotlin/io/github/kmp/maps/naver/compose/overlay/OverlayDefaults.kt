package io.github.kmp.maps.naver.compose.overlay

/**
 * 네이버 지도 Compose 라이브러리에서 사용되는 오버레이 기본 상수를 정의합니다.
 *
 * Default constants used across overlay types in the Naver Map Compose library.
 * These correspond to the Naver Map SDK's internal default values.
 */
object OverlayDefaults {

    // ── Z-Index ─────────────────────────────────────────────
    /** 마커의 기본 글로벌 Z-index (NMF_MARKER_GLOBAL_Z_INDEX). */
    const val MARKER_GLOBAL_Z_INDEX: Int = 200_000

    /** 위치 오버레이의 기본 글로벌 Z-index. */
    const val LOCATION_OVERLAY_GLOBAL_Z_INDEX: Int = 300_000

    /** 정보 창의 기본 글로벌 Z-index. */
    const val INFO_WINDOW_GLOBAL_Z_INDEX: Int = 400_000

    // ── Zoom ────────────────────────────────────────────────
    /** 네이버 지도 SDK가 지원하는 최소 줌 레벨. */
    const val MIN_ZOOM: Double = 0.0

    /** 네이버 지도 SDK가 지원하는 최대 줌 레벨. */
    const val MAX_ZOOM: Double = 21.0

    // ── Colors (ARGB Int) ───────────────────────────────────
    /** 불투명 검정: `0xFF000000` */
    const val COLOR_BLACK: Int = 0xFF000000.toInt()

    /** 불투명 흰색: `0xFFFFFFFF` */
    const val COLOR_WHITE: Int = 0xFFFFFFFF.toInt()

    /** 완전 투명: `0x00000000` */
    const val COLOR_TRANSPARENT: Int = 0x00000000

    /** 기본 반투명 파란색 채우기: `0x7F0000FF` */
    const val DEFAULT_FILL_COLOR: Int = 0x7F0000FF

    /** 기본 불투명 파란색 선 색상: `0xFF0000FF` */
    const val DEFAULT_LINE_COLOR: Int = 0xFF0000FF.toInt()

    /** 기본 회색 (지나온 경로): `0xFF888888` */
    const val DEFAULT_PASSED_COLOR: Int = 0xFF888888.toInt()

    /** 기본 어두운 회색 (지나온 경로 테두리): `0xFF444444` */
    const val DEFAULT_PASSED_OUTLINE_COLOR: Int = 0xFF444444.toInt()
}
