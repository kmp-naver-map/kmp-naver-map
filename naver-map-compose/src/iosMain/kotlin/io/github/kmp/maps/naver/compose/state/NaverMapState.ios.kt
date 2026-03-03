@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cocoapods.NMapsMap.*
import io.github.kmp.maps.naver.compose.internal.*
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.LocationTrackingMode
import io.github.kmp.maps.naver.compose.model.MapType
import io.github.kmp.maps.naver.compose.model.Symbol
import io.github.kmp.maps.naver.compose.options.*
import io.github.kmp.maps.naver.compose.overlay.*
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import platform.CoreGraphics.*
import platform.UIKit.*
import platform.darwin.NSObject

@Stable
actual class NaverMapState actual constructor(
    private val initialPosition: CameraPosition
) {
    private val _isMapReady = mutableStateOf(false)
    actual val isMapReady: Boolean get() = _isMapReady.value

    // 카메라 위치 실시간 State
    private var _cameraPosition by mutableStateOf(initialPosition)
    actual var cameraPosition: CameraPosition
        get() = _cameraPosition
        set(value) {
            _cameraPosition = value
            naverMap?.moveCamera(NMFCameraUpdate.cameraUpdateWithPosition(value.toNaver()))
        }

    // 현재 보이는 영역 실시간 State
    private val _contentRegion = mutableStateOf<LatLngBounds?>(null)
    actual val contentRegion: LatLngBounds? get() = _contentRegion.value

    internal var naverMapView: NMFNaverMapView? = null
        set(value) {
            field = value
            _isMapReady.value = value != null

            if (value != null) {
                val cameraUpdate = NMFCameraUpdate.cameraUpdateWithPosition(initialPosition.toNaver())
                value.mapView.moveCamera(cameraUpdate)

                applyUiSettings()
                applyLocationTrackingMode()
                applyLocationOverlayOptions()
                applyCameraConstraints()
                setupListeners(value)

                // 초기 상태 동기화
                _cameraPosition = value.mapView.cameraPosition.toCommon()
                _contentRegion.value = value.mapView.contentRegion.exteriorRing().points()
                    .filterIsInstance<NMGLatLng>().toCommonBounds()
            }
        }

    internal val naverMap: NMFMapView? get() = naverMapView?.mapView

    private val _lastLocation = mutableStateOf<LatLng?>(null)
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

    internal fun applyUiSettings() {
        val container = naverMapView ?: return
        val map = container.mapView
        map.scrollGestureEnabled = _uiSettings.isScrollGesturesEnabled
        map.zoomGestureEnabled = _uiSettings.isZoomGesturesEnabled
        map.tiltGestureEnabled = _uiSettings.isTiltGesturesEnabled
        map.rotateGestureEnabled = _uiSettings.isRotateGesturesEnabled
        map.stopGestureEnabled = _uiSettings.isStopGesturesEnabled
        container.showCompass = _uiSettings.isCompassEnabled
        container.showScaleBar = _uiSettings.isScaleBarEnabled
        container.showZoomControls = _uiSettings.isZoomControlEnabled
        container.showIndoorLevelPicker = _uiSettings.isIndoorLevelPickerEnabled
        container.showLocationButton = _uiSettings.isLocationButtonEnabled
        map.logoInteractionEnabled = _uiSettings.isLogoClickEnabled
        map.setLayerGroup(NMF_LAYER_GROUP_BUILDING, _uiSettings.isBuildingLayerGroupEnabled)
        map.setLayerGroup(NMF_LAYER_GROUP_TRANSIT, _uiSettings.isTransitLayerGroupEnabled)
        map.setLayerGroup(NMF_LAYER_GROUP_BICYCLE, _uiSettings.isBicycleLayerGroupEnabled)
        map.setLayerGroup(NMF_LAYER_GROUP_TRAFFIC, _uiSettings.isTrafficLayerGroupEnabled)
        map.setLayerGroup(NMF_LAYER_GROUP_MOUNTAIN, _uiSettings.isMountainLayerGroupEnabled)
        map.setLayerGroup(NMF_LAYER_GROUP_CADASTRAL, _uiSettings.isCadastralLayerGroupEnabled)
    }

    private var _locationTrackingMode = mutableStateOf(LocationTrackingMode.None)
    actual var locationTrackingMode: LocationTrackingMode
        get() = _locationTrackingMode.value
        set(value) {
            _locationTrackingMode.value = value
            applyLocationTrackingMode()
        }

    private fun applyLocationTrackingMode() {
        val map = naverMap ?: return
        val mode = _locationTrackingMode.value.toIos()
        if (map.positionMode != mode) map.positionMode = mode
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
            overlay.hidden = !_locationOverlayOptions.isVisible
            (_locationOverlayOptions.icon as? OverlayImage)?.let { overlay.icon = it.nativeImage }
            overlay.iconWidth = _locationOverlayOptions.width.toDouble()
            overlay.iconHeight = _locationOverlayOptions.height.toDouble()
            overlay.anchor = CGPointMake(_locationOverlayOptions.anchor.x.toDouble(), _locationOverlayOptions.anchor.y.toDouble())
            overlay.heading = _locationOverlayOptions.bearing.toDouble()
            overlay.globalZIndex = _locationOverlayOptions.globalZIndex.toLong()
            overlay.circleRadius = _locationOverlayOptions.circleRadius
            overlay.circleColor = _locationOverlayOptions.circleColor.toUIColor()
            overlay.circleOutlineWidth = _locationOverlayOptions.circleOutlineWidth.toDouble()
            overlay.circleOutlineColor = _locationOverlayOptions.circleOutlineColor.toUIColor()
            if (_locationOverlayOptions.isSubIconVisible) {
                (_locationOverlayOptions.subIcon as? OverlayImage)?.let { overlay.subIcon = it.nativeImage }
            } else {
                overlay.subIcon = null
            }
            overlay.subIconWidth = _locationOverlayOptions.subIconWidth.toDouble()
            overlay.subIconHeight = _locationOverlayOptions.subIconHeight.toDouble()
            overlay.subAnchor = CGPointMake(_locationOverlayOptions.subIconAnchor.x.toDouble(), _locationOverlayOptions.subIconAnchor.y.toDouble())
        }
    }

    private var _minZoom: Double = 0.0
    actual var minZoom: Double
        get() = _minZoom
        set(value) { _minZoom = value; naverMap?.minZoomLevel = value }

    private var _maxZoom: Double = 21.0
    actual var maxZoom: Double
        get() = _maxZoom
        set(value) { _maxZoom = value; naverMap?.maxZoomLevel = value }

    private var _extent: LatLngBounds? = null
    actual var extent: LatLngBounds?
        get() = _extent
        set(value) { _extent = value; naverMap?.extent = value?.toNaver() }

    private fun applyCameraConstraints() {
        naverMap?.let { map ->
            map.minZoomLevel = _minZoom
            map.maxZoomLevel = _maxZoom
            map.extent = _extent?.toNaver()
        }
    }

    actual var onCameraChange: ((reason: Int, animated: Boolean) -> Unit)? = null
    actual var onCameraIdle: (() -> Unit)? = null
    actual var onCameraChangeStarted: ((reason: Int) -> Unit)? = null
    actual var onMapClick: ((latLng: LatLng) -> Unit)? = null
    actual var onSymbolClick: ((symbol: Symbol) -> Boolean)? = null

    private fun setupListeners(naverView: NMFNaverMapView) {
        val map = naverView.mapView
        map.addOptionDelegate(object : NSObject(), NMFMapViewOptionDelegateProtocol {
            override fun mapViewOptionChanged(mapView: NMFMapView) {
                val commonMode = mapView.positionMode.toCommon()
                if (_locationTrackingMode.value != commonMode) _locationTrackingMode.value = commonMode
            }
        })

        map.addCameraDelegate(object : NSObject(), NMFMapViewCameraDelegateProtocol {
            @ObjCSignatureOverride
            override fun mapView(mapView: NMFMapView, cameraWillChangeByReason: Long, animated: Boolean) {
                if (cameraWillChangeByReason == -1L) {
                    if (_locationTrackingMode.value == LocationTrackingMode.Follow || _locationTrackingMode.value == LocationTrackingMode.Face) {
                        _locationTrackingMode.value = LocationTrackingMode.NoFollow
                    }
                }
                onCameraChangeStarted?.invoke(cameraWillChangeByReason.toInt())
            }

            override fun mapView(mapView: NMFMapView, cameraIsChangingByReason: Long) {
                // 실시간 카메라 위치 및 영역 동기화
                _cameraPosition = mapView.cameraPosition.toCommon()
                _contentRegion.value = mapView.contentRegion.exteriorRing().points()
                    .filterIsInstance<NMGLatLng>().toCommonBounds()
                onCameraChange?.invoke(cameraIsChangingByReason.toInt(), true)
            }

            @ObjCSignatureOverride
            override fun mapView(mapView: NMFMapView, cameraDidChangeByReason: Long, animated: Boolean) {
                _cameraPosition = mapView.cameraPosition.toCommon()
                _contentRegion.value = mapView.contentRegion.exteriorRing().points()
                    .filterIsInstance<NMGLatLng>().toCommonBounds()
                onCameraChange?.invoke(cameraDidChangeByReason.toInt(), animated)
            }

            override fun mapViewCameraIdle(mapView: NMFMapView) { onCameraIdle?.invoke() }
        })

        map.touchDelegate = object : NSObject(), NMFMapViewTouchDelegateProtocol {
            override fun mapView(mapView: NMFMapView, didTapMap: NMGLatLng, point: CValue<CGPoint>) { onMapClick?.invoke(didTapMap.toCommon()) }
            override fun mapView(mapView: NMFMapView, didTapSymbol: NMFSymbol): Boolean {
                val pos = didTapSymbol.position
                return if (pos != null) onSymbolClick?.invoke(Symbol(didTapSymbol.caption ?: "", pos.toCommon())) ?: false else false
            }
        }

        NMFLocationManager.sharedInstance()?.addDelegate(object : NSObject(), NMFLocationManagerDelegateProtocol {
            override fun locationManager(locationManager: NMFLocationManager?, didUpdateLocations: List<*>?) {
                val location = didUpdateLocations?.lastOrNull() as? platform.CoreLocation.CLLocation
                if (location != null) {
                    val latLng = LatLng(location.coordinate.useContents { latitude }, location.coordinate.useContents { longitude })
                    _lastLocation.value = latLng
                    onLocationChange?.invoke(latLng)
                }
            }
        })
    }

    actual fun addMarker(options: MarkerOptions): Marker {
        val nativeMarker = NMFMarker().apply {
            position = options.position.toNaver()
            (options.icon as? OverlayImage)?.let { iconImage = it.nativeImage }
            captionText = options.caption
            subCaptionText = options.subCaption
            alpha = options.alpha.toDouble()
            hidden = !options.isVisible
            flat = options.isFlat
            isForceShowCaption = options.isForceShowCaption
            isForceShowIcon = options.isForceShowIcon
            zIndex = options.zIndex.toLong()
            globalZIndex = options.globalZIndex.toLong()
            width = options.width.toDouble()
            height = options.height.toDouble()
            angle = options.angle.toDouble()
            anchor = CGPointMake(options.anchor.x.toDouble(), options.anchor.y.toDouble())
            minZoom = options.minZoom
            maxZoom = options.maxZoom
            isMinZoomInclusive = options.isMinZoomInclusive
            isMaxZoomInclusive = options.isMaxZoomInclusive
            captionColor = options.captionColor.toUIColor()
            captionHaloColor = options.captionHaloColor.toUIColor()
            captionTextSize = options.captionTextSize.toDouble()
            captionMinZoom = options.captionMinZoom
            captionMaxZoom = options.captionMaxZoom
            captionRequestedWidth = options.captionRequestedWidth.toDouble()
            captionOffset = options.captionOffset.toDouble()
            captionPerspectiveEnabled = options.captionPerspectiveEnabled
            subCaptionColor = options.subCaptionColor.toUIColor()
            subCaptionHaloColor = options.subCaptionHaloColor.toUIColor()
            subCaptionTextSize = options.subCaptionTextSize.toDouble()
            subCaptionMinZoom = options.subCaptionMinZoom
            subCaptionMaxZoom = options.subCaptionMaxZoom
            subCaptionRequestedWidth = options.subCaptionRequestedWidth.toDouble()
            isHideCollidedMarkers = options.isHideCollidedMarkers
            isHideCollidedSymbols = options.isHideCollidedSymbols
            isHideCollidedCaptions = options.isHideCollidedCaptions
            iconPerspectiveEnabled = options.isIconPerspectiveEnabled
            if (options.iconTintColor != 0) iconTintColor = options.iconTintColor.toUIColor()
            mapView = naverMap
        }
        val marker = Marker(nativeMarker)
        marker.tag = options.tag
        _markers.add(marker)
        return marker
    }

    actual fun removeMarker(marker: Marker) { marker.remove(); _markers.remove(marker) }
    actual fun clearMarkers() { _markers.forEach { it.remove() }; _markers.clear() }

    actual fun addPolyline(options: PolylineOptions): PolylineOverlay {
        val nativePolyline = NMFPolylineOverlay().apply {
            line = NMGLineString.lineStringWithPoints(options.coords.map { it.toNaver() })
            color = options.color.toUIColor(); width = options.width.toDouble()
            capType = options.capType.toNaver(); joinType = options.joinType.toNaver()
            zIndex = options.zIndex.toLong(); globalZIndex = options.zIndex.toLong()
            hidden = !options.isVisible; mapView = naverMap
        }
        val polyline = PolylineOverlay(nativePolyline); polyline.tag = options.tag; _polylines.add(polyline)
        return polyline
    }

    actual fun removePolyline(overlay: PolylineOverlay) { overlay.remove(); _polylines.remove(overlay) }
    actual fun clearPolylines() { _polylines.forEach { it.remove() }; _polylines.clear() }

    actual fun addPolygon(options: PolygonOptions): PolygonOverlay {
        val exteriorRing = NMGLineString.lineStringWithPoints(options.coords.map { it.toNaver() })
        val interiorRings = options.holes.map { ring -> NMGLineString.lineStringWithPoints(ring.map { it.toNaver() }) }
        val nativePolygon = NMFPolygonOverlay().apply {
            polygon = NMGPolygon.polygonWithRing(exteriorRing, interiorRings = interiorRings)
            fillColor = options.fillColor.toUIColor(); outlineColor = options.outlineColor.toUIColor()
            outlineWidth = options.outlineWidth.toULong()
            zIndex = options.zIndex.toLong(); globalZIndex = options.zIndex.toLong()
            hidden = !options.isVisible; mapView = naverMap
        }
        val polygon = PolygonOverlay(nativePolygon); polygon.tag = options.tag; _polygons.add(polygon)
        return polygon
    }

    actual fun removePolygon(overlay: PolygonOverlay) { overlay.remove(); _polygons.remove(overlay) }
    actual fun clearPolygons() { _polygons.forEach { it.remove() }; _polygons.clear() }

    actual fun addCircle(options: CircleOptions): CircleOverlay {
        val nativeCircle = NMFCircleOverlay().apply {
            center = options.center.toNaver(); radius = options.radius
            fillColor = options.fillColor.toUIColor(); outlineColor = options.outlineColor.toUIColor()
            outlineWidth = options.outlineWidth.toDouble()
            zIndex = options.zIndex.toLong(); globalZIndex = options.zIndex.toLong()
            hidden = !options.isVisible; mapView = naverMap
        }
        val circle = CircleOverlay(nativeCircle); circle.tag = options.tag; _circles.add(circle)
        return circle
    }

    actual fun removeCircle(overlay: CircleOverlay) { overlay.remove(); _circles.remove(overlay) }
    actual fun clearCircles() { _circles.forEach { it.remove() }; _circles.clear() }

    actual fun addPath(options: PathOptions): PathOverlay {
        val nativePath = NMFPath().apply {
            path = NMGLineString.lineStringWithPoints(options.coords.map { it.toNaver() })
            width = options.width.toDouble(); outlineWidth = options.outlineWidth.toDouble()
            color = options.color.toUIColor(); outlineColor = options.outlineColor.toUIColor()
            passedColor = options.passedColor.toUIColor(); passedOutlineColor = options.passedOutlineColor.toUIColor()
            progress = options.progress; patternInterval = options.patternInterval.toULong()
            isHideCollidedSymbols = options.isHideCollidedSymbols; isHideCollidedMarkers = options.isHideCollidedMarkers
            isHideCollidedCaptions = options.isHideCollidedCaptions
            zIndex = options.zIndex.toLong(); globalZIndex = options.zIndex.toLong()
            hidden = !options.isVisible; mapView = naverMap
        }
        val path = PathOverlay(nativePath); path.tag = options.tag; _paths.add(path)
        return path
    }

    actual fun removePath(overlay: PathOverlay) { overlay.remove(); _paths.remove(overlay) }
    actual fun clearPaths() { _paths.forEach { it.remove() }; _paths.clear() }

    actual fun addArrowheadPath(options: ArrowheadPathOptions): ArrowheadPathOverlay {
        val nativeArrowheadPath = NMFArrowheadPath().apply {
            points = options.coords.map { it.toNaver() }
            width = options.width.toDouble(); outlineWidth = options.outlineWidth.toDouble()
            color = options.color.toUIColor(); outlineColor = options.outlineColor.toUIColor()
            elevation = options.elevation.toDouble(); headSizeRatio = options.headSizeRatio.toDouble()
            zIndex = options.zIndex.toLong(); globalZIndex = options.zIndex.toLong()
            hidden = !options.isVisible; mapView = naverMap
        }
        val arrowheadPath = ArrowheadPathOverlay(nativeArrowheadPath); arrowheadPath.tag = options.tag; _arrowheadPaths.add(arrowheadPath)
        return arrowheadPath
    }

    actual fun removeArrowheadPath(overlay: ArrowheadPathOverlay) { overlay.remove(); _arrowheadPaths.remove(overlay) }
    actual fun clearArrowheadPaths() { _arrowheadPaths.forEach { it.remove() }; _arrowheadPaths.clear() }

    actual fun addInfoWindow(options: InfoWindowOptions, marker: Marker?): InfoWindow {
        val nativeInfoWindow = NMFInfoWindow.infoWindow().apply {
            position = options.position.toNaver(); alpha = options.alpha.toDouble()
            zIndex = options.zIndex.toLong(); globalZIndex = options.zIndex.toLong()
            offsetX = options.offsetX.toLong(); offsetY = options.offsetY.toLong()
            dataSource = object : NSObject(), NMFOverlayImageDataSourceProtocol {
                override fun viewWithOverlay(overlay: NMFOverlay): UIView {
                    val padding = 8.0
                    val label = UILabel().apply {
                        text = options.text; textColor = options.textColor.toUIColor()
                        font = UIFont.systemFontOfSize(options.textSize.toDouble()); sizeToFit()
                    }
                    val width = label.frame.useContents { size.width } + padding * 2
                    val height = label.frame.useContents { size.height } + padding * 2
                    val container = UIView().apply {
                        backgroundColor = options.backgroundColor.toUIColor()
                        layer.cornerRadius = options.cornerRadiusDp.toDouble()
                        clipsToBounds = options.cornerRadiusDp > 0
                        setFrame(CGRectMake(0.0, 0.0, width, height))
                    }
                    label.setFrame(CGRectMake(padding, padding, label.frame.useContents { size.width }, label.frame.useContents { size.height }))
                    container.addSubview(label)
                    return container
                }
            }
        }
        val infoWindow = InfoWindow(nativeInfoWindow); infoWindow.tag = options.tag
        if (marker != null) nativeInfoWindow.openWithMarker(marker.nativeMarker) else naverMap?.let { nativeInfoWindow.openWithMapView(it) }
        _infoWindows.add(infoWindow)
        return infoWindow
    }

    actual fun removeInfoWindow(infoWindow: InfoWindow) { infoWindow.remove(); _infoWindows.remove(infoWindow) }
    actual fun clearInfoWindows() { _infoWindows.forEach { it.remove() }; _infoWindows.clear() }

    actual fun clearAll() {
        clearMarkers(); clearPolylines(); clearPolygons(); clearCircles(); clearPaths(); clearArrowheadPaths(); clearInfoWindows()
    }

    actual fun animateCamera(position: CameraPosition, durationMs: Int, onFinish: (() -> Unit)?) {
        val update = NMFCameraUpdate.cameraUpdateWithPosition(position.toNaver()).apply {
            animation = NMFCameraUpdateAnimation.NMFCameraUpdateAnimationLinear
            animationDuration = durationMs.toDouble() / 1000.0
        }
        naverMap?.moveCamera(update) { isCancelled -> if (!isCancelled) onFinish?.invoke() }
    }

    actual fun fitBounds(bounds: LatLngBounds, paddingDp: Int) {
        val update = NMFCameraUpdate.cameraUpdateWithFitBounds(bounds.toNaver(), paddingDp.toDouble())
        naverMap?.moveCamera(update)
    }

    actual fun setMapType(mapType: MapType) { naverMap?.mapType = mapType.toIos() }
    actual fun setNightMode(enabled: Boolean) { naverMap?.nightModeEnabled = enabled }
    actual fun setIndoorEnabled(enabled: Boolean) { naverMap?.indoorMapEnabled = enabled }
    actual fun setBuildingHeight(height: Float) { naverMap?.buildingHeight = height }
    actual fun setSymbolScale(scale: Float) { naverMap?.symbolScale = scale.toDouble() }
}