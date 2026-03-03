package io.github.kmp.maps.naver.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.LocationTrackingMode
import io.github.kmp.maps.naver.compose.model.MapType
import io.github.kmp.maps.naver.compose.model.Symbol
import io.github.kmp.maps.naver.compose.options.ArrowheadPathOptions
import io.github.kmp.maps.naver.compose.options.CircleOptions
import io.github.kmp.maps.naver.compose.options.InfoWindowOptions
import io.github.kmp.maps.naver.compose.options.LocationOverlayOptions
import io.github.kmp.maps.naver.compose.options.MapUiSettings
import io.github.kmp.maps.naver.compose.options.MarkerOptions
import io.github.kmp.maps.naver.compose.options.PathOptions
import io.github.kmp.maps.naver.compose.options.PolygonOptions
import io.github.kmp.maps.naver.compose.options.PolylineOptions
import io.github.kmp.maps.naver.compose.overlay.ArrowheadPathOverlay
import io.github.kmp.maps.naver.compose.overlay.CircleOverlay
import io.github.kmp.maps.naver.compose.overlay.InfoWindow
import io.github.kmp.maps.naver.compose.overlay.Marker
import io.github.kmp.maps.naver.compose.overlay.PathOverlay
import io.github.kmp.maps.naver.compose.overlay.PolygonOverlay
import io.github.kmp.maps.naver.compose.overlay.PolylineOverlay

/**
 * [NaverMapState]를 생성하고 기억합니다.
 *
 * Creates and remembers a [NaverMapState] instance.
 *
 * @param initialPosition 초기 카메라 위치. 기본값은 서울시청.
 */
@Composable
fun rememberNaverMapState(
    initialPosition: CameraPosition = CameraPosition.DEFAULT
): NaverMapState {
    return remember { NaverMapState(initialPosition) }
}

/**
 * 네이버 지도의 전체 상태를 관리하는 클래스입니다.
 * 카메라 위치, UI 설정, 오버레이 관리, 이벤트 콜백 등을 포함합니다.
 *
 * Manages the overall state of a Naver Map, including camera position,
 * UI settings, overlay management, and event callbacks.
 *
 * @param initialPosition 초기 카메라 위치.
 */
expect class NaverMapState(initialPosition: CameraPosition) {
    val isMapReady: Boolean
    var cameraPosition: CameraPosition
    
    /**
     * 현재 카메라가 비추고 있는 지도의 영역을 반환합니다.
     */
    val contentRegion: LatLngBounds?

    var uiSettings: MapUiSettings
    var locationTrackingMode: LocationTrackingMode
    var locationOverlayOptions: LocationOverlayOptions

    // 위치 관련
    val lastLocation: LatLng?
    var onLocationChange: ((LatLng) -> Unit)?

    // 카메라 제한 설정
    var minZoom: Double
    var maxZoom: Double
    var extent: LatLngBounds?

    // 카메라 이벤트 콜백
    var onCameraChange: ((reason: Int, animated: Boolean) -> Unit)?
    var onCameraIdle: (() -> Unit)?
    var onCameraChangeStarted: ((reason: Int) -> Unit)?

    // 클릭 이벤트 콜백
    var onMapClick: ((latLng: LatLng) -> Unit)?
    var onSymbolClick: ((symbol: Symbol) -> Boolean)?

    fun addMarker(options: MarkerOptions): Marker
    fun removeMarker(marker: Marker)
    fun clearMarkers()

    fun addPolyline(options: PolylineOptions): PolylineOverlay
    fun removePolyline(overlay: PolylineOverlay)
    fun clearPolylines()

    fun addPolygon(options: PolygonOptions): PolygonOverlay
    fun removePolygon(overlay: PolygonOverlay)
    fun clearPolygons()

    fun addCircle(options: CircleOptions): CircleOverlay
    fun removeCircle(overlay: CircleOverlay)
    fun clearCircles()

    fun addPath(options: PathOptions): PathOverlay
    fun removePath(overlay: PathOverlay)
    fun clearPaths()

    fun addArrowheadPath(options: ArrowheadPathOptions): ArrowheadPathOverlay
    fun removeArrowheadPath(overlay: ArrowheadPathOverlay)
    fun clearArrowheadPaths()

    // InfoWindow 관련
    fun addInfoWindow(options: InfoWindowOptions, marker: Marker? = null): InfoWindow
    fun removeInfoWindow(infoWindow: InfoWindow)
    fun clearInfoWindows()

    fun clearAll()

    fun animateCamera(
        position: CameraPosition,
        durationMs: Int = 800,
        onFinish: (() -> Unit)? = null
    )

    fun fitBounds(
        bounds: LatLngBounds,
        paddingDp: Int = 48
    )

    fun setMapType(mapType: MapType)
    fun setNightMode(enabled: Boolean)
    fun setIndoorEnabled(enabled: Boolean)
    fun setBuildingHeight(height: Float)
    fun setSymbolScale(scale: Float)
}
