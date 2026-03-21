@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package io.github.kmp.maps.naver.compose.internal

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import cocoapods.NMapsMap.*
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.LocationTrackingMode
import io.github.kmp.maps.naver.compose.model.MapType
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import kotlin.math.max
import kotlin.math.min

// ──────────────────────────────
// LatLng
// ──────────────────────────────

internal fun LatLng.toNaver(): NMGLatLng =
    NMGLatLng.latLngWithLat(latitude, longitude)

internal fun NMGLatLng.toCommon(): LatLng =
    LatLng(latitude = lat(), longitude = lng())

// ──────────────────────────────
// CameraPosition
// ──────────────────────────────

internal fun CameraPosition.toNaver(): NMFCameraPosition =
    NMFCameraPosition.cameraPosition(
        target.toNaver(),
        zoom = zoom,
        tilt = tilt,
        heading = bearing
    )

internal fun NMFCameraPosition.toCommon(): CameraPosition =
    CameraPosition(
        target = target().toCommon(),
        zoom = zoom(),
        tilt = tilt(),
        bearing = heading()
    )

// ──────────────────────────────
// LatLngBounds
// ──────────────────────────────

internal fun LatLngBounds.toNaver(): NMGLatLngBounds =
    NMGLatLngBounds.latLngBoundsSouthWest(
        southWest = southwest.toNaver(),
        northEast = northeast.toNaver()
    )

internal fun NMGLatLngBounds.toCommon(): LatLngBounds =
    LatLngBounds(
        southwest = southWest().toCommon(),
        northeast = northEast().toCommon()
    )

/**
 * iOS 네이티브 LatLng 배열을 LatLngBounds로 변환합니다.
 * getContentRegion()이 반환하는 4개의 좌표를 모두 포함하는 최소 사각형 영역을 계산합니다.
 */
internal fun List<NMGLatLng>.toCommonBounds(): LatLngBounds {
    if (this.isEmpty()) return LatLngBounds(LatLng(0.0, 0.0), LatLng(0.0, 0.0))
    
    var minLat = Double.MAX_VALUE
    var maxLat = -Double.MAX_VALUE
    var minLng = Double.MAX_VALUE
    var maxLng = -Double.MAX_VALUE
    
    this.forEach {
        val lat = it.lat()
        val lng = it.lng()
        minLat = min(minLat, lat)
        maxLat = max(maxLat, lat)
        minLng = min(minLng, lng)
        maxLng = max(maxLng, lng)
    }
    
    return LatLngBounds(
        southwest = LatLng(minLat, minLng),
        northeast = LatLng(maxLat, maxLng)
    )
}

// ──────────────────────────────
// MapType / LocationTrackingMode
// ──────────────────────────────

internal fun MapType.toIos(): NMFMapType = when (this) {
    MapType.Basic     -> NMFMapTypeBasic
    MapType.Navi      -> NMFMapTypeNavi
    MapType.Satellite -> NMFMapTypeSatellite
    MapType.Hybrid    -> NMFMapTypeHybrid
    MapType.Terrain   -> NMFMapTypeTerrain
    MapType.None      -> NMFMapTypeNone
}

internal fun LocationTrackingMode.toIos(): NMFMyPositionMode = when (this) {
    LocationTrackingMode.None -> NMFMyPositionDisabled
    LocationTrackingMode.NoFollow -> NMFMyPositionNormal
    LocationTrackingMode.Follow -> NMFMyPositionDirection
    LocationTrackingMode.Face -> NMFMyPositionCompass
}

internal fun NMFMyPositionMode.toCommon(): LocationTrackingMode = when (this) {
    NMFMyPositionDisabled -> LocationTrackingMode.None
    NMFMyPositionNormal -> LocationTrackingMode.NoFollow
    NMFMyPositionDirection -> LocationTrackingMode.Follow
    NMFMyPositionCompass -> LocationTrackingMode.Face
    else -> LocationTrackingMode.None
}

// ──────────────────────────────
// LineCap / LineJoin
// ──────────────────────────────

internal fun LineCap.toNaver(): NMFOverlayLineCap = when (this) {
    LineCap.Butt   -> NMFOverlayLineCap.NMFOverlayLineCapButt
    LineCap.Round  -> NMFOverlayLineCap.NMFOverlayLineCapRound
    LineCap.Square -> NMFOverlayLineCap.NMFOverlayLineCapSquare
}

internal fun Int.toCommonLineCap(): LineCap = when (this) {
    NMFOverlayLineCap.NMFOverlayLineCapButt.value.toInt() -> LineCap.Butt
    NMFOverlayLineCap.NMFOverlayLineCapRound.value.toInt() -> LineCap.Round
    NMFOverlayLineCap.NMFOverlayLineCapSquare.value.toInt() -> LineCap.Square
    else -> LineCap.Butt
}

internal fun LineJoin.toNaver(): NMFOverlayLineJoin = when (this) {
    LineJoin.Bevel -> NMFOverlayLineJoin.NMFOverlayLineJoinBevel
    LineJoin.Miter -> NMFOverlayLineJoin.NMFOverlayLineJoinMiter
    LineJoin.Round -> NMFOverlayLineJoin.NMFOverlayLineJoinRound
}

internal fun Int.toCommonLineJoin(): LineJoin = when (this) {
    NMFOverlayLineJoin.NMFOverlayLineJoinBevel.value.toInt() -> LineJoin.Bevel
    NMFOverlayLineJoin.NMFOverlayLineJoinMiter.value.toInt() -> LineJoin.Miter
    NMFOverlayLineJoin.NMFOverlayLineJoinRound.value.toInt() -> LineJoin.Round
    else -> LineJoin.Round
}

// ──────────────────────────────
// Unit conversion
// ──────────────────────────────

/**
 * iOS는 UIKit points 단위를 사용하며 1pt = 1dp (논리 단위)로 변환 불필요.
 * Android의 dpToPx()와 대칭적으로 사용하기 위해 명시적 함수로 제공.
 */
internal fun Int.dpToPoints(): Double = this.toDouble()
internal fun Float.dpToPoints(): Double = this.toDouble()

// ──────────────────────────────
// Image / Utils
// ──────────────────────────────

internal fun ImageBitmap.toUIImage(): UIImage? {
    val skiaBitmap = this.asSkiaBitmap()
    val image = Image.makeFromBitmap(skiaBitmap)
    val data = image.encodeToData(EncodedImageFormat.PNG) ?: return null
    val bytes = data.bytes
    val nsData = bytes.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
    }
    return UIImage.imageWithData(nsData)
}
