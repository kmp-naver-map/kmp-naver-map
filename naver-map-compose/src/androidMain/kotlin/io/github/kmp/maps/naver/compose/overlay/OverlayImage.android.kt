package io.github.kmp.maps.naver.compose.overlay

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import com.naver.maps.map.overlay.OverlayImage as NativeOverlayImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.core.graphics.createBitmap
import kotlin.math.cos
import kotlin.math.sin

/**
 * Android용 OverlayImage 실제 구현체입니다.
 */
actual class OverlayImage internal constructor(internal val nativeImage: NativeOverlayImage) {
    actual companion object {
        actual val DEFAULT: OverlayImage = OverlayImage(com.naver.maps.map.overlay.Marker.DEFAULT_ICON)

        actual fun fromAsset(assetName: String): OverlayImage =
            OverlayImage(NativeOverlayImage.fromAsset(assetName))

        actual fun fromPath(absolutePath: String): OverlayImage =
            OverlayImage(NativeOverlayImage.fromPath(absolutePath))

        fun fromResource(@androidx.annotation.DrawableRes resId: Int): OverlayImage =
            OverlayImage(NativeOverlayImage.fromResource(resId))

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

// ─────────────────────────────────────────────────────────────────────────────
// 내부 헬퍼: teardrop 비트맵 생성
//
// srcBitmap 이 null 이면 흰색 teardrop 만 그림 (placeholder 용).
// srcBitmap 이 있으면 borderWidthPx 만큼 여백을 두고 원 안에 이미지를 합성.
// ─────────────────────────────────────────────────────────────────────────────
private fun buildTearDropBitmap(
    sizePx: Int,
    shadowRadiusPx: Float,
    shadowDx: Float,
    shadowDy: Float,
    shadowColor: Int,
    tailHeightPx: Int,
    srcBitmap: android.graphics.Bitmap?,
    borderWidthPx: Int,
): android.graphics.Bitmap {
    val hasShadow = shadowRadiusPx > 0f
    val hasTail   = tailHeightPx > 0

    val shadowExtra = if (hasShadow) {
        (shadowRadiusPx + kotlin.math.abs(shadowDx) + kotlin.math.abs(shadowDy)).toInt() + 2
    } else 0

    val totalWidth  = sizePx + shadowExtra * 2
    val totalHeight = sizePx + (if (hasTail) tailHeightPx else 0) + shadowExtra * 2

    val cx     = totalWidth / 2f
    val cy     = (shadowExtra + sizePx / 2).toFloat()
    val radius = sizePx / 2f

    // ── 꼬리 베지어 파라미터 ────────────────────────────────────────────────
    val alphaDeg = 30f
    val alphaRad = Math.toRadians(alphaDeg.toDouble()).toFloat()
    val sinA = sin(alphaRad)
    val cosA = cos(alphaRad)

    // 접점: 원 위의 lower-right / lower-left
    val jRx = cx + radius * sinA
    val jRy = cy + radius * cosA
    val jLx = cx - radius * sinA
    val jLy = cy + radius * cosA

    // 둥근 끝 캡
    val tipR      = (tailHeightPx * 0.13f).coerceIn(3f, 12f)
    val tipCenterY = cy + radius + tailHeightPx - tipR

    val t1 = tailHeightPx * 0.55f   // 접선 방향 장력
    val t2 = tailHeightPx * 0.45f   // 끝 진입 장력

    /**
     * 원호 → 오른쪽 베지어 → 둥근 끝 캡 → 왼쪽 베지어 로 이어지는 teardrop Path.
     * ox/oy 오프셋을 주면 그림자용으로도 사용.
     */
    fun buildPath(ox: Float = 0f, oy: Float = 0f): android.graphics.Path {
        val p = android.graphics.Path()
        val oval = android.graphics.RectF(
            cx + ox - radius, cy + oy - radius,
            cx + ox + radius, cy + oy + radius,
        )
        if (hasTail) {
            // ① 원호: lower-left → (상단) → lower-right (시계 방향)
            //    Android arcTo: 0°=오른쪽, 90°=아래, 증가 = 시계 방향
            p.arcTo(oval, 90f + alphaDeg, 360f - 2f * alphaDeg)

            // ② 오른쪽 베지어: lower-right → 둥근 끝 오른쪽
            p.cubicTo(
                jRx + ox - t1 * cosA, jRy + oy + t1 * sinA,   // CP1: 접선 방향
                cx + ox + tipR,        tipCenterY + oy - t2,   // CP2: 끝 위에서 진입
                cx + ox + tipR,        tipCenterY + oy,        // 끝점
            )

            // ③ 둥근 끝 캡: 0° → 180° 시계 방향
            val tipOval = android.graphics.RectF(
                cx + ox - tipR, tipCenterY + oy - tipR,
                cx + ox + tipR, tipCenterY + oy + tipR,
            )
            p.arcTo(tipOval, 0f, 180f)

            // ④ 왼쪽 베지어: 둥근 끝 왼쪽 → lower-left
            p.cubicTo(
                cx + ox - tipR,        tipCenterY + oy - t2,   // CP1: 끝에서 위로
                jLx + ox + t1 * cosA, jLy + oy + t1 * sinA,   // CP2: 접선 역방향
                jLx + ox,              jLy + oy,               // 끝점
            )
        } else {
            p.addCircle(cx + ox, cy + oy, radius, android.graphics.Path.Direction.CW)
        }
        p.close()
        return p
    }

    val output = createBitmap(totalWidth, totalHeight)
    val canvas = android.graphics.Canvas(output)
    val paint  = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

    // 1) 그림자
    if (hasShadow) {
        paint.color     = shadowColor
        paint.maskFilter = android.graphics.BlurMaskFilter(
            shadowRadiusPx,
            android.graphics.BlurMaskFilter.Blur.NORMAL,
        )
        canvas.drawPath(buildPath(shadowDx, shadowDy), paint)
        paint.maskFilter = null
    }

    // 2) 흰색 teardrop
    paint.color = android.graphics.Color.WHITE
    canvas.drawPath(buildPath(), paint)

    // 3) 원 안에 이미지 합성 (srcBitmap 이 있을 때만)
    if (srcBitmap != null) {
        val innerRadius = (radius - borderWidthPx).coerceAtLeast(1f)
        val innerSize   = (innerRadius * 2).toInt()
        val scaled      = android.graphics.Bitmap.createScaledBitmap(srcBitmap, innerSize, innerSize, true)
        val clipPath    = android.graphics.Path().apply {
            addCircle(cx, cy, innerRadius, android.graphics.Path.Direction.CW)
        }
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawBitmap(scaled, cx - innerRadius, cy - innerRadius, paint)
        canvas.restore()
    }

    return output
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
): OverlayImage? = try {
    val bitmap = buildTearDropBitmap(
        sizePx        = sizePx,
        shadowRadiusPx = shadowRadiusPx,
        shadowDx      = shadowDx,
        shadowDy      = shadowDy,
        shadowColor   = shadowColor,
        tailHeightPx  = tailHeightPx,
        srcBitmap     = null,
        borderWidthPx = 0,
    )
    OverlayImage(NativeOverlayImage.fromBitmap(bitmap))
} catch (e: Exception) {
    null
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
): OverlayImage? = withContext(Dispatchers.IO) {
    try {
        val srcBitmap = java.net.URL(url).openStream().use { BitmapFactory.decodeStream(it) }
            ?: return@withContext null
        val bitmap = buildTearDropBitmap(
            sizePx        = sizePx,
            shadowRadiusPx = shadowRadiusPx,
            shadowDx      = shadowDx,
            shadowDy      = shadowDy,
            shadowColor   = shadowColor,
            tailHeightPx  = tailHeightPx,
            srcBitmap     = srcBitmap,
            borderWidthPx = borderWidthPx,
        )
        OverlayImage(NativeOverlayImage.fromBitmap(bitmap))
    } catch (e: Exception) {
        null
    }
}

actual suspend fun downloadOverlayImageFromUrl(url: String): OverlayImage? {
    return withContext(Dispatchers.IO) {
        try {
            val bitmap = java.net.URL(url).openStream().use { stream ->
                BitmapFactory.decodeStream(stream)
            }
            bitmap?.let { OverlayImage(NativeOverlayImage.fromBitmap(it)) }
        } catch (e: Exception) {
            null
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
            val width  = size.width.toInt()
            val height = size.height.toInt()
            val bitmap = createBitmap(width, height)
            val canvas = android.graphics.Canvas(bitmap)
            CanvasDrawScope().draw(
                density         = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas          = Canvas(canvas),
                size            = size,
            ) {
                with(painter) { draw(size) }
            }
            OverlayImage(NativeOverlayImage.fromBitmap(bitmap))
        } else {
            OverlayImage.DEFAULT
        }
    }
}
