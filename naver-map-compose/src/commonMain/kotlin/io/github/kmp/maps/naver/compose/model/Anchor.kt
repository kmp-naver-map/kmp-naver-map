package io.github.kmp.maps.naver.compose.model

/**
 * 오버레이의 앵커 포인트를 정규화된 좌표로 나타냅니다.
 * `(0, 0)`은 좌상단, `(1, 1)`은 우하단입니다.
 *
 * Represents an anchor point for overlays as normalised coordinates.
 * `(0, 0)` is the top-left corner and `(1, 1)` is the bottom-right corner.
 *
 * @property x 수평 앵커 [0, 1]. 0.5는 수평 중앙.
 * @property y 수직 앵커 [0, 1]. 1.0은 하단 정렬.
 */
data class Anchor(
    val x: Float,
    val y: Float
) {
    companion object {
        /** 하단 중앙 앵커. 마커의 기본값. */
        val CenterBottom = Anchor(0.5f, 1f)

        /** 정중앙 앵커. 위치 오버레이의 기본값. */
        val Center = Anchor(0.5f, 0.5f)
    }
}

/** [Anchor]를 [Pair]로 변환합니다. */
fun Anchor.toPair(): Pair<Float, Float> = Pair(x, y)

/** [Pair]를 [Anchor]로 변환합니다. */
fun Pair<Float, Float>.toAnchor(): Anchor = Anchor(first, second)
