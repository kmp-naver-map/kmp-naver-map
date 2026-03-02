package io.github.kmp.maps.naver.compose.overlay

import androidx.compose.runtime.Composable
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
