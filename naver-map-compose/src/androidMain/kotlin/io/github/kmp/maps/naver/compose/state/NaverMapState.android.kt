package io.github.kmp.maps.naver.compose.state

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.NaverMap
import io.github.kmp.maps.naver.compose.ui.hasLocationPermission
import io.github.kmp.maps.naver.compose.internal.*
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
import io.github.kmp.maps.naver.compose.options.LogoAlign
import io.github.kmp.maps.naver.compose.options.MapUiSettings
import io.github.kmp.maps.naver.compose.options.MarkerOptions
import io.github.kmp.maps.naver.compose.options.PathOptions
import io.github.kmp.maps.naver.compose.options.PolygonOptions
import io.github.kmp.maps.naver.compose.options.PolylineOptions
import io.github.kmp.maps.naver.compose.overlay.ArrowheadPathOverlay
import io.github.kmp.maps.naver.compose.overlay.CircleOverlay
import io.github.kmp.maps.naver.compose.overlay.InfoWindow
import io.github.kmp.maps.naver.compose.overlay.Marker
import io.github.kmp.maps.naver.compose.overlay.OverlayImage
import io.github.kmp.maps.naver.compose.overlay.PathOverlay
import io.github.kmp.maps.naver.compose.overlay.PolygonOverlay
import io.github.kmp.maps.naver.compose.overlay.PolylineOverlay

actual class NaverMapState actual constructor(
    private val initialPosition: CameraPosition
) {
    private val _isMapReady = mutableStateOf(false)
    actual val isMapReady: Boolean get() = _isMapReady.value

    internal var _context: Context? = null  // 내부 Context (dpToPx 변환 등에서 사용)

    // 카메라 위치를 State로 관리하여 실시간 동기화
    private var _cameraPosition by mutableStateOf(initialPosition)
    actual var cameraPosition: CameraPosition
        get() = _cameraPosition
        set(value) {
            _cameraPosition = value
            naverMap?.moveCamera(CameraUpdate.toCameraPosition(value.toNaver()))
        }

    // 현재 보이는 영역을 State로 관리
    private val _contentRegion = mutableStateOf<LatLngBounds?>(null)
    actual val contentRegion: LatLngBounds? get() = _contentRegion.value

    internal var naverMap: NaverMap? = null
        set(value) {
            field = value
            _isMapReady.value = value != null
            if (value != null) {
                // initialPosition 적용 (listeners 설정 전에)
                value.moveCamera(CameraUpdate.toCameraPosition(initialPosition.toNaver()))

                value.locationSource = _locationSource
                applyUiSettings()
                applyLocationTrackingMode()
                applyLocationOverlayOptions()
                applyCameraConstraints()
                setupListeners(value)

                // moveCamera는 비동기라 읽어오지 않고 initialPosition을 직접 사용
                _cameraPosition = initialPosition
                _contentRegion.value = value.contentRegion.toCommonBounds()
            }
        }

    private var _locationSource: LocationSource? = null
    var locationSource: LocationSource?
        get() = _locationSource
        set(value) {
            _locationSource = value
            naverMap?.locationSource = value
        }

    // NaverMapView에서 설정하는 권한 요청 launcher
    internal var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    private var _lastLocation = mutableStateOf<LatLng?>(null)
    actual val lastLocation: LatLng? get() = _lastLocation.value

    actual var onLocationChange: ((LatLng) -> Unit)? = null

    private val _markers = mutableListOf<Marker>()
    private val _polylines = mutableListOf<PolylineOverlay>()
    private val _polygons = mutableListOf<PolygonOverlay>()
    private val _circles = mutableListOf<CircleOverlay>()
    private val _paths = mutableListOf<PathOverlay>()
    private val _arrowheadPaths = mutableListOf<ArrowheadPathOverlay>()
    private val _infoWindows = mutableListOf<InfoWindow>()

    private var _uiSettings = MapUiSettings()
    actual var uiSettings: MapUiSettings
        get() = _uiSettings
        set(value) {
            _uiSettings = value
            applyUiSettings()
        }

    private fun applyUiSettings() {
        naverMap?.let { map ->
            val settings = map.uiSettings
            settings.isScrollGesturesEnabled = _uiSettings.isScrollGesturesEnabled
            settings.isZoomGesturesEnabled = _uiSettings.isZoomGesturesEnabled
            settings.isTiltGesturesEnabled = _uiSettings.isTiltGesturesEnabled
            settings.isRotateGesturesEnabled = _uiSettings.isRotateGesturesEnabled
            settings.isStopGesturesEnabled = _uiSettings.isStopGesturesEnabled
            settings.isCompassEnabled = _uiSettings.isCompassEnabled
            settings.isScaleBarEnabled = _uiSettings.isScaleBarEnabled
            settings.isZoomControlEnabled = _uiSettings.isZoomControlEnabled
            settings.isIndoorLevelPickerEnabled = _uiSettings.isIndoorLevelPickerEnabled
            settings.isLocationButtonEnabled = _uiSettings.isLocationButtonEnabled
            settings.isLogoClickEnabled = _uiSettings.isLogoClickEnabled

            // 로고 위치 및 마진
            val logoGravity = when (_uiSettings.logoAlign) {
                LogoAlign.LeftBottom  -> android.view.Gravity.BOTTOM or android.view.Gravity.START
                LogoAlign.RightBottom -> android.view.Gravity.BOTTOM or android.view.Gravity.END
                LogoAlign.LeftTop     -> android.view.Gravity.TOP    or android.view.Gravity.START
                LogoAlign.RightTop    -> android.view.Gravity.TOP    or android.view.Gravity.END
            }
            settings.setLogoGravity(logoGravity)
            settings.setLogoMargin(
                _uiSettings.logoMarginLeft.toFloat().dpToPx().toInt(),
                _uiSettings.logoMarginTop.toFloat().dpToPx().toInt(),
                _uiSettings.logoMarginRight.toFloat().dpToPx().toInt(),
                _uiSettings.logoMarginBottom.toFloat().dpToPx().toInt(),
            )

            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, _uiSettings.isBuildingLayerGroupEnabled)
            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, _uiSettings.isTransitLayerGroupEnabled)
            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, _uiSettings.isBicycleLayerGroupEnabled)
            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, _uiSettings.isTrafficLayerGroupEnabled)
            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, _uiSettings.isMountainLayerGroupEnabled)
            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, _uiSettings.isCadastralLayerGroupEnabled)
        }
    }

    private var _locationTrackingMode = mutableStateOf(LocationTrackingMode.None)
    actual var locationTrackingMode: LocationTrackingMode
        get() = _locationTrackingMode.value
        set(value) {
            if (_locationTrackingMode.value != value) {
                _locationTrackingMode.value = value
                applyLocationTrackingMode()
            }
        }

    private fun applyLocationTrackingMode() {
        // 지도가 아직 준비되지 않았으면 naverMap setter에서 다시 호출됨
        val map = naverMap ?: return

        if (_locationTrackingMode.value != LocationTrackingMode.None) {
            val ctx = _context ?: return

            if (!ctx.hasLocationPermission()) {
                // 권한 없음 → 요청 후 대기 (onPermissionGranted에서 이어서 적용)
                permissionLauncher?.launch(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                )
                return
            }
        }

        map.locationTrackingMode = _locationTrackingMode.value.toNaver()
    }

    // 권한 허용 후 NaverMapView에서 호출
    internal fun onPermissionGranted() {
        naverMap?.locationTrackingMode = _locationTrackingMode.value.toNaver()
    }

    // 권한 거부 시 NaverMapView에서 호출 → 모드를 None으로 복원
    internal fun onPermissionDenied() {
        _locationTrackingMode.value = LocationTrackingMode.None
        naverMap?.locationTrackingMode = _locationTrackingMode.value.toNaver()
    }

    private var _locationOverlayOptions = LocationOverlayOptions()
    actual var locationOverlayOptions: LocationOverlayOptions
        get() = _locationOverlayOptions
        set(value) {
            _locationOverlayOptions = value
            applyLocationOverlayOptions()
        }

    private fun applyLocationOverlayOptions() {
        naverMap?.locationOverlay?.let { overlay ->
            overlay.isVisible = _locationOverlayOptions.isVisible
            (_locationOverlayOptions.icon as? OverlayImage)?.let { overlay.icon = it.nativeImage }
            
            overlay.iconWidth = if (_locationOverlayOptions.width == Marker.MarkerSize.AUTO)
                com.naver.maps.map.overlay.Marker.SIZE_AUTO
            else _locationOverlayOptions.width.dpToPx().toInt()
            overlay.iconHeight = if (_locationOverlayOptions.height == Marker.MarkerSize.AUTO)
                com.naver.maps.map.overlay.Marker.SIZE_AUTO
            else _locationOverlayOptions.height.dpToPx().toInt()
            overlay.anchor = PointF(_locationOverlayOptions.anchor.x, _locationOverlayOptions.anchor.y)
            overlay.bearing = _locationOverlayOptions.bearing
            overlay.globalZIndex = _locationOverlayOptions.globalZIndex
            
            overlay.circleRadius = _locationOverlayOptions.circleRadius.toInt()
            overlay.circleColor = _locationOverlayOptions.circleColor
            overlay.circleOutlineWidth = _locationOverlayOptions.circleOutlineWidth.dpToPx().toInt()
            overlay.circleOutlineColor = _locationOverlayOptions.circleOutlineColor
            
            if (_locationOverlayOptions.isSubIconVisible) {
                // 커스텀 subIcon이 없으면 SDK 내장 화살표 아이콘을 기본값으로 사용
                overlay.subIcon = (_locationOverlayOptions.subIcon as? OverlayImage)?.nativeImage
                    ?: com.naver.maps.map.overlay.OverlayImage.fromResource(
                        com.naver.maps.map.R.drawable.navermap_default_location_overlay_sub_icon_arrow
                    )
            } else {
                overlay.subIcon = null
            }
            
            overlay.subIconWidth = if (_locationOverlayOptions.subIconWidth == Marker.MarkerSize.AUTO)
                com.naver.maps.map.overlay.Marker.SIZE_AUTO
            else _locationOverlayOptions.subIconWidth.dpToPx().toInt()
            overlay.subIconHeight = if (_locationOverlayOptions.subIconHeight == Marker.MarkerSize.AUTO)
                com.naver.maps.map.overlay.Marker.SIZE_AUTO
            else _locationOverlayOptions.subIconHeight.dpToPx().toInt()
            overlay.subAnchor = PointF(_locationOverlayOptions.subIconAnchor.x, _locationOverlayOptions.subIconAnchor.y)
        }
    }

    private var _minZoom: Double = 0.0
    actual var minZoom: Double
        get() = _minZoom
        set(value) {
            _minZoom = value
            naverMap?.minZoom = value
        }

    private var _maxZoom: Double = 21.0
    actual var maxZoom: Double
        get() = _maxZoom
        set(value) {
            _maxZoom = value
            naverMap?.maxZoom = value
        }

    private var _extent: LatLngBounds? = null
    actual var extent: LatLngBounds?
        get() = _extent
        set(value) {
            _extent = value
            naverMap?.extent = value?.toNaver()
        }

    private fun applyCameraConstraints() {
        naverMap?.let { map ->
            map.minZoom = _minZoom
            map.maxZoom = _maxZoom
            map.extent = _extent?.toNaver()
        }
    }

    actual var onCameraChange: ((reason: Int, animated: Boolean) -> Unit)? = null
    actual var onCameraIdle: (() -> Unit)? = null
    actual var onCameraChangeStarted: ((reason: Int) -> Unit)? = null

    actual var onMapClick: ((latLng: LatLng) -> Unit)? = null
    actual var onSymbolClick: ((symbol: Symbol) -> Boolean)? = null

    private fun setupListeners(map: NaverMap) {
        map.addOnCameraChangeListener { reason, animated ->
            // 카메라 상태 동기화
            _cameraPosition = map.cameraPosition.toCommon()
            _contentRegion.value = map.contentRegion.toCommonBounds()
            onCameraChange?.invoke(reason, animated)
        }
        map.addOnCameraIdleListener {
            onCameraIdle?.invoke()
        }
        
        map.setOnMapClickListener { _, latLng ->
            onMapClick?.invoke(latLng.toCommon())
        }
        
        map.setOnSymbolClickListener { symbol ->
            onSymbolClick?.invoke(Symbol(symbol.caption, symbol.position.toCommon())) ?: false
        }

        map.addOnLocationChangeListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            _lastLocation.value = latLng
            onLocationChange?.invoke(latLng)
        }

        map.addOnOptionChangeListener {
            val mode = map.locationTrackingMode
            _locationTrackingMode.value = mode.toCommon()
        }
    }

    actual fun addMarker(options: MarkerOptions): Marker {
        val nativeMarker = com.naver.maps.map.overlay.Marker().apply {
            position = options.position.toNaver()
            (options.icon as? OverlayImage)?.let { icon = it.nativeImage }
            captionText = options.caption
            subCaptionText = options.subCaption
            alpha = options.alpha
            isVisible = options.isVisible
            isFlat = options.isFlat
            isForceShowCaption = options.isForceShowCaption
            isForceShowIcon = options.isForceShowIcon
            zIndex = options.zIndex
            globalZIndex = options.globalZIndex
            width = if (options.width == Marker.MarkerSize.AUTO) com.naver.maps.map.overlay.Marker.SIZE_AUTO else options.width.dpToPx().toInt()
            height = if (options.height == Marker.MarkerSize.AUTO) com.naver.maps.map.overlay.Marker.SIZE_AUTO else options.height.dpToPx().toInt()
            angle = options.angle
            anchor = PointF(options.anchor.x, options.anchor.y)
            minZoom = options.minZoom
            maxZoom = options.maxZoom
            isMinZoomInclusive = options.isMinZoomInclusive
            isMaxZoomInclusive = options.isMaxZoomInclusive
            captionColor = options.captionColor
            captionHaloColor = options.captionHaloColor
            captionTextSize = options.captionTextSize
            captionMinZoom = options.captionMinZoom
            captionMaxZoom = options.captionMaxZoom
            captionRequestedWidth = options.captionRequestedWidth.dpToPx().toInt()
            captionOffset = options.captionOffset.dpToPx().toInt()
            isCaptionPerspectiveEnabled = options.captionPerspectiveEnabled
            subCaptionColor = options.subCaptionColor
            subCaptionHaloColor = options.subCaptionHaloColor
            subCaptionTextSize = options.subCaptionTextSize
            subCaptionMinZoom = options.subCaptionMinZoom
            subCaptionMaxZoom = options.subCaptionMaxZoom
            subCaptionRequestedWidth = options.subCaptionRequestedWidth.toInt()
            isHideCollidedMarkers = options.isHideCollidedMarkers
            isHideCollidedSymbols = options.isHideCollidedSymbols
            isHideCollidedCaptions = options.isHideCollidedCaptions
            isIconPerspectiveEnabled = options.isIconPerspectiveEnabled
            if (options.iconTintColor != 0) iconTintColor = options.iconTintColor
            tag = options.tag
            map = naverMap
        }
        return Marker(nativeMarker).also { _markers.add(it) }
    }

    actual fun removeMarker(marker: Marker) {
        marker.nativeMarker.map = null
        _markers.remove(marker)
    }

    actual fun clearMarkers() {
        _markers.forEach { it.nativeMarker.map = null }
        _markers.clear()
    }

    actual fun addPolyline(options: PolylineOptions): PolylineOverlay {
        val nativePolyline = com.naver.maps.map.overlay.PolylineOverlay().apply {
            coords = options.coords.map { it.toNaver() }
            color = options.color
            width = options.width.dpToPx().toInt()
            capType = options.capType.toNaver()
            joinType = options.joinType.toNaver()
            zIndex = options.zIndex
            isVisible = options.isVisible
            tag = options.tag
            map = naverMap
        }
        return PolylineOverlay(nativePolyline).apply {
            if (options.pattern.isNotEmpty()) pattern = options.pattern
        }.also { _polylines.add(it) }
    }

    actual fun removePolyline(overlay: PolylineOverlay) {
        overlay.nativePolyline.map = null
        _polylines.remove(overlay)
    }

    actual fun clearPolylines() {
        _polylines.forEach { it.nativePolyline.map = null }
        _polylines.clear()
    }

    actual fun addPolygon(options: PolygonOptions): PolygonOverlay {
        val nativePolygon = com.naver.maps.map.overlay.PolygonOverlay().apply {
            coords = options.coords.map { it.toNaver() }
            holes = options.holes.map { hole -> hole.map { it.toNaver() } }
            color = options.fillColor
            outlineColor = options.outlineColor
            outlineWidth = options.outlineWidth.dpToPx().toInt()
            zIndex = options.zIndex
            isVisible = options.isVisible
            tag = options.tag
            map = naverMap
        }
        return PolygonOverlay(nativePolygon).also { _polygons.add(it) }
    }

    actual fun removePolygon(overlay: PolygonOverlay) {
        overlay.nativePolygon.map = null
        _polygons.remove(overlay)
    }

    actual fun clearPolygons() {
        _polygons.forEach { it.nativePolygon.map = null }
        _polygons.clear()
    }

    actual fun addCircle(options: CircleOptions): CircleOverlay {
        val nativeCircle = com.naver.maps.map.overlay.CircleOverlay().apply {
            center = options.center.toNaver()
            radius = options.radius
            color = options.fillColor
            outlineColor = options.outlineColor
            outlineWidth = options.outlineWidth.dpToPx().toInt()
            zIndex = options.zIndex
            isVisible = options.isVisible
            tag = options.tag
            map = naverMap
        }
        return CircleOverlay(nativeCircle).also { _circles.add(it) }
    }

    actual fun removeCircle(overlay: CircleOverlay) {
        overlay.nativeCircle.map = null
        _circles.remove(overlay)
    }

    actual fun clearCircles() {
        _circles.forEach { it.nativeCircle.map = null }
        _circles.clear()
    }

    actual fun addPath(options: PathOptions): PathOverlay {
        val nativePath = com.naver.maps.map.overlay.PathOverlay().apply {
            coords = options.coords.map { it.toNaver() }
            width = options.width.dpToPx().toInt()
            outlineWidth = options.outlineWidth.dpToPx().toInt()
            color = options.color
            outlineColor = options.outlineColor
            passedColor = options.passedColor
            passedOutlineColor = options.passedOutlineColor
            progress = options.progress
            patternInterval = options.patternInterval.dpToPx().toInt()
            isHideCollidedSymbols = options.isHideCollidedSymbols
            isHideCollidedMarkers = options.isHideCollidedMarkers
            isHideCollidedCaptions = options.isHideCollidedCaptions
            zIndex = options.zIndex
            isVisible = options.isVisible
            tag = options.tag
            map = naverMap
        }
        return PathOverlay(nativePath).also { _paths.add(it) }
    }

    actual fun removePath(overlay: PathOverlay) {
        overlay.nativePathOverlay.map = null
        _paths.remove(overlay)
    }

    actual fun clearPaths() {
        _paths.forEach { it.nativePathOverlay.map = null }
        _paths.clear()
    }

    actual fun addArrowheadPath(options: ArrowheadPathOptions): ArrowheadPathOverlay {
        val nativeArrowheadPath = com.naver.maps.map.overlay.ArrowheadPathOverlay().apply {
            coords = options.coords.map { it.toNaver() }
            width = options.width.dpToPx().toInt()
            outlineWidth = options.outlineWidth.dpToPx().toInt()
            color = options.color
            outlineColor = options.outlineColor
            elevation = options.elevation.dpToPx().toInt()
            headSizeRatio = options.headSizeRatio
            zIndex = options.zIndex
            isVisible = options.isVisible
            tag = options.tag
            map = naverMap
        }
        return ArrowheadPathOverlay(nativeArrowheadPath).also { _arrowheadPaths.add(it) }
    }

    actual fun removeArrowheadPath(overlay: ArrowheadPathOverlay) {
        overlay.nativeArrowheadPathOverlay.map = null
        _arrowheadPaths.remove(overlay)
    }

    actual fun clearArrowheadPaths() {
        _arrowheadPaths.forEach { it.nativeArrowheadPathOverlay.map = null }
        _arrowheadPaths.clear()
    }

    actual fun addInfoWindow(options: InfoWindowOptions, marker: Marker?): InfoWindow {
        val adapterContext = _context ?: throw IllegalStateException("Context is not available. Ensure the map is ready.")
        val nativeInfoWindow = com.naver.maps.map.overlay.InfoWindow()
        val infoWindow = InfoWindow(nativeInfoWindow)
        nativeInfoWindow.apply {
            position = options.position.toNaver()
            alpha = options.alpha
            zIndex = options.zIndex
            anchor = PointF(options.anchor.x, options.anchor.y)
            offsetX = options.offsetX
            offsetY = options.offsetY
            adapter = object : com.naver.maps.map.overlay.InfoWindow.ViewAdapter() {
                override fun getView(iw: com.naver.maps.map.overlay.InfoWindow): View {
                    return TextView(adapterContext).apply {
                        text = infoWindow.text
                        setTextColor(infoWindow.textColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, infoWindow.textSize)
                        val shape = GradientDrawable().apply {
                            setColor(infoWindow.backgroundColor)
                            cornerRadius = infoWindow.cornerRadiusDp.dpToPx()
                        }
                        background = shape
                        val p = 8.toFloat().dpToPx().toInt()
                        setPadding(p, p, p, p)
                    }
                }
            }
            tag = options.tag
        }
        infoWindow.applyOptions(options)
        if (marker != null) nativeInfoWindow.open(marker.nativeMarker) else naverMap?.let { nativeInfoWindow.open(it) }
        return infoWindow.also { _infoWindows.add(it) }
    }

    actual fun removeInfoWindow(infoWindow: InfoWindow) {
        infoWindow.close()
        _infoWindows.remove(infoWindow)
    }

    actual fun clearInfoWindows() {
        _infoWindows.forEach { it.close() }
        _infoWindows.clear()
    }

    actual fun clearAll() {
        clearMarkers()
        clearPolylines()
        clearPolygons()
        clearCircles()
        clearPaths()
        clearArrowheadPaths()
        clearInfoWindows()
    }

    actual fun animateCamera(position: CameraPosition, animation: io.github.kmp.maps.naver.compose.model.CameraAnimation, durationMs: Int, onFinish: (() -> Unit)?) {
        val nativeAnimation = when (animation) {
            io.github.kmp.maps.naver.compose.model.CameraAnimation.Easing -> CameraAnimation.Easing
            io.github.kmp.maps.naver.compose.model.CameraAnimation.Fly -> CameraAnimation.Fly
            io.github.kmp.maps.naver.compose.model.CameraAnimation.Linear -> CameraAnimation.Linear
            io.github.kmp.maps.naver.compose.model.CameraAnimation.None -> CameraAnimation.None
        }
        val update = CameraUpdate.toCameraPosition(position.toNaver()).animate(nativeAnimation, durationMs.toLong())
        val map = naverMap ?: return
        if (onFinish != null) {
            map.addOnCameraIdleListener(object : NaverMap.OnCameraIdleListener {
                override fun onCameraIdle() {
                    map.removeOnCameraIdleListener(this)
                    onFinish.invoke()
                }
            })
        }
        map.moveCamera(update)
    }

    actual fun fitBounds(bounds: LatLngBounds, paddingDp: Int, animation: io.github.kmp.maps.naver.compose.model.CameraAnimation, durationMs: Int) {
        val paddingPx = paddingDp.toFloat().dpToPx().toInt()
        val nativeAnimation = when (animation) {
            io.github.kmp.maps.naver.compose.model.CameraAnimation.Easing -> CameraAnimation.Easing
            io.github.kmp.maps.naver.compose.model.CameraAnimation.Fly -> CameraAnimation.Fly
            io.github.kmp.maps.naver.compose.model.CameraAnimation.Linear -> CameraAnimation.Linear
            io.github.kmp.maps.naver.compose.model.CameraAnimation.None -> CameraAnimation.None
        }
        val update = CameraUpdate.fitBounds(bounds.toNaver(), paddingPx).animate(nativeAnimation, durationMs.toLong())
        naverMap?.moveCamera(update)
    }
    actual fun setMapType(mapType: MapType) { naverMap?.mapType = mapType.toNaver() }
    actual fun setNightMode(enabled: Boolean) { naverMap?.isNightModeEnabled = enabled }
    actual fun setIndoorEnabled(enabled: Boolean) { naverMap?.isIndoorEnabled = enabled }
    actual fun setBuildingHeight(height: Float) { naverMap?.buildingHeight = height }
    actual fun setSymbolScale(scale: Float) { naverMap?.symbolScale = scale }
    actual fun setCustomStyleId(customStyleId: String?) { naverMap?.customStyleId = customStyleId }
}
