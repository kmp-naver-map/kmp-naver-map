@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package io.github.kmp.maps.naver.compose.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import cocoapods.NMapsMap.NMFOverlayImage
import cocoapods.NMapsMap.NMF_MARKER_IMAGE_DEFAULT
import io.github.kmp.maps.naver.compose.internal.toUIImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import platform.UIKit.UIImage

/**
 * iOS용 OverlayImage 구현체입니다.
 */
actual class OverlayImage internal constructor(internal val nativeImage: NMFOverlayImage) {
    actual companion object {
        actual val DEFAULT: OverlayImage = OverlayImage(NMF_MARKER_IMAGE_DEFAULT!!)

        /**
         * Asset에 포함된 이미지 파일로부터 OverlayImage 객체를 생성합니다.
         */
        actual fun fromAsset(assetName: String): OverlayImage {
            val image = UIImage.imageNamed(assetName) ?: return DEFAULT
            return OverlayImage(NMFOverlayImage.overlayImageWithImage(image))
        }

        /**
         * 지정된 경로의 파일로부터 OverlayImage 객체를 생성합니다.
         */
        actual fun fromPath(absolutePath: String): OverlayImage {
            val image = UIImage.imageWithContentsOfFile(absolutePath) ?: return DEFAULT
            return OverlayImage(NMFOverlayImage.overlayImageWithImage(image))
        }

        /**
         * UIImage로부터 OverlayImage 객체를 생성합니다. (iOS 전용)
         */
        fun fromImage(image: UIImage): OverlayImage =
            OverlayImage(NMFOverlayImage.overlayImageWithImage(image))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OverlayImage) return false
        return nativeImage == other.nativeImage
    }

    override fun hashCode(): Int = nativeImage.hashCode()
}

@Composable
actual fun rememberOverlayImage(
    resource: DrawableResource
): OverlayImage? {
    val painter = painterResource(resource)
    val density = LocalDensity.current
    
    return remember(painter, density) {
        val size = painter.intrinsicSize
        if (size.isSpecified && size.width > 0f && size.height > 0f) {
            val width = size.width.toInt()
            val height = size.height.toInt()
            
            // ImageBitmap 생성
            val imageBitmap = ImageBitmap(width, height)
            val composeCanvas = Canvas(imageBitmap)
            
            // Painter 그리기
            CanvasDrawScope().draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = composeCanvas,
                size = size
            ) {
                with(painter) {
                    draw(size)
                }
            }
            
            // ImageBitmap -> UIImage 변환
            val uiImage = imageBitmap.toUIImage()
            
            if (uiImage != null) {
                OverlayImage(NMFOverlayImage.overlayImageWithImage(uiImage))
            } else {
                OverlayImage.DEFAULT
            }
        } else {
            OverlayImage.DEFAULT
        }
    }
}
