package io.github.kmp.maps.naver.compose.internal

import android.content.res.Resources
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PolylineOverlay
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.LocationTrackingMode
import io.github.kmp.maps.naver.compose.model.MapType
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin

// ──────────────────────────────
// LatLng
// ──────────────────────────────

internal fun LatLng.toNaver(): com.naver.maps.geometry.LatLng =
    com.naver.maps.geometry.LatLng(latitude, longitude)

internal fun com.naver.maps.geometry.LatLng.toCommon(): LatLng =
    LatLng(latitude, longitude)

// ──────────────────────────────
// CameraPosition
// ──────────────────────────────

internal fun CameraPosition.toNaver(): com.naver.maps.map.CameraPosition =
    com.naver.maps.map.CameraPosition(target.toNaver(), zoom, tilt, bearing)

internal fun com.naver.maps.map.CameraPosition.toCommon(): CameraPosition =
    CameraPosition(
        target = target.toCommon(),
        zoom = zoom,
        tilt = tilt,
        bearing = bearing
    )

// ──────────────────────────────
// LatLngBounds
// ──────────────────────────────

internal fun LatLngBounds.toNaver(): com.naver.maps.geometry.LatLngBounds =
    com.naver.maps.geometry.LatLngBounds(southwest.toNaver(), northeast.toNaver())

internal fun com.naver.maps.geometry.LatLngBounds.toCommon(): LatLngBounds =
    LatLngBounds(
        southwest = southWest.toCommon(),
        northeast = northEast.toCommon()
    )

/**
 * 안드로이드 SDK의 LatLng[] (4개의 꼭짓점 영역)을 LatLngBounds로 변환합니다.
 */
internal fun Array<com.naver.maps.geometry.LatLng>.toCommonBounds(): LatLngBounds {
    val nativeBounds = com.naver.maps.geometry.LatLngBounds.from(this.toList())
    return nativeBounds.toCommon()
}

// ──────────────────────────────
// MapType / LocationTrackingMode
// ──────────────────────────────

internal fun MapType.toNaver(): NaverMap.MapType = when (this) {
    MapType.Basic -> NaverMap.MapType.Basic
    MapType.Navi -> NaverMap.MapType.Navi
    MapType.Satellite -> NaverMap.MapType.Satellite
    MapType.Hybrid -> NaverMap.MapType.Hybrid
    MapType.Terrain -> NaverMap.MapType.Terrain
    MapType.None -> NaverMap.MapType.None
}

internal fun LocationTrackingMode.toNaver(): com.naver.maps.map.LocationTrackingMode = when (this) {
    LocationTrackingMode.None -> com.naver.maps.map.LocationTrackingMode.None
    LocationTrackingMode.NoFollow -> com.naver.maps.map.LocationTrackingMode.NoFollow
    LocationTrackingMode.Follow -> com.naver.maps.map.LocationTrackingMode.Follow
    LocationTrackingMode.Face -> com.naver.maps.map.LocationTrackingMode.Face
}

internal fun com.naver.maps.map.LocationTrackingMode.toCommon(): LocationTrackingMode = when (this) {
    com.naver.maps.map.LocationTrackingMode.None -> LocationTrackingMode.None
    com.naver.maps.map.LocationTrackingMode.NoFollow -> LocationTrackingMode.NoFollow
    com.naver.maps.map.LocationTrackingMode.Follow -> LocationTrackingMode.Follow
    com.naver.maps.map.LocationTrackingMode.Face -> LocationTrackingMode.Face
}

// ──────────────────────────────
// LineCap / LineJoin
// ──────────────────────────────

internal fun LineCap.toNaver(): PolylineOverlay.LineCap = when (this) {
    LineCap.Butt -> PolylineOverlay.LineCap.Butt
    LineCap.Round -> PolylineOverlay.LineCap.Round
    LineCap.Square -> PolylineOverlay.LineCap.Square
}

internal fun PolylineOverlay.LineCap.toCommon(): LineCap = when (this) {
    PolylineOverlay.LineCap.Butt -> LineCap.Butt
    PolylineOverlay.LineCap.Round -> LineCap.Round
    PolylineOverlay.LineCap.Square -> LineCap.Square
}

internal fun LineJoin.toNaver(): PolylineOverlay.LineJoin = when (this) {
    LineJoin.Bevel -> PolylineOverlay.LineJoin.Bevel
    LineJoin.Miter -> PolylineOverlay.LineJoin.Miter
    LineJoin.Round -> PolylineOverlay.LineJoin.Round
}

internal fun PolylineOverlay.LineJoin.toCommon(): LineJoin = when (this) {
    PolylineOverlay.LineJoin.Bevel -> LineJoin.Bevel
    PolylineOverlay.LineJoin.Miter -> LineJoin.Miter
    PolylineOverlay.LineJoin.Round -> LineJoin.Round
}

// ──────────────────────────────
// Utils
// ──────────────────────────────

internal fun Float.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density
