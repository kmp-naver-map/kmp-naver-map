package io.github.kmp.maps.naver.compose.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.DrawableResource

/**
 * 지도의 오버레이(마커 등)에 사용될 이미지를 나타내는 클래스입니다.
 */
expect class OverlayImage {
    companion object {
        /**
         * 기본 마커 아이콘을 반환합니다.
         */
        val DEFAULT: OverlayImage

        /**
         * Asset에 포함된 이미지 파일로부터 OverlayImage 객체를 생성합니다.
         */
        fun fromAsset(assetName: String): OverlayImage

        /**
         * 지정된 경로의 파일로부터 OverlayImage 객체를 생성합니다.
         */
        fun fromPath(absolutePath: String): OverlayImage
    }
}

/**
 * [DrawableResource]에서 [OverlayImage]를 로드하는 유틸리티 컴포저블입니다.
 */
@Composable
expect fun rememberOverlayImage(
    resource: DrawableResource
): OverlayImage?

/**
 * 외부 URL에서 이미지를 다운로드하여 [OverlayImage]를 반환하는 플랫폼별 suspend 함수입니다.
 */
expect suspend fun downloadOverlayImageFromUrl(url: String): OverlayImage?

/**
 * 외부 이미지 URL로부터 [OverlayImage]를 비동기로 로드하는 유틸리티 컴포저블입니다.
 * 로딩 중에는 null을 반환합니다.
 */
@Composable
fun rememberOverlayImageFromUrl(url: String): OverlayImage? {
    var image by remember(url) { mutableStateOf<OverlayImage?>(null) }
    LaunchedEffect(url) {
        image = downloadOverlayImageFromUrl(url)
    }
    return image
}

/**
 * URL 이미지 없이 teardrop 형태만 동기적으로 그려 [OverlayImage]를 반환합니다.
 * URL 로딩 전 placeholder로 즉시 표시하기 위해 사용됩니다.
 */
expect fun createWhiteRoundOverlayImage(
    sizePx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    shadowColor: Int,
    tailHeightPx: Int,
    backgroundColor: Int,
): OverlayImage?

/**
 * 외부 URL 이미지를 원형(+ 선택적 꼬리) 배경 안에 합성하여 [OverlayImage]를 반환하는 플랫폼별 suspend 함수입니다.
 */
expect suspend fun downloadRoundOverlayImageFromUrl(
    url: String,
    sizePx: Int,
    borderWidthPx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    shadowColor: Int,
    tailHeightPx: Int,
    backgroundColor: Int,
): OverlayImage?

/**
 * 경로(PathOverlay) 패턴용 진행 방향 화살표 [OverlayImage]를 생성합니다.
 * 위쪽(진행 방향)을 가리키는 화살표를 그리며, 지도 SDK가 경로 방향에 맞춰 회전시킵니다.
 *
 * @param widthDp       화살표 너비 (dp)
 * @param heightDp      화살표 높이 (dp)
 * @param color         화살표 색상 ARGB (기본 흰색)
 * @param strokeWidthDp 0이면 꽉 찬 chevron 삼각형, 0보다 크면 해당 굵기(dp)의
 *                      열린 꺾쇠(˄) 스트로크로 그립니다.
 */
expect fun createDirectionArrowOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int = 0xFFFFFFFF.toInt(),
    strokeWidthDp: Float = 0f,
): OverlayImage?

/**
 * 경로(PathOverlay) 패턴용 발자국 [OverlayImage]를 생성합니다.
 * 발바닥(타원) + 발가락(원) 한 발이 진행 방향(위쪽)을 향하는 모양으로,
 * 도보 경로를 직관적으로 표현할 때 사용합니다.
 * 지도 SDK가 경로 방향에 맞춰 자동으로 회전시킵니다.
 *
 * @param widthDp  이미지 너비 (dp)
 * @param heightDp 이미지 높이 (dp)
 * @param color    발자국 색상 ARGB (기본 흰색)
 */
expect fun createFootprintOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int = 0xFFFFFFFF.toInt(),
): OverlayImage?

/**
 * teardrop 마커의 꼬리 끝이 지도 좌표를 정확히 가리키도록
 * shadow 패딩을 반영한 [Anchor] 값을 반환합니다.
 *
 * [buildTearDropBitmap] / [drawTearDropUIImage] 는 shadow 블러가 잘리지 않도록
 * 이미지 전체에 `shadowExtra` 여백을 추가합니다. 이 때문에 이미지 하단이
 * 실제 꼬리 끝보다 `shadowExtra`px 만큼 더 아래에 위치하게 되어,
 * 기본 Anchor.CenterBottom(0.5, 1.0) 을 사용하면 마커가 좌표보다 위로 올라가 보입니다.
 *
 * 이 함수는 꼬리 끝 y좌표 / 전체 이미지 높이로 정확한 anchor y를 계산합니다.
 *
 * @param sizePx          원형 영역 픽셀 크기 ([rememberRoundOverlayImageFromUrl]의 sizePx)
 * @param tailHeightPx    꼬리 높이 ([rememberRoundOverlayImageFromUrl]의 tailHeightPx)
 * @param shadowRadiusPx  그림자 블러 반경 (0 이면 그림자 없음)
 * @param shadowDx        그림자 X 오프셋
 * @param shadowDy        그림자 Y 오프셋
 */
fun tearDropAnchor(
    sizePx: Int,
    tailHeightPx: Int = 20,
    shadowRadiusPx: Float = 8f,
    shadowDx: Float = 0f,
    shadowDy: Float = 4f,
): io.github.kmp.maps.naver.compose.model.Anchor {
    val metrics = TearDropMetrics(sizePx, shadowRadiusPx, shadowDx, shadowDy, tailHeightPx)
    return io.github.kmp.maps.naver.compose.model.Anchor(x = 0.5f, y = metrics.anchorY)
}

/**
 * 외부 이미지 URL을 원형 배경 위에 합성하여 마커 아이콘으로 로드합니다.
 *
 * **2단계 렌더링:**
 * 1. 즉시 → teardrop placeholder 표시 (기본 파란 마커 없음)
 * 2. URL 로드 완료 → 이미지가 합성된 마커로 교체
 *
 * @param url 이미지 URL. null이면 placeholder만 표시합니다.
 * @param sizePx 원형 영역의 픽셀 크기 (기본 120px)
 * @param borderWidthPx 이미지와 원 테두리 사이의 여백 (기본 10px)
 * @param shadowRadiusPx 그림자 블러 반경 (기본 8px, 0 = 그림자 없음)
 * @param shadowDx 그림자 X 오프셋 (기본 0px)
 * @param shadowDy 그림자 Y 오프셋 (기본 4px, 양수 = 아래 방향)
 * @param shadowColor 그림자 색상 ARGB (기본 0x40000000 = 반투명 검정)
 * @param tailHeightPx 원 아래로 튀어나오는 꼬리 높이 (기본 20px, 0 = 꼬리 없음)
 * @param backgroundColor teardrop 배경 색상 ARGB (기본 0xFFFFFFFF = 흰색)
 * @param onError 이미지 로드 실패 시 호출되는 콜백. Signed URL 만료 시 URL 갱신 용도로 활용 가능.
 */
@Composable
fun rememberRoundOverlayImageFromUrl(
    url: String?,
    sizePx: Int = 120,
    borderWidthPx: Int = 10,
    shadowRadiusPx: Float = 8f,
    shadowDx: Float = 0f,
    shadowDy: Float = 4f,
    shadowColor: Int = 0x40000000,
    tailHeightPx: Int = 20,
    backgroundColor: Int = 0xFFFFFFFF.toInt(),
    onError: (() -> Unit)? = null,
): OverlayImage? {
    // Phase 1: teardrop을 동기적으로 즉시 생성.
    // PlaceholderCache를 통해 동일 스타일의 마커들이 같은 인스턴스를 공유합니다.
    val placeholder = remember(sizePx, shadowRadiusPx, shadowDx, shadowDy, shadowColor, tailHeightPx, backgroundColor) {
        val key = "ph:$sizePx:$shadowRadiusPx:$shadowDx:$shadowDy:$shadowColor:$tailHeightPx:$backgroundColor"
        PlaceholderCache.getOrCreate(key) {
            createWhiteRoundOverlayImage(sizePx, shadowRadiusPx, shadowDx, shadowDy, shadowColor, tailHeightPx, backgroundColor)
        }
    }

    // Phase 2: URL이 바뀌면 placeholder로 초기화 → 비동기로 이미지 로드 후 교체
    // url이 null이면 placeholder를 그대로 유지
    var image by remember(url, sizePx, borderWidthPx, shadowRadiusPx, shadowDx, shadowDy, shadowColor, tailHeightPx, backgroundColor) {
        mutableStateOf<OverlayImage?>(placeholder)
    }
    LaunchedEffect(url, sizePx, borderWidthPx, shadowRadiusPx, shadowDx, shadowDy, shadowColor, tailHeightPx, backgroundColor) {
        if (url != null) {
            val result = downloadRoundOverlayImageFromUrl(url, sizePx, borderWidthPx, shadowRadiusPx, shadowDx, shadowDy, shadowColor, tailHeightPx, backgroundColor)
            if (result != null) {
                image = result
            } else {
                // 실패 시 placeholder 유지 (null 할당 방지) 후 콜백 전달
                // Signed URL 만료 등 네트워크 오류 시 URL 갱신 기회 제공
                onError?.invoke()
            }
        }
    }
    return image
}
