package io.github.kmp.maps.naver.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.kmp.maps.naver.compose.controller.INaverMapController
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LocationTrackingMode
import io.github.kmp.maps.naver.compose.options.ArrowheadPathOptions
import io.github.kmp.maps.naver.compose.options.CircleOptions
import io.github.kmp.maps.naver.compose.options.InfoWindowOptions
import io.github.kmp.maps.naver.compose.options.LineCap
import io.github.kmp.maps.naver.compose.options.LineJoin
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
import io.github.kmp.maps.naver.compose.overlay.OverlayImage
import io.github.kmp.maps.naver.compose.overlay.PathOverlay
import io.github.kmp.maps.naver.compose.overlay.PolygonOverlay
import io.github.kmp.maps.naver.compose.overlay.PolylineOverlay
import io.github.kmp.maps.naver.compose.overlay.rememberOverlayImage
import io.github.kmp.maps.naver.compose.state.MarkerState
import io.github.kmp.maps.naver.compose.state.NaverMapState
import io.github.kmp.maps.naver.compose.state.rememberMarkerState
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import org.jetbrains.compose.resources.DrawableResource

/**
 * [NaverMap]은 네이버 지도를 표시하는 Composable입니다.
 * DSL 기반으로 마커, 폴리라인 등의 오버레이를 선언적으로 추가할 수 있습니다.
 */
@Composable
fun NaverMap(
    modifier: Modifier = Modifier,
    state: NaverMapState = rememberNaverMapState(),
    uiSettings: MapUiSettings = MapUiSettings(),
    locationTrackingMode: LocationTrackingMode = LocationTrackingMode.None,
    locationOverlayOptions: LocationOverlayOptions = LocationOverlayOptions(),
    isNightModeEnabled: Boolean = false,
    isIndoorEnabled: Boolean = false,
    buildingHeight: Float = 1f,
    symbolScale: Float = 1f,
    onMapClick: ((LatLng) -> Unit)? = null,
    onMapReady: (INaverMapController) -> Unit = {},
    content: @Composable NaverMapScope.() -> Unit = {}
) {
    // 상태 동기화
    LaunchedEffect(uiSettings) { state.uiSettings = uiSettings }
    LaunchedEffect(locationTrackingMode) { state.locationTrackingMode = locationTrackingMode }
    LaunchedEffect(locationOverlayOptions) { state.locationOverlayOptions = locationOverlayOptions }
    LaunchedEffect(isNightModeEnabled) { state.setNightMode(isNightModeEnabled) }
    LaunchedEffect(isIndoorEnabled) { state.setIndoorEnabled(isIndoorEnabled) }
    LaunchedEffect(buildingHeight) { state.setBuildingHeight(buildingHeight) }
    LaunchedEffect(symbolScale) { state.setSymbolScale(symbolScale) }
    LaunchedEffect(onMapClick) { state.onMapClick = onMapClick }

    NaverMapView(
        modifier = modifier,
        state = state,
        onMapReady = { controller ->
            onMapReady(controller)
        }
    )

    // 지도가 준비되었을 때만 오버레이들을 추가함
    if (state.isMapReady) {
        val scope = remember(state) { NaverMapScopeImpl(state) }
        scope.content()
    }
}

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class NaverMapDsl

@NaverMapDsl
interface NaverMapScope {
    /**
     * 지도의 특정 상태 변화에 따라 지도를 조작할 수 있는 Side-Effect 컴포저블입니다.
     * [LaunchedEffect]를 기반으로 동작하며, 지도가 준비된 상태에서 [NaverMapState]를 receiver로 제공합니다.
     * 이를 통해 카메라 이동 등을 선언적으로 처리할 수 있습니다.
     *
     * @param key1 효과를 재실행할 키
     * @param block 실행할 서스펜드 블록
     */
    @Composable
    fun MapEffect(key1: Any?, block: suspend NaverMapState.() -> Unit)

    /**
     * @see [MapEffect]
     */
    @Composable
    fun MapEffect(key1: Any?, key2: Any?, block: suspend NaverMapState.() -> Unit)

    /**
     * @see [MapEffect]
     */
    @Composable
    fun MapEffect(key1: Any?, key2: Any?, key3: Any?, block: suspend NaverMapState.() -> Unit)

    /**
     * @see [MapEffect]
     */
    @Composable
    fun MapEffect(vararg keys: Any?, block: suspend NaverMapState.() -> Unit)

    @Composable
    fun Marker(
        state: MarkerState,
        icon: Any? = null,
        caption: String = "",
        subCaption: String = "",
        alpha: Float = 1f,
        isVisible: Boolean = true,
        isFlat: Boolean = false,
        isForceShowCaption: Boolean = false,
        isForceShowIcon: Boolean = false,
        zIndex: Int = 0,
        globalZIndex: Int = 200000,
        width: Float = Marker.MarkerSize.AUTO,
        height: Float = Marker.MarkerSize.AUTO,
        angle: Float = 0f,
        anchor: Pair<Float, Float> = Pair(0.5f, 1f),
        minZoom: Double = 0.0,
        maxZoom: Double = 21.0,
        isMinZoomInclusive: Boolean = true,
        isMaxZoomInclusive: Boolean = true,
        captionColor: Int = 0xFF000000.toInt(),
        captionHaloColor: Int = 0xFFFFFFFF.toInt(),
        captionTextSize: Float = 12f,
        captionMinZoom: Double = 0.0,
        captionMaxZoom: Double = 21.0,
        captionRequestedWidth: Float = 0f,
        captionOffset: Float = 0f,
        captionPerspectiveEnabled: Boolean = false,
        subCaptionColor: Int = 0xFF000000.toInt(),
        subCaptionHaloColor: Int = 0xFFFFFFFF.toInt(),
        subCaptionTextSize: Float = 10f,
        subCaptionMinZoom: Double = 0.0,
        subCaptionMaxZoom: Double = 21.0,
        subCaptionRequestedWidth: Float = 0f,
        isHideCollidedMarkers: Boolean = false,
        isHideCollidedSymbols: Boolean = false,
        isHideCollidedCaptions: Boolean = false,
        isIconPerspectiveEnabled: Boolean = false,
        iconTintColor: Int = 0x00000000,
        tag: Any? = null,
        onClick: ((Marker) -> Boolean)? = null
    )

    @Composable
    fun Marker(
        position: LatLng,
        icon: Any? = null,
        caption: String = "",
        subCaption: String = "",
        alpha: Float = 1f,
        isVisible: Boolean = true,
        isFlat: Boolean = false,
        isForceShowCaption: Boolean = false,
        isForceShowIcon: Boolean = false,
        zIndex: Int = 0,
        globalZIndex: Int = 200000,
        width: Float = Marker.MarkerSize.AUTO,
        height: Float = Marker.MarkerSize.AUTO,
        angle: Float = 0f,
        anchor: Pair<Float, Float> = Pair(0.5f, 1f),
        minZoom: Double = 0.0,
        maxZoom: Double = 21.0,
        isMinZoomInclusive: Boolean = true,
        isMaxZoomInclusive: Boolean = true,
        captionColor: Int = 0xFF000000.toInt(),
        captionHaloColor: Int = 0xFFFFFFFF.toInt(),
        captionTextSize: Float = 12f,
        captionMinZoom: Double = 0.0,
        captionMaxZoom: Double = 21.0,
        captionRequestedWidth: Float = 0f,
        captionOffset: Float = 0f,
        captionPerspectiveEnabled: Boolean = false,
        subCaptionColor: Int = 0xFF000000.toInt(),
        subCaptionHaloColor: Int = 0xFFFFFFFF.toInt(),
        subCaptionTextSize: Float = 10f,
        subCaptionMinZoom: Double = 0.0,
        subCaptionMaxZoom: Double = 21.0,
        subCaptionRequestedWidth: Float = 0f,
        isHideCollidedMarkers: Boolean = false,
        isHideCollidedSymbols: Boolean = false,
        isHideCollidedCaptions: Boolean = false,
        isIconPerspectiveEnabled: Boolean = false,
        iconTintColor: Int = 0x00000000,
        tag: Any? = null,
        onClick: ((Marker) -> Boolean)? = null
    ) = Marker(
        state = rememberMarkerState(position),
        icon = icon,
        caption = caption,
        subCaption = subCaption,
        alpha = alpha,
        isVisible = isVisible,
        isFlat = isFlat,
        isForceShowCaption = isForceShowCaption,
        isForceShowIcon = isForceShowIcon,
        zIndex = zIndex,
        globalZIndex = globalZIndex,
        width = width,
        height = height,
        angle = angle,
        anchor = anchor,
        minZoom = minZoom,
        maxZoom = maxZoom,
        isMinZoomInclusive = isMinZoomInclusive,
        isMaxZoomInclusive = isMaxZoomInclusive,
        captionColor = captionColor,
        captionHaloColor = captionHaloColor,
        captionTextSize = captionTextSize,
        captionMinZoom = captionMinZoom,
        captionMaxZoom = captionMaxZoom,
        captionRequestedWidth = captionRequestedWidth,
        captionOffset = captionOffset,
        captionPerspectiveEnabled = captionPerspectiveEnabled,
        subCaptionColor = subCaptionColor,
        subCaptionHaloColor = subCaptionHaloColor,
        subCaptionTextSize = subCaptionTextSize,
        subCaptionMinZoom = subCaptionMinZoom,
        subCaptionMaxZoom = subCaptionMaxZoom,
        subCaptionRequestedWidth = subCaptionRequestedWidth,
        isHideCollidedMarkers = isHideCollidedMarkers,
        isHideCollidedSymbols = isHideCollidedSymbols,
        isHideCollidedCaptions = isHideCollidedCaptions,
        isIconPerspectiveEnabled = isIconPerspectiveEnabled,
        iconTintColor = iconTintColor,
        tag = tag,
        onClick = onClick
    )

    @Composable
    fun Polyline(
        coords: List<LatLng>,
        color: Int = 0xFF0000FF.toInt(),
        width: Float = 5f,
        pattern: List<Float> = emptyList(),
        capType: LineCap = LineCap.Round,
        joinType: LineJoin = LineJoin.Round,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((PolylineOverlay) -> Boolean)? = null
    )

    @Composable
    fun Polygon(
        coords: List<LatLng>,
        holes: List<List<LatLng>> = emptyList(),
        fillColor: Int = 0x7F0000FF,
        outlineColor: Int = 0xFF0000FF.toInt(),
        outlineWidth: Float = 3f,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((PolygonOverlay) -> Boolean)? = null
    )

    @Composable
    fun Circle(
        center: LatLng,
        radius: Double,
        fillColor: Int = 0x7F0000FF,
        outlineColor: Int = 0xFF0000FF.toInt(),
        outlineWidth: Float = 3f,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((CircleOverlay) -> Boolean)? = null
    )

    @Composable
    fun Path(
        coords: List<LatLng>,
        width: Float = 5f,
        outlineWidth: Float = 1f,
        color: Int = 0xFFFFFFFF.toInt(),
        outlineColor: Int = 0xFF000000.toInt(),
        passedColor: Int = 0xFF888888.toInt(),
        passedOutlineColor: Int = 0xFF444444.toInt(),
        progress: Double = 0.0,
        patternInterval: Float = 0f,
        isHideCollidedSymbols: Boolean = false,
        isHideCollidedMarkers: Boolean = false,
        isHideCollidedCaptions: Boolean = false,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((PathOverlay) -> Boolean)? = null
    )

    @Composable
    fun ArrowheadPath(
        coords: List<LatLng>,
        width: Float = 10f,
        outlineWidth: Float = 2f,
        color: Int = 0xFFFFFFFF.toInt(),
        outlineColor: Int = 0xFF000000.toInt(),
        elevation: Float = 0f,
        headSizeRatio: Float = 2.5f,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((ArrowheadPathOverlay) -> Boolean)? = null
    )

    @Composable
    fun InfoWindow(
        position: LatLng = LatLng(0.0, 0.0),
        text: String = "",
        alpha: Float = 1f,
        zIndex: Int = 400000,
        anchor: Pair<Float, Float> = Pair(0.5f, 1f),
        offsetX: Int = 0,
        offsetY: Int = 0,
        textColor: Int = 0xFF000000.toInt(),
        textSize: Float = 14f,
        backgroundColor: Int = 0xFFFFFFFF.toInt(),
        cornerRadiusDp: Float = 0f,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((InfoWindow) -> Boolean)? = null
    )
}

internal class NaverMapScopeImpl(
    val state: NaverMapState
) : NaverMapScope {

    @Composable
    override fun MapEffect(key1: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(key1) {
            block(state)
        }
    }

    @Composable
    override fun MapEffect(key1: Any?, key2: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(key1, key2) {
            block(state)
        }
    }

    @Composable
    override fun MapEffect(key1: Any?, key2: Any?, key3: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(key1, key2, key3) {
            block(state)
        }
    }

    @Composable
    override fun MapEffect(vararg keys: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(keys) {
            block(state)
        }
    }

    @Composable
    override fun Marker(
        state: MarkerState,
        icon: Any?,
        caption: String,
        subCaption: String,
        alpha: Float,
        isVisible: Boolean,
        isFlat: Boolean,
        isForceShowCaption: Boolean,
        isForceShowIcon: Boolean,
        zIndex: Int,
        globalZIndex: Int,
        width: Float,
        height: Float,
        angle: Float,
        anchor: Pair<Float, Float>,
        minZoom: Double,
        maxZoom: Double,
        isMinZoomInclusive: Boolean,
        isMaxZoomInclusive: Boolean,
        captionColor: Int,
        captionHaloColor: Int,
        captionTextSize: Float,
        captionMinZoom: Double,
        captionMaxZoom: Double,
        captionRequestedWidth: Float,
        captionOffset: Float,
        captionPerspectiveEnabled: Boolean,
        subCaptionColor: Int,
        subCaptionHaloColor: Int,
        subCaptionTextSize: Float,
        subCaptionMinZoom: Double,
        subCaptionMaxZoom: Double,
        subCaptionRequestedWidth: Float,
        isHideCollidedMarkers: Boolean,
        isHideCollidedSymbols: Boolean,
        isHideCollidedCaptions: Boolean,
        isIconPerspectiveEnabled: Boolean,
        iconTintColor: Int,
        tag: Any?,
        onClick: ((Marker) -> Boolean)?
    ) {
        val resolvedIcon = when (icon) {
            is OverlayImage -> icon
            is DrawableResource -> rememberOverlayImage(icon)
            else -> null
        }

        DisposableEffect(state) {
            val options = MarkerOptions(
                position = state.position, icon = resolvedIcon, caption = caption, subCaption = subCaption,
                alpha = alpha, isVisible = isVisible, isFlat = isFlat, isForceShowCaption = isForceShowCaption,
                isForceShowIcon = isForceShowIcon, zIndex = zIndex, globalZIndex = globalZIndex,
                width = width, height = height, angle = angle, anchor = anchor, minZoom = minZoom, maxZoom = maxZoom,
                isMinZoomInclusive = isMinZoomInclusive, isMaxZoomInclusive = isMaxZoomInclusive,
                captionColor = captionColor, captionHaloColor = captionHaloColor, captionTextSize = captionTextSize,
                captionMinZoom = captionMinZoom, captionMaxZoom = captionMaxZoom,
                captionRequestedWidth = captionRequestedWidth, captionOffset = captionOffset,
                captionPerspectiveEnabled = captionPerspectiveEnabled, subCaptionColor = subCaptionColor,
                subCaptionHaloColor = subCaptionHaloColor, subCaptionTextSize = subCaptionTextSize,
                subCaptionMinZoom = subCaptionMinZoom, subCaptionMaxZoom = subCaptionMaxZoom,
                subCaptionRequestedWidth = subCaptionRequestedWidth, isHideCollidedMarkers = isHideCollidedMarkers,
                isHideCollidedSymbols = isHideCollidedSymbols, isHideCollidedCaptions = isHideCollidedCaptions,
                isIconPerspectiveEnabled = isIconPerspectiveEnabled, iconTintColor = iconTintColor, tag = tag
            )
            val marker = this@NaverMapScopeImpl.state.addMarker(options)
            state.marker = marker
            onDispose {
                state.marker = null
                this@NaverMapScopeImpl.state.removeMarker(marker)
            }
        }

        LaunchedEffect(state.position) { state.marker?.position = state.position }
        LaunchedEffect(resolvedIcon) { state.marker?.icon = resolvedIcon }
        LaunchedEffect(caption) { state.marker?.caption = caption }
        LaunchedEffect(subCaption) { state.marker?.subCaption = subCaption }
        LaunchedEffect(alpha) { state.marker?.alpha = alpha }
        LaunchedEffect(isVisible) { state.marker?.isVisible = isVisible }
        LaunchedEffect(isFlat) { state.marker?.isFlat = isFlat }
        LaunchedEffect(isForceShowCaption) { state.marker?.isForceShowCaption = isForceShowCaption }
        LaunchedEffect(isForceShowIcon) { state.marker?.isForceShowIcon = isForceShowIcon }
        LaunchedEffect(zIndex) { state.marker?.zIndex = zIndex }
        LaunchedEffect(globalZIndex) { state.marker?.globalZIndex = globalZIndex }
        LaunchedEffect(width) { state.marker?.width = width }
        LaunchedEffect(height) { state.marker?.height = height }
        LaunchedEffect(angle) { state.marker?.angle = angle }
        LaunchedEffect(anchor) { state.marker?.anchor = anchor }
        LaunchedEffect(minZoom) { state.marker?.minZoom = minZoom }
        LaunchedEffect(maxZoom) { state.marker?.maxZoom = maxZoom }
        LaunchedEffect(isMinZoomInclusive) { state.marker?.isMinZoomInclusive = isMinZoomInclusive }
        LaunchedEffect(isMaxZoomInclusive) { state.marker?.isMaxZoomInclusive = isMaxZoomInclusive }
        LaunchedEffect(captionColor) { state.marker?.captionColor = captionColor }
        LaunchedEffect(captionHaloColor) { state.marker?.captionHaloColor = captionHaloColor }
        LaunchedEffect(captionTextSize) { state.marker?.captionTextSize = captionTextSize }
        LaunchedEffect(captionMinZoom) { state.marker?.captionMinZoom = captionMinZoom }
        LaunchedEffect(captionMaxZoom) { state.marker?.captionMaxZoom = captionMaxZoom }
        LaunchedEffect(captionRequestedWidth) { state.marker?.captionRequestedWidth = captionRequestedWidth }
        LaunchedEffect(captionOffset) { state.marker?.captionOffset = captionOffset }
        LaunchedEffect(captionPerspectiveEnabled) { state.marker?.captionPerspectiveEnabled = captionPerspectiveEnabled }
        LaunchedEffect(subCaptionColor) { state.marker?.subCaptionColor = subCaptionColor }
        LaunchedEffect(subCaptionHaloColor) { state.marker?.subCaptionHaloColor = subCaptionHaloColor }
        LaunchedEffect(subCaptionTextSize) { state.marker?.subCaptionTextSize = subCaptionTextSize }
        LaunchedEffect(subCaptionMinZoom) { state.marker?.subCaptionMinZoom = subCaptionMinZoom }
        LaunchedEffect(subCaptionMaxZoom) { state.marker?.subCaptionMaxZoom = subCaptionMaxZoom }
        LaunchedEffect(subCaptionRequestedWidth) { state.marker?.subCaptionRequestedWidth = subCaptionRequestedWidth }
        LaunchedEffect(isHideCollidedMarkers) { state.marker?.isHideCollidedMarkers = isHideCollidedMarkers }
        LaunchedEffect(isHideCollidedSymbols) { state.marker?.isHideCollidedSymbols = isHideCollidedSymbols }
        LaunchedEffect(isHideCollidedCaptions) { state.marker?.isHideCollidedCaptions = isHideCollidedCaptions }
        LaunchedEffect(isIconPerspectiveEnabled) { state.marker?.isIconPerspectiveEnabled = isIconPerspectiveEnabled }
        LaunchedEffect(iconTintColor) { state.marker?.iconTintColor = iconTintColor }
        LaunchedEffect(tag) { state.marker?.tag = tag }
        
        val currentMarker = state.marker
        LaunchedEffect(currentMarker, onClick) {
            currentMarker?.onClick { marker -> onClick?.invoke(marker) ?: false }
        }
    }

    @Composable
    override fun Polyline(
        coords: List<LatLng>, color: Int, width: Float, pattern: List<Float>, capType: LineCap,
        joinType: LineJoin, zIndex: Int, isVisible: Boolean, tag: Any?, onClick: ((PolylineOverlay) -> Boolean)?
    ) {
        val polyline = remember { mutableStateOf<PolylineOverlay?>(null) }
        DisposableEffect(pattern) {
            val options = PolylineOptions(
                coords = coords, color = color, width = width, pattern = pattern,
                capType = capType, joinType = joinType, zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addPolyline(options)
            polyline.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removePolyline(overlay)
                polyline.value = null
            }
        }
        LaunchedEffect(coords) { polyline.value?.coords = coords }
        LaunchedEffect(color) { polyline.value?.color = color }
        LaunchedEffect(width) { polyline.value?.width = width }
        LaunchedEffect(capType) { polyline.value?.capType = capType }
        LaunchedEffect(joinType) { polyline.value?.joinType = joinType }
        LaunchedEffect(zIndex) { polyline.value?.zIndex = zIndex }
        LaunchedEffect(isVisible) { polyline.value?.isVisible = isVisible }
        LaunchedEffect(tag) { polyline.value?.tag = tag }
        
        val currentOverlay = polyline.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    @Composable
    override fun Polygon(
        coords: List<LatLng>, holes: List<List<LatLng>>, fillColor: Int, outlineColor: Int,
        outlineWidth: Float, zIndex: Int, isVisible: Boolean, tag: Any?, onClick: ((PolygonOverlay) -> Boolean)?
    ) {
        val polygon = remember { mutableStateOf<PolygonOverlay?>(null) }
        DisposableEffect(Unit) {
            val options = PolygonOptions(
                coords = coords, holes = holes, fillColor = fillColor, outlineColor = outlineColor,
                outlineWidth = outlineWidth, zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addPolygon(options)
            polygon.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removePolygon(overlay)
                polygon.value = null
            }
        }
        LaunchedEffect(coords) { polygon.value?.coords = coords }
        LaunchedEffect(holes) { polygon.value?.holes = holes }
        LaunchedEffect(fillColor) { polygon.value?.fillColor = fillColor }
        LaunchedEffect(outlineColor) { polygon.value?.outlineColor = outlineColor }
        LaunchedEffect(outlineWidth) { polygon.value?.outlineWidth = outlineWidth }
        LaunchedEffect(zIndex) { polygon.value?.zIndex = zIndex }
        LaunchedEffect(isVisible) { polygon.value?.isVisible = isVisible }
        LaunchedEffect(tag) { polygon.value?.tag = tag }
        
        val currentOverlay = polygon.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    @Composable
    override fun Circle(
        center: LatLng, radius: Double, fillColor: Int, outlineColor: Int, outlineWidth: Float,
        zIndex: Int, isVisible: Boolean, tag: Any?, onClick: ((CircleOverlay) -> Boolean)?
    ) {
        val circle = remember { mutableStateOf<CircleOverlay?>(null) }
        DisposableEffect(Unit) {
            val options = CircleOptions(
                center = center, radius = radius, fillColor = fillColor, outlineColor = outlineColor,
                outlineWidth = outlineWidth, zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addCircle(options)
            circle.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removeCircle(overlay)
                circle.value = null
            }
        }
        LaunchedEffect(center) { circle.value?.center = center }
        LaunchedEffect(radius) { circle.value?.radius = radius }
        LaunchedEffect(fillColor) { circle.value?.fillColor = fillColor }
        LaunchedEffect(outlineColor) { circle.value?.outlineColor = outlineColor }
        LaunchedEffect(outlineWidth) { circle.value?.outlineWidth = outlineWidth }
        LaunchedEffect(zIndex) { circle.value?.zIndex = zIndex }
        LaunchedEffect(isVisible) { circle.value?.isVisible = isVisible }
        LaunchedEffect(tag) { circle.value?.tag = tag }
        
        val currentOverlay = circle.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    @Composable
    override fun Path(
        coords: List<LatLng>, width: Float, outlineWidth: Float, color: Int, outlineColor: Int,
        passedColor: Int, passedOutlineColor: Int, progress: Double, patternInterval: Float,
        isHideCollidedSymbols: Boolean, isHideCollidedMarkers: Boolean, isHideCollidedCaptions: Boolean,
        zIndex: Int, isVisible: Boolean, tag: Any?, onClick: ((PathOverlay) -> Boolean)?
    ) {
        val path = remember { mutableStateOf<PathOverlay?>(null) }
        DisposableEffect(Unit) {
            val options = PathOptions(
                coords = coords, width = width, outlineWidth = outlineWidth, color = color,
                outlineColor = outlineColor, passedColor = passedColor, passedOutlineColor = passedOutlineColor,
                progress = progress, patternInterval = patternInterval, isHideCollidedSymbols = isHideCollidedSymbols,
                isHideCollidedMarkers = isHideCollidedMarkers, isHideCollidedCaptions = isHideCollidedCaptions,
                zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addPath(options)
            path.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removePath(overlay)
                path.value = null
            }
        }
        LaunchedEffect(coords) { path.value?.coords = coords }
        LaunchedEffect(width) { path.value?.width = width }
        LaunchedEffect(outlineWidth) { path.value?.outlineWidth = outlineWidth }
        LaunchedEffect(color) { path.value?.color = color }
        LaunchedEffect(outlineColor) { path.value?.outlineColor = outlineColor }
        LaunchedEffect(passedColor) { path.value?.passedColor = passedColor }
        LaunchedEffect(passedOutlineColor) { path.value?.passedOutlineColor = passedOutlineColor }
        LaunchedEffect(progress) { path.value?.progress = progress }
        LaunchedEffect(patternInterval) { path.value?.patternInterval = patternInterval }
        LaunchedEffect(isHideCollidedSymbols) { path.value?.isHideCollidedSymbols = isHideCollidedSymbols }
        LaunchedEffect(isHideCollidedMarkers) { path.value?.isHideCollidedMarkers = isHideCollidedMarkers }
        LaunchedEffect(isHideCollidedCaptions) { path.value?.isHideCollidedCaptions = isHideCollidedCaptions }
        LaunchedEffect(zIndex) { path.value?.zIndex = zIndex }
        LaunchedEffect(isVisible) { path.value?.isVisible = isVisible }
        LaunchedEffect(tag) { path.value?.tag = tag }
        
        val currentOverlay = path.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    @Composable
    override fun ArrowheadPath(
        coords: List<LatLng>, width: Float, outlineWidth: Float, color: Int, outlineColor: Int,
        elevation: Float, headSizeRatio: Float, zIndex: Int, isVisible: Boolean, tag: Any?,
        onClick: ((ArrowheadPathOverlay) -> Boolean)?
    ) {
        val arrowheadPath = remember { mutableStateOf<ArrowheadPathOverlay?>(null) }
        DisposableEffect(Unit) {
            val options = ArrowheadPathOptions(
                coords = coords, width = width, outlineWidth = outlineWidth, color = color,
                outlineColor = outlineColor, elevation = elevation, headSizeRatio = headSizeRatio,
                zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addArrowheadPath(options)
            arrowheadPath.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removeArrowheadPath(overlay)
                arrowheadPath.value = null
            }
        }
        LaunchedEffect(coords) { arrowheadPath.value?.coords = coords }
        LaunchedEffect(width) { arrowheadPath.value?.width = width }
        LaunchedEffect(outlineWidth) { arrowheadPath.value?.outlineWidth = outlineWidth }
        LaunchedEffect(color) { arrowheadPath.value?.color = color }
        LaunchedEffect(outlineColor) { arrowheadPath.value?.outlineColor = outlineColor }
        LaunchedEffect(elevation) { arrowheadPath.value?.elevation = elevation }
        LaunchedEffect(headSizeRatio) { arrowheadPath.value?.headSizeRatio = headSizeRatio }
        LaunchedEffect(zIndex) { arrowheadPath.value?.zIndex = zIndex }
        LaunchedEffect(isVisible) { arrowheadPath.value?.isVisible = isVisible }
        LaunchedEffect(tag) { arrowheadPath.value?.tag = tag }
        
        val currentOverlay = arrowheadPath.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    @Composable
    override fun InfoWindow(
        position: LatLng, text: String, alpha: Float, zIndex: Int, anchor: Pair<Float, Float>,
        offsetX: Int, offsetY: Int, textColor: Int, textSize: Float, backgroundColor: Int,
        cornerRadiusDp: Float, isVisible: Boolean, tag: Any?, onClick: ((InfoWindow) -> Boolean)?
    ) {
        val infoWindow = remember { mutableStateOf<InfoWindow?>(null) }
        DisposableEffect(Unit) {
            val options = InfoWindowOptions(
                position = position, text = text, alpha = alpha, zIndex = zIndex,
                anchor = anchor, offsetX = offsetX, offsetY = offsetY, textColor = textColor,
                textSize = textSize, backgroundColor = backgroundColor, cornerRadiusDp = cornerRadiusDp,
                isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addInfoWindow(options)
            infoWindow.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removeInfoWindow(overlay)
                infoWindow.value = null
            }
        }
        LaunchedEffect(position) { infoWindow.value?.position = position }
        LaunchedEffect(text) { infoWindow.value?.text = text }
        LaunchedEffect(alpha) { infoWindow.value?.alpha = alpha }
        LaunchedEffect(zIndex) { infoWindow.value?.zIndex = zIndex }
        LaunchedEffect(anchor) { infoWindow.value?.anchor = anchor }
        LaunchedEffect(offsetX) { infoWindow.value?.offsetX = offsetX }
        LaunchedEffect(offsetY) { infoWindow.value?.offsetY = offsetY }
        LaunchedEffect(textColor) { infoWindow.value?.textColor = textColor }
        LaunchedEffect(textSize) { infoWindow.value?.textSize = textSize }
        LaunchedEffect(backgroundColor) { infoWindow.value?.backgroundColor = backgroundColor }
        LaunchedEffect(cornerRadiusDp) { infoWindow.value?.cornerRadiusDp = cornerRadiusDp }
        LaunchedEffect(isVisible) { infoWindow.value?.isVisible = isVisible }
        LaunchedEffect(tag) { infoWindow.value?.tag = tag }
        
        val currentOverlay = infoWindow.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }
}
