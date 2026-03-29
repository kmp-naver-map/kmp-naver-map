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
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import platform.CoreGraphics.CGContextAddEllipseInRect
import platform.CoreGraphics.CGContextBeginTransparencyLayer
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGContextEndTransparencyLayer
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextSetShadowWithColor
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithURL
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import kotlin.coroutines.resume
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * iOS용 OverlayImage 구현체입니다.
 */
actual class OverlayImage internal constructor(internal val nativeImage: NMFOverlayImage) {
    actual companion object {
        actual val DEFAULT: OverlayImage = OverlayImage(NMF_MARKER_IMAGE_DEFAULT)

        actual fun fromAsset(assetName: String): OverlayImage {
            val image = UIImage.imageNamed(assetName) ?: return DEFAULT
            return OverlayImage(NMFOverlayImage.overlayImageWithImage(image))
        }

        actual fun fromPath(absolutePath: String): OverlayImage {
            val image = UIImage.imageWithContentsOfFile(absolutePath) ?: return DEFAULT
            return OverlayImage(NMFOverlayImage.overlayImageWithImage(image))
        }

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

// ─────────────────────────────────────────────────────────────────────────────
// 내부 헬퍼: teardrop UIImage 생성
//
// srcImage 가 null 이면 흰색 teardrop 만 그림 (placeholder 용).
// srcImage 가 있으면 borderWidthPx 여백을 두고 원 안에 이미지를 합성.
// ─────────────────────────────────────────────────────────────────────────────
private fun drawTearDropUIImage(
    sizePx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    shadowColor: Int,
    tailHeightPx: Int,
    srcImage: UIImage?,
    borderWidthPx: Int,
): UIImage? {
    val hasShadow = shadowRadiusPx > 0f
    val hasTail = tailHeightPx > 0

    val shadowExtra = if (hasShadow) {
        (shadowRadiusPx + kotlin.math.abs(shadowDx) + kotlin.math.abs(shadowDy)).toInt() + 2
    } else 0

    val totalWidth = (sizePx + (if (hasTail) tailHeightPx else 0) + shadowExtra * 2).toDouble()
    val totalHeight = (sizePx + (if (hasTail) tailHeightPx else 0) + shadowExtra * 2).toDouble()

    val cx = totalWidth / 2.0
    val cy = (shadowExtra + sizePx / 2).toDouble()
    val radius = sizePx / 2.0

    // ── 꼬리 베지어 파라미터 ────────────────────────────────────────────────
    val alphaDeg = 45.0
    val alphaRad = alphaDeg * PI / 180.0
    val sinA = sin(alphaRad)
    val cosA = cos(alphaRad)

    // 접점: 원 위의 lower-right / lower-left
    val jRx = cx + radius * sinA
    val jRy = cy + radius * cosA
    val jLx = cx - radius * sinA
    val jLy = cy + radius * cosA

    // 둥근 끝 캡
    val tipR = (tailHeightPx * 0.13).coerceIn(3.0, 12.0)
    val tipCenterY = cy + radius + tailHeightPx - tipR

    val t1 = tailHeightPx * 0.55  // 접선 방향 장력
    val t2 = tailHeightPx * 0.45  // 끝 진입 장력

    /**
     * UIBezierPath 기반 teardrop 경로.
     * UIKit 좌표계(Y↓, clockwise=true = 화면상 시계 방향):
     *   0 = 오른쪽, π/2 = 아래 → Android 공식과 동일하게 적용 가능.
     */
    fun buildBezierPath(ox: Double = 0.0, oy: Double = 0.0): UIBezierPath {
        val path = UIBezierPath()
        if (hasTail) {
            // ① 원호: lower-left → (상단) → lower-right (시계 방향)
            path.addArcWithCenter(
                CGPointMake(cx + ox, cy + oy),
                radius = radius,
                startAngle = PI / 2.0 + alphaRad,
                endAngle = PI / 2.0 - alphaRad,
                clockwise = true,
            )
            // ② 오른쪽 베지어: lower-right → 둥근 끝 오른쪽
            path.addCurveToPoint(
                CGPointMake(cx + ox + tipR, tipCenterY + oy),
                controlPoint1 = CGPointMake(jRx + ox - t1 * cosA, jRy + oy + t1 * sinA),
                controlPoint2 = CGPointMake(cx + ox + tipR, tipCenterY + oy - t2),
            )
            // ③ 둥근 끝 캡: 0 → π 시계 방향 (오른쪽 → 아래 → 왼쪽)
            path.addArcWithCenter(
                CGPointMake(cx + ox, tipCenterY + oy),
                radius = tipR,
                startAngle = 0.0,
                endAngle = PI,
                clockwise = true,
            )
            // ④ 왼쪽 베지어: 둥근 끝 왼쪽 → lower-left
            path.addCurveToPoint(
                CGPointMake(jLx + ox, jLy + oy),
                controlPoint1 = CGPointMake(cx + ox - tipR, tipCenterY + oy - t2),
                controlPoint2 = CGPointMake(jLx + ox + t1 * cosA, jLy + oy + t1 * sinA),
            )
        } else {
            path.addArcWithCenter(
                CGPointMake(cx + ox, cy + oy),
                radius = radius,
                startAngle = 0.0,
                endAngle = 2.0 * PI,
                clockwise = true,
            )
        }
        path.closePath()
        return path
    }

    UIGraphicsBeginImageContextWithOptions(CGSizeMake(totalWidth, totalHeight), false, 0.0)
    val context = UIGraphicsGetCurrentContext() ?: run {
        UIGraphicsEndImageContext()
        return null
    }

    // 1) 그림자 + 흰색 teardrop (투명 레이어로 shape 전체에 그림자 하나만 적용)
    CGContextSaveGState(context)
    if (hasShadow) {
        val a = ((shadowColor ushr 24) and 0xFF) / 255.0
        val r = ((shadowColor ushr 16) and 0xFF) / 255.0
        val g = ((shadowColor ushr 8) and 0xFF) / 255.0
        val b = (shadowColor and 0xFF) / 255.0
        CGContextSetShadowWithColor(
            context,
            CGSizeMake(shadowDx.toDouble(), shadowDy.toDouble()),
            shadowRadiusPx.toDouble(),
            UIColor(red = r, green = g, blue = b, alpha = a).CGColor,
        )
    }
    CGContextBeginTransparencyLayer(context, null)
    UIColor.whiteColor.setFill()
    buildBezierPath().fill()
    CGContextEndTransparencyLayer(context)
    CGContextRestoreGState(context)

    // 2) 원 안에 이미지 합성 (srcImage 가 있을 때만)
    if (srcImage != null) {
        val innerRadius = (radius - borderWidthPx).coerceAtLeast(1.0)
        val imageRect =
            CGRectMake(cx - innerRadius, cy - innerRadius, innerRadius * 2, innerRadius * 2)
        CGContextAddEllipseInRect(context, imageRect)
        CGContextClip(context)
        srcImage.drawInRect(imageRect)
    }

    val result = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return result
}

// ─────────────────────────────────────────────────────────────────────────────
// Public API
// ─────────────────────────────────────────────────────────────────────────────

/** URL 이미지 없이 흰색 teardrop 만 즉시(동기) 생성 → placeholder 용 */
actual fun createWhiteRoundOverlayImage(
    sizePx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    shadowColor: Int,
    tailHeightPx: Int,
): OverlayImage? {
    val uiImage = drawTearDropUIImage(
        sizePx = sizePx,
        shadowRadiusPx = shadowRadiusPx,
        shadowDx = shadowDx,
        shadowDy = shadowDy,
        shadowColor = shadowColor,
        tailHeightPx = tailHeightPx,
        srcImage = null,
        borderWidthPx = 0,
    ) ?: return null
    return OverlayImage(NMFOverlayImage.overlayImageWithImage(uiImage))
}

/** URL 이미지를 다운로드하여 teardrop 안에 합성 */
actual suspend fun downloadRoundOverlayImageFromUrl(
    url: String,
    sizePx: Int,
    borderWidthPx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    shadowColor: Int,
    tailHeightPx: Int,
): OverlayImage? {
    val cacheKey = "round:$url:$sizePx:$borderWidthPx:$shadowRadiusPx:$shadowDx:$shadowDy:$shadowColor:$tailHeightPx"
    return OverlayImageCache.getOrLoad(cacheKey) {
        suspendCancellableCoroutine { continuation ->
            val nsUrl = NSURL.URLWithString(url)
                ?: run { continuation.resume(null); return@suspendCancellableCoroutine }
            val task = NSURLSession.sharedSession.dataTaskWithURL(nsUrl) { data, response, error ->
                // NSURLSession.sharedSession 콜백은 직렬(serial) 큐에서 실행됨.
                // 데이터 검증만 여기서 수행하고, 무거운 비트맵 합성은 즉시 반환하여
                // 다음 콜백이 블로킹되지 않도록 GCD concurrent 큐로 위임.
                if (error != null || data == null) {
                    continuation.resume(null); return@dataTaskWithURL
                }
                // HTTP 4xx/5xx (Signed URL 만료 등) 감지: Android와 동일하게 null 반환
                val statusCode = (response as? NSHTTPURLResponse)?.statusCode?.toInt() ?: 200
                if (statusCode !in 200..299) {
                    continuation.resume(null); return@dataTaskWithURL
                }
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)!!) {
                    val srcImage = UIImage(data = data)
                    val uiImage = drawTearDropUIImage(
                        sizePx         = sizePx,
                        shadowRadiusPx = shadowRadiusPx,
                        shadowDx       = shadowDx,
                        shadowDy       = shadowDy,
                        shadowColor    = shadowColor,
                        tailHeightPx   = tailHeightPx,
                        srcImage       = srcImage,
                        borderWidthPx  = borderWidthPx,
                    )
                    continuation.resume(uiImage?.let { OverlayImage(NMFOverlayImage.overlayImageWithImage(it)) })
                }
            }
            task.resume()
            continuation.invokeOnCancellation { task.cancel() }
        }
    }
}

actual suspend fun downloadOverlayImageFromUrl(url: String): OverlayImage? {
    return OverlayImageCache.getOrLoad("plain:$url") {
        suspendCancellableCoroutine { continuation ->
            val nsUrl = NSURL.URLWithString(url)
            if (nsUrl == null) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }
            val task = NSURLSession.sharedSession.dataTaskWithURL(nsUrl) { data, response, error ->
                if (error != null || data == null) {
                    continuation.resume(null)
                    return@dataTaskWithURL
                }
                // HTTP 4xx/5xx (Signed URL 만료 등) 감지
                val statusCode = (response as? NSHTTPURLResponse)?.statusCode?.toInt() ?: 200
                if (statusCode !in 200..299) {
                    continuation.resume(null)
                    return@dataTaskWithURL
                }
                val image = UIImage(data = data) ?: run {
                    continuation.resume(null)
                    return@dataTaskWithURL
                }
                continuation.resume(OverlayImage(NMFOverlayImage.overlayImageWithImage(image)))
            }
            task.resume()
            continuation.invokeOnCancellation { task.cancel() }
        }
    }
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

            val imageBitmap = ImageBitmap(width, height)
            val composeCanvas = Canvas(imageBitmap)

            CanvasDrawScope().draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = composeCanvas,
                size = size,
            ) {
                with(painter) { draw(size) }
            }

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
