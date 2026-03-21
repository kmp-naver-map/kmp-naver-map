package io.github.kmp.maps.naver.compose.overlay

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import com.naver.maps.map.overlay.OverlayImage as NativeOverlayImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.core.graphics.createBitmap

/**
 * Android용 OverlayImage 실제 구현체입니다.
 */
actual class OverlayImage internal constructor(internal val nativeImage: NativeOverlayImage) {
    actual companion object {
        actual val DEFAULT: OverlayImage = OverlayImage(com.naver.maps.map.overlay.Marker.DEFAULT_ICON)

        /**
         * Asset에 포함된 이미지 파일로부터 OverlayImage 객체를 생성합니다.
         */
        actual fun fromAsset(assetName: String): OverlayImage =
            OverlayImage(NativeOverlayImage.fromAsset(assetName))

        /**
         * 지정된 경로의 파일로부터 OverlayImage 객체를 생성합니다.
         */
        actual fun fromPath(absolutePath: String): OverlayImage =
            OverlayImage(NativeOverlayImage.fromPath(absolutePath))

        /**
         * Android drawable 리소스 ID로부터 OverlayImage 객체를 생성합니다. (Android 전용)
         */
        fun fromResource(@androidx.annotation.DrawableRes resId: Int): OverlayImage =
            OverlayImage(NativeOverlayImage.fromResource(resId))

        /**
         * Bitmap으로부터 OverlayImage 객체를 생성합니다. (Android 전용)
         */
        fun fromBitmap(bitmap: Bitmap): OverlayImage =
            OverlayImage(NativeOverlayImage.fromBitmap(bitmap))
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
            val bitmap = createBitmap(width, height)
            val canvas = android.graphics.Canvas(bitmap)
            
            CanvasDrawScope().draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = Canvas(canvas),
                size = size
            ) {
                with(painter) {
                    draw(size)
                }
            }
            OverlayImage(NativeOverlayImage.fromBitmap(bitmap))
        } else {
            OverlayImage.DEFAULT
        }
    }
}
