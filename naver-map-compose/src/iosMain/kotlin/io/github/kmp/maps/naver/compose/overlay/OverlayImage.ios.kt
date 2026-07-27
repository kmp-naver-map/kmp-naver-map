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
import io.github.kmp.maps.naver.compose.internal.toUIColor
import io.github.kmp.maps.naver.compose.internal.toUIImage
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import platform.CoreGraphics.CGContextAddEllipseInRect
import platform.CoreGraphics.CGContextBeginTransparencyLayer
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGContextEndTransparencyLayer
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextRotateCTM
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextSetShadowWithColor
import platform.CoreGraphics.CGContextTranslateCTM
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
    backgroundColor: Int,
): UIImage? {
    val metrics = TearDropMetrics(sizePx, shadowRadiusPx, shadowDx, shadowDy, tailHeightPx)
    val hasShadow = metrics.hasShadow
    val hasTail = metrics.hasTail

    val totalWidth = metrics.totalSize.toDouble()
    val totalHeight = metrics.totalSize.toDouble()

    val cx = metrics.cx
    val cy = metrics.cy
    val radius = metrics.radius

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
    val bgA = ((backgroundColor ushr 24) and 0xFF) / 255.0
    val bgR = ((backgroundColor ushr 16) and 0xFF) / 255.0
    val bgG = ((backgroundColor ushr 8) and 0xFF) / 255.0
    val bgB = (backgroundColor and 0xFF) / 255.0
    UIColor(red = bgR, green = bgG, blue = bgB, alpha = bgA).setFill()
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

/** URL 이미지 없이 teardrop 만 즉시(동기) 생성 → placeholder 용 */
actual fun createWhiteRoundOverlayImage(
    sizePx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    shadowColor: Int,
    tailHeightPx: Int,
    backgroundColor: Int,
): OverlayImage? {
    val uiImage = drawTearDropUIImage(
        sizePx          = sizePx,
        shadowRadiusPx  = shadowRadiusPx,
        shadowDx        = shadowDx,
        shadowDy        = shadowDy,
        shadowColor     = shadowColor,
        tailHeightPx    = tailHeightPx,
        srcImage        = null,
        borderWidthPx   = 0,
        backgroundColor = backgroundColor,
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
    backgroundColor: Int,
): OverlayImage? {
    val cacheKey = "round:$url:$sizePx:$borderWidthPx:$shadowRadiusPx:$shadowDx:$shadowDy:$shadowColor:$tailHeightPx:$backgroundColor"
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
                        sizePx          = sizePx,
                        shadowRadiusPx  = shadowRadiusPx,
                        shadowDx        = shadowDx,
                        shadowDy        = shadowDy,
                        shadowColor     = shadowColor,
                        tailHeightPx    = tailHeightPx,
                        srcImage        = srcImage,
                        borderWidthPx   = borderWidthPx,
                        backgroundColor = backgroundColor,
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
                val image = UIImage.imageWithData(data) ?: run {
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

/**
 * 경로 패턴용 진행 방향 화살표를 그립니다.
 * strokeWidthDp가 0이면 밑변 중앙이 파인 꽉 찬 chevron 삼각형,
 * 0보다 크면 열린 꺾쇠(˄) 스트로크로 그립니다.
 * dp를 포인트로 그대로 사용합니다(iOS 1pt = 1dp).
 */
actual fun createDirectionArrowOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int,
    strokeWidthDp: Float,
): OverlayImage? {
    val w = widthDp.toDouble().coerceAtLeast(2.0)
    val h = heightDp.toDouble().coerceAtLeast(2.0)
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(w, h), false, 0.0)
    if (strokeWidthDp > 0f) {
        val stroke = strokeWidthDp.toDouble()
        val half = stroke / 2.0
        val path = UIBezierPath.bezierPath()
        path.moveToPoint(CGPointMake(half, h - half))
        path.addLineToPoint(CGPointMake(w / 2.0, half))      // 꼭짓점 (진행 방향)
        path.addLineToPoint(CGPointMake(w - half, h - half))
        path.lineWidth = stroke
        path.lineCapStyle = platform.CoreGraphics.CGLineCap.kCGLineCapRound
        path.lineJoinStyle = platform.CoreGraphics.CGLineJoin.kCGLineJoinRound
        color.toUIColor().setStroke()
        path.stroke()
    } else {
        val path = UIBezierPath.bezierPath()
        path.moveToPoint(CGPointMake(w / 2.0, 0.0))          // 꼭짓점 (진행 방향)
        path.addLineToPoint(CGPointMake(w, h))
        path.addLineToPoint(CGPointMake(w / 2.0, h * 0.72))  // 밑변 중앙 파임 (chevron)
        path.addLineToPoint(CGPointMake(0.0, h))
        path.closePath()
        color.toUIColor().setFill()
        path.fill()
    }
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return image?.let { OverlayImage(NMFOverlayImage.overlayImageWithImage(it)) }
}

/**
 * 경로 패턴용 사람 발자국을 그립니다.
 * 신발 자국 두 개를 가로 중앙에 놓고 기울기만 ±8°로 번갈아 주어,
 * 경로선을 얇게 유지하면서도 한 발씩 내딛는 걸음 리듬을 표현합니다.
 * dp를 포인트로 그대로 사용합니다(iOS 1pt = 1dp).
 */
actual fun createFootprintOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int,
): OverlayImage? {
    val w = widthDp.toDouble().coerceAtLeast(4.0)
    val h = heightDp.toDouble().coerceAtLeast(4.0)
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(w, h), false, 0.0)
    val ctx = UIGraphicsGetCurrentContext()
    color.toUIColor().setFill()
    // 신발 자국 한 짝(앞창 + 뒤굽 분리)을 cy 높이에 degrees만큼 기울여 그린다.
    // 크기는 이미지 "너비"에서만 정해지므로 heightDp는 걸음 간격만 조절한다.
    fun shoe(cy: Double, degrees: Double) {
        var shoeW = w * 0.737            // 8° 기울여도 좌우가 잘리지 않는 최대 폭
        var shoeH = shoeW * 2.63         // 실제 구두 비율(가로:세로 ≈ 0.38:1)
        // heightDp가 권장 최소치보다 작으면 자국이 이미지 위아래로 잘린다.
        // 잘리는 대신 자국을 줄여 형태를 온전히 유지한다.
        val halfBound = (shoeH * COS8 + shoeW * SIN8) / 2.0
        val slot = h * PATTERN_FOOT_OFFSET.toDouble()
        if (halfBound > slot) {
            val fit = slot / halfBound
            shoeW *= fit
            shoeH *= fit
        }
        CGContextSaveGState(ctx)
        CGContextTranslateCTM(ctx, w / 2.0, cy)
        CGContextRotateCTM(ctx, degrees * kotlin.math.PI / 180.0)
        CGContextTranslateCTM(ctx, -shoeW / 2.0, -shoeH / 2.0)
        // 앞창: 신발 폭을 꽉 채우는 세로로 긴 타원 (전체 길이의 60%까지)
        val front = UIBezierPath.bezierPath()
        front.moveToPoint(CGPointMake(shoeW * 0.50, shoeH * 0.01))
        front.addCurveToPoint(
            CGPointMake(shoeW * 1.00, shoeH * 0.30),
            controlPoint1 = CGPointMake(shoeW * 0.84, shoeH * 0.01),
            controlPoint2 = CGPointMake(shoeW * 1.00, shoeH * 0.13),
        )
        front.addCurveToPoint(
            CGPointMake(shoeW * 0.70, shoeH * 0.60),
            controlPoint1 = CGPointMake(shoeW * 1.00, shoeH * 0.46),
            controlPoint2 = CGPointMake(shoeW * 0.88, shoeH * 0.58),
        )
        front.addCurveToPoint(
            CGPointMake(shoeW * 0.30, shoeH * 0.60),
            controlPoint1 = CGPointMake(shoeW * 0.58, shoeH * 0.615),
            controlPoint2 = CGPointMake(shoeW * 0.42, shoeH * 0.615),
        )
        front.addCurveToPoint(
            CGPointMake(shoeW * 0.00, shoeH * 0.30),
            controlPoint1 = CGPointMake(shoeW * 0.12, shoeH * 0.58),
            controlPoint2 = CGPointMake(shoeW * 0.00, shoeH * 0.46),
        )
        front.addCurveToPoint(
            CGPointMake(shoeW * 0.50, shoeH * 0.01),
            controlPoint1 = CGPointMake(shoeW * 0.00, shoeH * 0.13),
            controlPoint2 = CGPointMake(shoeW * 0.16, shoeH * 0.01),
        )
        front.closePath()
        front.fill()
        // 뒤굽: 앞창과 떨어진 둥근 사각형
        val heel = UIBezierPath.bezierPath()
        heel.moveToPoint(CGPointMake(shoeW * 0.50, shoeH * 0.71))
        heel.addCurveToPoint(
            CGPointMake(shoeW * 0.85, shoeH * 0.87),
            controlPoint1 = CGPointMake(shoeW * 0.74, shoeH * 0.71),
            controlPoint2 = CGPointMake(shoeW * 0.86, shoeH * 0.79),
        )
        heel.addCurveToPoint(
            CGPointMake(shoeW * 0.50, shoeH * 1.00),
            controlPoint1 = CGPointMake(shoeW * 0.85, shoeH * 0.96),
            controlPoint2 = CGPointMake(shoeW * 0.70, shoeH * 1.00),
        )
        heel.addCurveToPoint(
            CGPointMake(shoeW * 0.15, shoeH * 0.87),
            controlPoint1 = CGPointMake(shoeW * 0.30, shoeH * 1.00),
            controlPoint2 = CGPointMake(shoeW * 0.15, shoeH * 0.96),
        )
        heel.addCurveToPoint(
            CGPointMake(shoeW * 0.50, shoeH * 0.71),
            controlPoint1 = CGPointMake(shoeW * 0.14, shoeH * 0.79),
            controlPoint2 = CGPointMake(shoeW * 0.26, shoeH * 0.71),
        )
        heel.closePath()
        heel.fill()
        CGContextRestoreGState(ctx)
    }
    // 두 발 위치는 SDK의 높이 축소를 상쇄하도록 계산된 값(≈18.3% / 81.7%).
    // 유도 과정은 PATTERN_FOOT_OFFSET 주석 참고.
    shoe(h * PATTERN_FOOT_OFFSET.toDouble(), 8.0)
    shoe(h * (1.0 - PATTERN_FOOT_OFFSET.toDouble()), -8.0)
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return image?.let { OverlayImage(NMFOverlayImage.overlayImageWithImage(it)) }
}

/**
 * 경로 패턴용 동물 발바닥을 그립니다.
 * 발바닥 두 개를 가로 중앙에 놓고 기울기만 ±8°로 번갈아 주어,
 * 경로선을 얇게 유지하면서도 한 발씩 내딛는 걸음 리듬을 표현합니다.
 * dp를 포인트로 그대로 사용합니다(iOS 1pt = 1dp).
 */
actual fun createPawprintOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int,
): OverlayImage? {
    val w = widthDp.toDouble().coerceAtLeast(4.0)
    val h = heightDp.toDouble().coerceAtLeast(4.0)
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(w, h), false, 0.0)
    val ctx = UIGraphicsGetCurrentContext()
    color.toUIColor().setFill()
    // 발바닥 한 개를 cy 높이에 degrees만큼 기울여 그린다.
    // 크기는 이미지 "너비"에서만 정해지므로 heightDp는 걸음 간격만 조절한다.
    fun paw(cy: Double, degrees: Double) {
        var pawW = w * 0.874             // 12° 기울여도 좌우가 잘리지 않는 최대 폭
        var pawH = pawW * 0.80           // 발바닥은 가로가 넓다
        // heightDp가 권장 최소치보다 작으면 자국이 이미지 위아래로 잘린다.
        // 잘리는 대신 자국을 줄여 형태를 온전히 유지한다.
        val halfBound = (pawH * COS12 + pawW * SIN12) / 2.0
        val slot = h * PATTERN_FOOT_OFFSET.toDouble()
        if (halfBound > slot) {
            val fit = slot / halfBound
            pawW *= fit
            pawH *= fit
        }
        CGContextSaveGState(ctx)
        CGContextTranslateCTM(ctx, w / 2.0, cy)
        CGContextRotateCTM(ctx, degrees * kotlin.math.PI / 180.0)
        CGContextTranslateCTM(ctx, -pawW / 2.0, -pawH / 2.0)
        fun toe(tx: Double, ty: Double, rx: Double, ry: Double, deg: Double) {
            CGContextSaveGState(ctx)
            CGContextTranslateCTM(ctx, tx, ty)
            CGContextRotateCTM(ctx, deg * kotlin.math.PI / 180.0)
            UIBezierPath.bezierPathWithOvalInRect(CGRectMake(-rx, -ry, rx * 2.0, ry * 2.0)).fill()
            CGContextRestoreGState(ctx)
        }
        toe(pawW * 0.330, pawH * 0.185, pawW * 0.130, pawH * 0.180, -8.0)
        toe(pawW * 0.670, pawH * 0.185, pawW * 0.130, pawH * 0.180, 8.0)
        toe(pawW * 0.130, pawH * 0.420, pawW * 0.115, pawH * 0.155, -32.0)
        toe(pawW * 0.870, pawH * 0.420, pawW * 0.115, pawH * 0.155, 32.0)
        val pad = UIBezierPath.bezierPath()
        pad.moveToPoint(CGPointMake(pawW * 0.500, pawH * 0.460))
        pad.addCurveToPoint(
            CGPointMake(pawW * 0.716, pawH * 0.585),
            controlPoint1 = CGPointMake(pawW * 0.596, pawH * 0.460),
            controlPoint2 = CGPointMake(pawW * 0.668, pawH * 0.507),
        )
        pad.addCurveToPoint(
            CGPointMake(pawW * 0.770, pawH * 0.876),
            controlPoint1 = CGPointMake(pawW * 0.770, pawH * 0.668),
            controlPoint2 = CGPointMake(pawW * 0.800, pawH * 0.782),
        )
        pad.addCurveToPoint(
            CGPointMake(pawW * 0.500, pawH * 0.980),
            controlPoint1 = CGPointMake(pawW * 0.740, pawH * 0.954),
            controlPoint2 = CGPointMake(pawW * 0.644, pawH * 0.980),
        )
        pad.addCurveToPoint(
            CGPointMake(pawW * 0.230, pawH * 0.876),
            controlPoint1 = CGPointMake(pawW * 0.356, pawH * 0.980),
            controlPoint2 = CGPointMake(pawW * 0.260, pawH * 0.954),
        )
        pad.addCurveToPoint(
            CGPointMake(pawW * 0.284, pawH * 0.585),
            controlPoint1 = CGPointMake(pawW * 0.200, pawH * 0.782),
            controlPoint2 = CGPointMake(pawW * 0.230, pawH * 0.668),
        )
        pad.addCurveToPoint(
            CGPointMake(pawW * 0.500, pawH * 0.460),
            controlPoint1 = CGPointMake(pawW * 0.332, pawH * 0.507),
            controlPoint2 = CGPointMake(pawW * 0.404, pawH * 0.460),
        )
        pad.closePath()
        pad.fill()
        CGContextRestoreGState(ctx)
    }
    // 두 발 위치는 위 발자국과 동일한 보정(PATTERN_FOOT_OFFSET).
    paw(h * PATTERN_FOOT_OFFSET.toDouble(), 12.0)
    paw(h * (1.0 - PATTERN_FOOT_OFFSET.toDouble()), -12.0)
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return image?.let { OverlayImage(NMFOverlayImage.overlayImageWithImage(it)) }
}
