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
import io.github.kmp.maps.naver.compose.internal.dpToPx
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
    backgroundColor: Int,
): android.graphics.Bitmap {
    val metrics = TearDropMetrics(sizePx, shadowRadiusPx, shadowDx, shadowDy, tailHeightPx)
    val hasShadow = metrics.hasShadow
    val hasTail   = metrics.hasTail

    val totalWidth  = metrics.totalSize
    val totalHeight = metrics.totalSize

    val cx     = metrics.cx.toFloat()
    val cy     = metrics.cy.toFloat()
    val radius = metrics.radius.toFloat()

    // ── 꼬리 베지어 파라미터 ────────────────────────────────────────────────
    val alphaDeg = 45f
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

    // 2) teardrop 배경
    paint.color = backgroundColor
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
        // createScaledBitmap은 크기가 같으면 srcBitmap을 그대로 반환하므로 동일 인스턴스 체크
        if (scaled !== srcBitmap) scaled.recycle()
    }

    return output
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
): OverlayImage? = try {
    val bitmap = buildTearDropBitmap(
        sizePx          = sizePx,
        shadowRadiusPx  = shadowRadiusPx,
        shadowDx        = shadowDx,
        shadowDy        = shadowDy,
        shadowColor     = shadowColor,
        tailHeightPx    = tailHeightPx,
        srcBitmap       = null,
        borderWidthPx   = 0,
        backgroundColor = backgroundColor,
    )
    OverlayImage(NativeOverlayImage.fromBitmap(bitmap))
} catch (e: Throwable) {
    // OutOfMemoryError(Error)도 포함하여 크래시 방지
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
    backgroundColor: Int,
    cacheKey: String,
): OverlayImage? {
    val fullCacheKey = "round:$cacheKey:$sizePx:$borderWidthPx:$shadowRadiusPx:$shadowDx:$shadowDy:$shadowColor:$tailHeightPx:$backgroundColor"
    return OverlayImageCache.getOrLoad(fullCacheKey) {
        withContext(Dispatchers.IO) {
            try {
                val srcBitmap = java.net.URL(url).openStream().use { BitmapFactory.decodeStream(it) }
                    ?: return@withContext null
                val bitmap = buildTearDropBitmap(
                    sizePx          = sizePx,
                    shadowRadiusPx  = shadowRadiusPx,
                    shadowDx        = shadowDx,
                    shadowDy        = shadowDy,
                    shadowColor     = shadowColor,
                    tailHeightPx    = tailHeightPx,
                    srcBitmap       = srcBitmap,
                    borderWidthPx   = borderWidthPx,
                    backgroundColor = backgroundColor,
                )
                srcBitmap.recycle()
                OverlayImage(NativeOverlayImage.fromBitmap(bitmap))
            } catch (e: Throwable) {
                // OutOfMemoryError(Error)도 포함하여 크래시 방지
                null
            }
        }
    }
}

actual suspend fun downloadOverlayImageFromUrl(url: String, cacheKey: String): OverlayImage? {
    return OverlayImageCache.getOrLoad("plain:$cacheKey") {
        withContext(Dispatchers.IO) {
            try {
                val bitmap = java.net.URL(url).openStream().use { BitmapFactory.decodeStream(it) }
                bitmap?.let { OverlayImage(NativeOverlayImage.fromBitmap(it)) }
            } catch (e: Throwable) {
                // OutOfMemoryError(Error)도 포함하여 크래시 방지
                null
            }
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

/**
 * 경로 패턴용 진행 방향 화살표를 그립니다.
 * strokeWidthDp가 0이면 밑변 중앙이 파인 꽉 찬 chevron 삼각형,
 * 0보다 크면 열린 꺾쇠(˄) 스트로크로 그립니다.
 */
actual fun createDirectionArrowOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int,
    strokeWidthDp: Float,
): OverlayImage? = try {
    val w = widthDp.dpToPx().toInt().coerceAtLeast(2)
    val h = heightDp.dpToPx().toInt().coerceAtLeast(2)
    val bitmap = createBitmap(w, h)
    val canvas = android.graphics.Canvas(bitmap)
    val strokePx = strokeWidthDp.dpToPx()
    if (strokePx > 0f) {
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = strokePx
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
        }
        val half = strokePx / 2f
        val path = android.graphics.Path().apply {
            moveTo(half, h - half)
            lineTo(w / 2f, half)        // 꼭짓점 (진행 방향)
            lineTo(w - half, h - half)
        }
        canvas.drawPath(path, paint)
    } else {
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = android.graphics.Paint.Style.FILL
        }
        val path = android.graphics.Path().apply {
            moveTo(w / 2f, 0f)          // 꼭짓점 (진행 방향)
            lineTo(w.toFloat(), h.toFloat())
            lineTo(w / 2f, h * 0.72f)   // 밑변 중앙 파임 (chevron)
            lineTo(0f, h.toFloat())
            close()
        }
        canvas.drawPath(path, paint)
    }
    OverlayImage.fromBitmap(bitmap)
} catch (_: Throwable) {
    null
}

/**
 * 경로 패턴용 사람 발자국을 그립니다.
 * 신발 자국 두 개를 가로 중앙에 놓고 기울기만 ±8°로 번갈아 주어,
 * 경로선을 얇게 유지하면서도 한 발씩 내딛는 걸음 리듬을 표현합니다.
 */
actual fun createFootprintOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int,
): OverlayImage? = try {
    val w = widthDp.dpToPx().toInt().coerceAtLeast(4)
    val h = heightDp.dpToPx().toInt().coerceAtLeast(4)
    val fw = w.toFloat()
    val fh = h.toFloat()
    val bitmap = createBitmap(w, h)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = android.graphics.Paint.Style.FILL
    }
    // 신발 자국 한 짝(앞창 + 뒤굽 분리)을 (cx, cy) 중심에 degrees만큼 기울여 그린다.
    // 크기는 이미지 "너비"에서만 정해지므로 heightDp는 걸음 간격만 조절한다.
    fun shoe(cy: Float, degrees: Float) {
        var shoeW = fw * 0.737f          // 8° 기울여도 좌우가 잘리지 않는 최대 폭
        var shoeH = shoeW * 2.63f        // 실제 구두 비율(가로:세로 ≈ 0.38:1)
        // heightDp가 권장 최소치보다 작으면 자국이 이미지 위아래로 잘린다.
        // 잘리는 대신 자국을 줄여 형태를 온전히 유지한다.
        val halfBound = (shoeH * COS8 + shoeW * SIN8) / 2f
        val slot = fh * PATTERN_FOOT_OFFSET
        if (halfBound > slot) {
            val fit = slot / halfBound
            shoeW *= fit
            shoeH *= fit
        }
        canvas.save()
        canvas.rotate(degrees, fw / 2f, cy)
        canvas.translate((fw - shoeW) / 2f, cy - shoeH / 2f)
        // 앞창: 신발 폭을 꽉 채우는 세로로 긴 타원 (전체 길이의 60%까지)
        val front = android.graphics.Path().apply {
            moveTo(shoeW * 0.50f, shoeH * 0.01f)
            cubicTo(shoeW * 0.84f, shoeH * 0.01f, shoeW * 1.00f, shoeH * 0.13f, shoeW * 1.00f, shoeH * 0.30f)
            cubicTo(shoeW * 1.00f, shoeH * 0.46f, shoeW * 0.88f, shoeH * 0.58f, shoeW * 0.70f, shoeH * 0.60f)
            cubicTo(shoeW * 0.58f, shoeH * 0.615f, shoeW * 0.42f, shoeH * 0.615f, shoeW * 0.30f, shoeH * 0.60f)
            cubicTo(shoeW * 0.12f, shoeH * 0.58f, shoeW * 0.00f, shoeH * 0.46f, shoeW * 0.00f, shoeH * 0.30f)
            cubicTo(shoeW * 0.00f, shoeH * 0.13f, shoeW * 0.16f, shoeH * 0.01f, shoeW * 0.50f, shoeH * 0.01f)
            close()
        }
        canvas.drawPath(front, paint)
        // 뒤굽: 앞창과 떨어진 둥근 사각형
        val heel = android.graphics.Path().apply {
            moveTo(shoeW * 0.50f, shoeH * 0.71f)
            cubicTo(shoeW * 0.74f, shoeH * 0.71f, shoeW * 0.86f, shoeH * 0.79f, shoeW * 0.85f, shoeH * 0.87f)
            cubicTo(shoeW * 0.85f, shoeH * 0.96f, shoeW * 0.70f, shoeH * 1.00f, shoeW * 0.50f, shoeH * 1.00f)
            cubicTo(shoeW * 0.30f, shoeH * 1.00f, shoeW * 0.15f, shoeH * 0.96f, shoeW * 0.15f, shoeH * 0.87f)
            cubicTo(shoeW * 0.14f, shoeH * 0.79f, shoeW * 0.26f, shoeH * 0.71f, shoeW * 0.50f, shoeH * 0.71f)
            close()
        }
        canvas.drawPath(heel, paint)
        canvas.restore()
    }
    // 두 발 위치는 위 발바닥과 동일한 보정(PATTERN_FOOT_OFFSET).
    shoe(fh * PATTERN_FOOT_OFFSET, 8f)
    shoe(fh * (1f - PATTERN_FOOT_OFFSET), -8f)
    OverlayImage.fromBitmap(bitmap)
} catch (_: Throwable) {
    null
}

/**
 * 경로 패턴용 동물 발바닥을 그립니다.
 * 발바닥 두 개를 가로 중앙에 놓고 기울기만 ±8°로 번갈아 주어,
 * 경로선을 얇게 유지하면서도 한 발씩 내딛는 걸음 리듬을 표현합니다.
 */
actual fun createPawprintOverlayImage(
    widthDp: Float,
    heightDp: Float,
    color: Int,
): OverlayImage? = try {
    val w = widthDp.dpToPx().toInt().coerceAtLeast(4)
    val h = heightDp.dpToPx().toInt().coerceAtLeast(4)
    val fw = w.toFloat()
    val fh = h.toFloat()
    val bitmap = createBitmap(w, h)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = android.graphics.Paint.Style.FILL
    }
    // 발바닥 한 개를 (cx, cy) 중심에 degrees만큼 기울여 그린다.
    // 크기는 이미지 "너비"에서만 정해지므로 heightDp는 걸음 간격만 조절한다.
    fun paw(cy: Float, degrees: Float) {
        var pawW = fw * 0.874f           // 12° 기울여도 좌우가 잘리지 않는 최대 폭
        var pawH = pawW * 0.80f          // 발바닥은 가로가 넓다
        // heightDp가 권장 최소치보다 작으면 자국이 이미지 위아래로 잘린다.
        // 잘리는 대신 자국을 줄여 형태를 온전히 유지한다.
        val halfBound = (pawH * COS12 + pawW * SIN12) / 2f
        val slot = fh * PATTERN_FOOT_OFFSET
        if (halfBound > slot) {
            val fit = slot / halfBound
            pawW *= fit
            pawH *= fit
        }
        canvas.save()
        canvas.rotate(degrees, fw / 2f, cy)
        canvas.translate((fw - pawW) / 2f, cy - pawH / 2f)
        fun toe(tx: Float, ty: Float, rx: Float, ry: Float, deg: Float) {
            canvas.save()
            canvas.rotate(deg, tx, ty)
            canvas.drawOval(tx - rx, ty - ry, tx + rx, ty + ry, paint)
            canvas.restore()
        }
        toe(pawW * 0.330f, pawH * 0.185f, pawW * 0.130f, pawH * 0.180f, -8f)
        toe(pawW * 0.670f, pawH * 0.185f, pawW * 0.130f, pawH * 0.180f, 8f)
        toe(pawW * 0.130f, pawH * 0.420f, pawW * 0.115f, pawH * 0.155f, -32f)
        toe(pawW * 0.870f, pawH * 0.420f, pawW * 0.115f, pawH * 0.155f, 32f)
        val pad = android.graphics.Path().apply {
            moveTo(pawW * 0.500f, pawH * 0.460f)
            cubicTo(pawW * 0.596f, pawH * 0.460f, pawW * 0.668f, pawH * 0.507f, pawW * 0.716f, pawH * 0.585f)
            cubicTo(pawW * 0.770f, pawH * 0.668f, pawW * 0.800f, pawH * 0.782f, pawW * 0.770f, pawH * 0.876f)
            cubicTo(pawW * 0.740f, pawH * 0.954f, pawW * 0.644f, pawH * 0.980f, pawW * 0.500f, pawH * 0.980f)
            cubicTo(pawW * 0.356f, pawH * 0.980f, pawW * 0.260f, pawH * 0.954f, pawW * 0.230f, pawH * 0.876f)
            cubicTo(pawW * 0.200f, pawH * 0.782f, pawW * 0.230f, pawH * 0.668f, pawW * 0.284f, pawH * 0.585f)
            cubicTo(pawW * 0.332f, pawH * 0.507f, pawW * 0.404f, pawH * 0.460f, pawW * 0.500f, pawH * 0.460f)
            close()
        }
        canvas.drawPath(pad, paint)
        canvas.restore()
    }
    // 두 발 위치는 SDK의 높이 축소를 상쇄하도록 계산된 값(≈18.3% / 81.7%).
    // 유도 과정은 PATTERN_FOOT_OFFSET 주석 참고.
    paw(fh * PATTERN_FOOT_OFFSET, 12f)
    paw(fh * (1f - PATTERN_FOOT_OFFSET), -12f)
    OverlayImage.fromBitmap(bitmap)
} catch (_: Throwable) {
    null
}
