package io.github.kmp.maps.naver.compose.model

/**
 * 지도의 유형을 정의하는 열거형입니다.
 *
 * Defines the type of map to be displayed.
 */
enum class MapType {
    /** 일반 지도. / Basic map. */
    Basic,

    /** 내비게이션 지도. / Navigation map. */
    Navi,

    /** 위성 지도. / Satellite map. */
    Satellite,

    /** 위성 지도와 도로·지명 등의 정보를 합성한 하이브리드 지도. / Hybrid map combining satellite imagery with road and label overlays. */
    Hybrid,

    /** 지형도. / Terrain map. */
    Terrain,

    /** 지도 없음 (빈 배경). / No map (empty background). */
    None
}