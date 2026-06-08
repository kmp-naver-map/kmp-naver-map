package io.github.kmp.maps.naver.compose.overlay

import kotlin.math.abs

/**
 * teardrop(물방울) 마커의 **레이아웃 기하**를 한 곳에서 계산하는 공통 클래스입니다.
 *
 * 동일한 sizing/anchor 수식이 이전에는 세 곳(Android 그리기, iOS 그리기, [tearDropAnchor])에
 * 중복되어 있었고, 한 쪽만 바뀌면 꼬리 끝 anchor가 어긋나 마커가 좌표를 잘못 가리키는
 * 버그로 이어졌습니다. 이 클래스가 단일 출처(single source of truth)가 됩니다.
 *
 * 베지어 꼬리 곡선의 삼각함수 계산은 플랫폼 그래픽 API(Android `Path`=Float,
 * iOS `UIBezierPath`=Double)에 강하게 묶여 있어 각 플랫폼 구현에 그대로 둡니다.
 *
 * 모든 값은 정수 기반이거나 정수에서 유도되므로, 플랫폼에서 `Float`/`Double`로 변환해도
 * 기존 동작과 픽셀 단위로 동일합니다.
 *
 * @param sizePx         원형 영역의 픽셀 크기
 * @param shadowRadiusPx 그림자 블러 반경 (0 이면 그림자 없음)
 * @param shadowDx       그림자 X 오프셋
 * @param shadowDy       그림자 Y 오프셋
 * @param tailHeightPx   꼬리 높이 (0 이면 꼬리 없음 → 단순 원형)
 */
internal class TearDropMetrics(
    sizePx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    tailHeightPx: Int,
) {
    /** 그림자 표시 여부. */
    val hasShadow: Boolean = shadowRadiusPx > 0f

    /** 꼬리 표시 여부. */
    val hasTail: Boolean = tailHeightPx > 0

    /** 그림자 블러가 잘리지 않도록 이미지 전체에 더하는 여백(px). */
    val shadowExtra: Int =
        if (hasShadow) (shadowRadiusPx + abs(shadowDx) + abs(shadowDy)).toInt() + 2 else 0

    /** 정사각형 비트맵 한 변의 픽셀 크기 (꼬리/그림자 여백 포함). */
    val totalSize: Int = sizePx + (if (hasTail) tailHeightPx else 0) + shadowExtra * 2

    /** 원 중심 x. */
    val cx: Double = totalSize / 2.0

    /** 원 중심 y (그림자 여백 + 반지름). `sizePx / 2`는 정수 나눗셈으로 기존 동작과 동일. */
    val cy: Double = (shadowExtra + sizePx / 2).toDouble()

    /** 원 반지름. */
    val radius: Double = sizePx / 2.0

    /** 꼬리 끝(tip)이 위치하는 y. anchor 계산 기준점. */
    val tipBottomY: Int = shadowExtra + sizePx + tailHeightPx

    /**
     * 이미지 전체 높이 대비 꼬리 끝 y 비율 = [Anchor]의 y.
     * 이 값으로 anchor를 잡아야 꼬리 끝이 지도 좌표를 정확히 가리킵니다.
     */
    val anchorY: Float = tipBottomY.toFloat() / totalSize.toFloat()
}
