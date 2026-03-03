package io.github.kmp.maps.naver.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.kmp.maps.naver.compose.controller.INaverMapController
import io.github.kmp.maps.naver.compose.model.Anchor
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
import io.github.kmp.maps.naver.compose.overlay.OverlayDefaults
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
 * 네이버 지도를 표시하는 메인 Composable입니다.
 * DSL 기반으로 마커, 폴리라인, 폴리곤, 원, 경로, 화살표 경로, 정보 창 등의 오버레이를
 * 선언적으로 추가할 수 있습니다.
 *
 * Main composable that displays a Naver Map. Provides a DSL-based scope
 * for declaratively adding overlays such as markers, polylines, polygons,
 * circles, paths, arrowhead paths, and info windows.
 *
 * @param modifier [Modifier] to apply to the map view.
 * @param state 지도의 상태를 관리하는 [NaverMapState].
 * @param uiSettings 줌 버튼, 나침반 등 지도 UI 설정.
 * @param locationTrackingMode 위치 추적 모드.
 * @param locationOverlayOptions 위치 오버레이 설정.
 * @param isNightModeEnabled 야간 모드 활성화 여부.
 * @param isIndoorEnabled 실내 지도 활성화 여부.
 * @param buildingHeight 건물 높이 비율 (0.0 ~ 1.0).
 * @param symbolScale 심볼 크기 비율.
 * @param onMapClick 지도 클릭 시 호출되는 콜백.
 * @param onMapReady 지도 준비 완료 시 호출되는 콜백.
 * @param content 오버레이를 선언적으로 추가할 수 있는 [NaverMapScope] DSL 블록.
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

/**
 * [NaverMap] 내부에서 오버레이를 선언적으로 추가하기 위한 DSL 스코프입니다.
 *
 * DSL scope for declaratively adding overlays inside a [NaverMap] composable.
 */
@NaverMapDsl
interface NaverMapScope {
    /**
     * 지도의 특정 상태 변화에 따라 지도를 조작할 수 있는 Side-Effect 컴포저블입니다.
     * [LaunchedEffect]를 기반으로 동작하며, 지도가 준비된 상태에서 [NaverMapState]를 receiver로 제공합니다.
     * 이를 통해 카메라 이동 등을 선언적으로 처리할 수 있습니다.
     *
     * Side-effect composable that provides [NaverMapState] as a receiver when the map is ready.
     * Based on [LaunchedEffect], allowing declarative camera moves and state manipulation.
     *
     * @param key1 효과를 재실행할 키
     * @param block 실행할 서스펜드 블록
     */
    @Composable
    fun MapEffect(key1: Any?, block: suspend NaverMapState.() -> Unit)

    /** @see [MapEffect] */
    @Composable
    fun MapEffect(key1: Any?, key2: Any?, block: suspend NaverMapState.() -> Unit)

    /** @see [MapEffect] */
    @Composable
    fun MapEffect(key1: Any?, key2: Any?, key3: Any?, block: suspend NaverMapState.() -> Unit)

    /** @see [MapEffect] */
    @Composable
    fun MapEffect(vararg keys: Any?, block: suspend NaverMapState.() -> Unit)

    /**
     * 지도 위에 마커를 표시합니다. [MarkerState]를 통해 위치를 관리합니다.
     *
     * Places a marker on the map. Position is managed via [MarkerState].
     */
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
        globalZIndex: Int = OverlayDefaults.MARKER_GLOBAL_Z_INDEX,
        width: Float = Marker.MarkerSize.AUTO,
        height: Float = Marker.MarkerSize.AUTO,
        angle: Float = 0f,
        anchor: Anchor = Anchor.CenterBottom,
        minZoom: Double = OverlayDefaults.MIN_ZOOM,
        maxZoom: Double = OverlayDefaults.MAX_ZOOM,
        isMinZoomInclusive: Boolean = true,
        isMaxZoomInclusive: Boolean = true,
        captionColor: Int = OverlayDefaults.COLOR_BLACK,
        captionHaloColor: Int = OverlayDefaults.COLOR_WHITE,
        captionTextSize: Float = 12f,
        captionMinZoom: Double = OverlayDefaults.MIN_ZOOM,
        captionMaxZoom: Double = OverlayDefaults.MAX_ZOOM,
        captionRequestedWidth: Float = 0f,
        captionOffset: Float = 0f,
        captionPerspectiveEnabled: Boolean = false,
        subCaptionColor: Int = OverlayDefaults.COLOR_BLACK,
        subCaptionHaloColor: Int = OverlayDefaults.COLOR_WHITE,
        subCaptionTextSize: Float = 10f,
        subCaptionMinZoom: Double = OverlayDefaults.MIN_ZOOM,
        subCaptionMaxZoom: Double = OverlayDefaults.MAX_ZOOM,
        subCaptionRequestedWidth: Float = 0f,
        isHideCollidedMarkers: Boolean = false,
        isHideCollidedSymbols: Boolean = false,
        isHideCollidedCaptions: Boolean = false,
        isIconPerspectiveEnabled: Boolean = false,
        iconTintColor: Int = OverlayDefaults.COLOR_TRANSPARENT,
        tag: Any? = null,
        onClick: ((Marker) -> Boolean)? = null
    )

    /**
     * 지도 위에 마커를 표시합니다. 위치를 직접 지정하는 편의 오버로드입니다.
     *
     * Places a marker at the given [position]. Convenience overload that creates
     * a [MarkerState] internally.
     */
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
        globalZIndex: Int = OverlayDefaults.MARKER_GLOBAL_Z_INDEX,
        width: Float = Marker.MarkerSize.AUTO,
        height: Float = Marker.MarkerSize.AUTO,
        angle: Float = 0f,
        anchor: Anchor = Anchor.CenterBottom,
        minZoom: Double = OverlayDefaults.MIN_ZOOM,
        maxZoom: Double = OverlayDefaults.MAX_ZOOM,
        isMinZoomInclusive: Boolean = true,
        isMaxZoomInclusive: Boolean = true,
        captionColor: Int = OverlayDefaults.COLOR_BLACK,
        captionHaloColor: Int = OverlayDefaults.COLOR_WHITE,
        captionTextSize: Float = 12f,
        captionMinZoom: Double = OverlayDefaults.MIN_ZOOM,
        captionMaxZoom: Double = OverlayDefaults.MAX_ZOOM,
        captionRequestedWidth: Float = 0f,
        captionOffset: Float = 0f,
        captionPerspectiveEnabled: Boolean = false,
        subCaptionColor: Int = OverlayDefaults.COLOR_BLACK,
        subCaptionHaloColor: Int = OverlayDefaults.COLOR_WHITE,
        subCaptionTextSize: Float = 10f,
        subCaptionMinZoom: Double = OverlayDefaults.MIN_ZOOM,
        subCaptionMaxZoom: Double = OverlayDefaults.MAX_ZOOM,
        subCaptionRequestedWidth: Float = 0f,
        isHideCollidedMarkers: Boolean = false,
        isHideCollidedSymbols: Boolean = false,
        isHideCollidedCaptions: Boolean = false,
        isIconPerspectiveEnabled: Boolean = false,
        iconTintColor: Int = OverlayDefaults.COLOR_TRANSPARENT,
        tag: Any? = null,
        onClick: ((Marker) -> Boolean)? = null
    ) = Marker(
        state = rememberMarkerState(position),
        icon = icon, caption = caption, subCaption = subCaption,
        alpha = alpha, isVisible = isVisible, isFlat = isFlat,
        isForceShowCaption = isForceShowCaption, isForceShowIcon = isForceShowIcon,
        zIndex = zIndex, globalZIndex = globalZIndex,
        width = width, height = height, angle = angle, anchor = anchor,
        minZoom = minZoom, maxZoom = maxZoom,
        isMinZoomInclusive = isMinZoomInclusive, isMaxZoomInclusive = isMaxZoomInclusive,
        captionColor = captionColor, captionHaloColor = captionHaloColor,
        captionTextSize = captionTextSize,
        captionMinZoom = captionMinZoom, captionMaxZoom = captionMaxZoom,
        captionRequestedWidth = captionRequestedWidth, captionOffset = captionOffset,
        captionPerspectiveEnabled = captionPerspectiveEnabled,
        subCaptionColor = subCaptionColor, subCaptionHaloColor = subCaptionHaloColor,
        subCaptionTextSize = subCaptionTextSize,
        subCaptionMinZoom = subCaptionMinZoom, subCaptionMaxZoom = subCaptionMaxZoom,
        subCaptionRequestedWidth = subCaptionRequestedWidth,
        isHideCollidedMarkers = isHideCollidedMarkers,
        isHideCollidedSymbols = isHideCollidedSymbols,
        isHideCollidedCaptions = isHideCollidedCaptions,
        isIconPerspectiveEnabled = isIconPerspectiveEnabled,
        iconTintColor = iconTintColor, tag = tag, onClick = onClick
    )

    /** 지도 위에 폴리라인을 표시합니다. */
    @Composable
    fun Polyline(
        coords: List<LatLng>,
        color: Int = OverlayDefaults.DEFAULT_LINE_COLOR,
        width: Float = 5f,
        pattern: List<Float> = emptyList(),
        capType: LineCap = LineCap.Round,
        joinType: LineJoin = LineJoin.Round,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((PolylineOverlay) -> Boolean)? = null
    )

    /** 지도 위에 폴리곤을 표시합니다. */
    @Composable
    fun Polygon(
        coords: List<LatLng>,
        holes: List<List<LatLng>> = emptyList(),
        fillColor: Int = OverlayDefaults.DEFAULT_FILL_COLOR,
        outlineColor: Int = OverlayDefaults.DEFAULT_LINE_COLOR,
        outlineWidth: Float = 3f,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((PolygonOverlay) -> Boolean)? = null
    )

    /** 지도 위에 원형 오버레이를 표시합니다. */
    @Composable
    fun Circle(
        center: LatLng,
        radius: Double,
        fillColor: Int = OverlayDefaults.DEFAULT_FILL_COLOR,
        outlineColor: Int = OverlayDefaults.DEFAULT_LINE_COLOR,
        outlineWidth: Float = 3f,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((CircleOverlay) -> Boolean)? = null
    )

    /** 지도 위에 경로 오버레이를 표시합니다. */
    @Composable
    fun Path(
        coords: List<LatLng>,
        width: Float = 5f,
        outlineWidth: Float = 1f,
        color: Int = OverlayDefaults.COLOR_WHITE,
        outlineColor: Int = OverlayDefaults.COLOR_BLACK,
        passedColor: Int = OverlayDefaults.DEFAULT_PASSED_COLOR,
        passedOutlineColor: Int = OverlayDefaults.DEFAULT_PASSED_OUTLINE_COLOR,
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

    /** 지도 위에 화살표 경로 오버레이를 표시합니다. */
    @Composable
    fun ArrowheadPath(
        coords: List<LatLng>,
        width: Float = 10f,
        outlineWidth: Float = 2f,
        color: Int = OverlayDefaults.COLOR_WHITE,
        outlineColor: Int = OverlayDefaults.COLOR_BLACK,
        elevation: Float = 0f,
        headSizeRatio: Float = 2.5f,
        zIndex: Int = 0,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((ArrowheadPathOverlay) -> Boolean)? = null
    )

    /** 지도 위에 정보 창을 표시합니다. */
    @Composable
    fun InfoWindow(
        position: LatLng = LatLng(0.0, 0.0),
        text: String = "",
        alpha: Float = 1f,
        zIndex: Int = OverlayDefaults.INFO_WINDOW_GLOBAL_Z_INDEX,
        anchor: Anchor = Anchor.CenterBottom,
        offsetX: Int = 0,
        offsetY: Int = 0,
        textColor: Int = OverlayDefaults.COLOR_BLACK,
        textSize: Float = 14f,
        backgroundColor: Int = OverlayDefaults.COLOR_WHITE,
        cornerRadiusDp: Float = 0f,
        isVisible: Boolean = true,
        tag: Any? = null,
        onClick: ((InfoWindow) -> Boolean)? = null
    )
}

// ──────────────────────────────────────────────────────────────────
// NaverMapScopeImpl — 각 오버레이 컴포저블의 내부 구현
// ──────────────────────────────────────────────────────────────────

internal class NaverMapScopeImpl(
    val state: NaverMapState
) : NaverMapScope {

    // ── MapEffect ───────────────────────────────────────────────

    @Composable
    override fun MapEffect(key1: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(key1) { block(state) }
    }

    @Composable
    override fun MapEffect(key1: Any?, key2: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(key1, key2) { block(state) }
    }

    @Composable
    override fun MapEffect(key1: Any?, key2: Any?, key3: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(key1, key2, key3) { block(state) }
    }

    @Composable
    override fun MapEffect(vararg keys: Any?, block: suspend NaverMapState.() -> Unit) {
        LaunchedEffect(keys) { block(state) }
    }

    // ── Marker ──────────────────────────────────────────────────

    @Composable
    override fun Marker(
        state: MarkerState, icon: Any?, caption: String, subCaption: String,
        alpha: Float, isVisible: Boolean, isFlat: Boolean,
        isForceShowCaption: Boolean, isForceShowIcon: Boolean,
        zIndex: Int, globalZIndex: Int, width: Float, height: Float,
        angle: Float, anchor: Anchor,
        minZoom: Double, maxZoom: Double,
        isMinZoomInclusive: Boolean, isMaxZoomInclusive: Boolean,
        captionColor: Int, captionHaloColor: Int, captionTextSize: Float,
        captionMinZoom: Double, captionMaxZoom: Double,
        captionRequestedWidth: Float, captionOffset: Float, captionPerspectiveEnabled: Boolean,
        subCaptionColor: Int, subCaptionHaloColor: Int, subCaptionTextSize: Float,
        subCaptionMinZoom: Double, subCaptionMaxZoom: Double, subCaptionRequestedWidth: Float,
        isHideCollidedMarkers: Boolean, isHideCollidedSymbols: Boolean, isHideCollidedCaptions: Boolean,
        isIconPerspectiveEnabled: Boolean, iconTintColor: Int, tag: Any?,
        onClick: ((Marker) -> Boolean)?
    ) {
        val resolvedIcon = when (icon) {
            is OverlayImage -> icon
            is DrawableResource -> rememberOverlayImage(icon)
            else -> null
        }

        // 라이프사이클: 마커 생성/제거
        DisposableEffect(state) {
            val options = MarkerOptions(
                position = state.position, icon = resolvedIcon, caption = caption,
                subCaption = subCaption, alpha = alpha, isVisible = isVisible, isFlat = isFlat,
                isForceShowCaption = isForceShowCaption, isForceShowIcon = isForceShowIcon,
                zIndex = zIndex, globalZIndex = globalZIndex,
                width = width, height = height, angle = angle, anchor = anchor,
                minZoom = minZoom, maxZoom = maxZoom,
                isMinZoomInclusive = isMinZoomInclusive, isMaxZoomInclusive = isMaxZoomInclusive,
                captionColor = captionColor, captionHaloColor = captionHaloColor,
                captionTextSize = captionTextSize,
                captionMinZoom = captionMinZoom, captionMaxZoom = captionMaxZoom,
                captionRequestedWidth = captionRequestedWidth, captionOffset = captionOffset,
                captionPerspectiveEnabled = captionPerspectiveEnabled,
                subCaptionColor = subCaptionColor, subCaptionHaloColor = subCaptionHaloColor,
                subCaptionTextSize = subCaptionTextSize,
                subCaptionMinZoom = subCaptionMinZoom, subCaptionMaxZoom = subCaptionMaxZoom,
                subCaptionRequestedWidth = subCaptionRequestedWidth,
                isHideCollidedMarkers = isHideCollidedMarkers,
                isHideCollidedSymbols = isHideCollidedSymbols,
                isHideCollidedCaptions = isHideCollidedCaptions,
                isIconPerspectiveEnabled = isIconPerspectiveEnabled,
                iconTintColor = iconTintColor, tag = tag
            )
            val marker = this@NaverMapScopeImpl.state.addMarker(options)
            state.marker = marker
            onDispose {
                state.marker = null
                this@NaverMapScopeImpl.state.removeMarker(marker)
            }
        }

        // 속성 변경 시 일괄 동기화
        val currentOptions = MarkerOptions(
            position = state.position, icon = resolvedIcon, caption = caption,
            subCaption = subCaption, alpha = alpha, isVisible = isVisible, isFlat = isFlat,
            isForceShowCaption = isForceShowCaption, isForceShowIcon = isForceShowIcon,
            zIndex = zIndex, globalZIndex = globalZIndex,
            width = width, height = height, angle = angle, anchor = anchor,
            minZoom = minZoom, maxZoom = maxZoom,
            isMinZoomInclusive = isMinZoomInclusive, isMaxZoomInclusive = isMaxZoomInclusive,
            captionColor = captionColor, captionHaloColor = captionHaloColor,
            captionTextSize = captionTextSize,
            captionMinZoom = captionMinZoom, captionMaxZoom = captionMaxZoom,
            captionRequestedWidth = captionRequestedWidth, captionOffset = captionOffset,
            captionPerspectiveEnabled = captionPerspectiveEnabled,
            subCaptionColor = subCaptionColor, subCaptionHaloColor = subCaptionHaloColor,
            subCaptionTextSize = subCaptionTextSize,
            subCaptionMinZoom = subCaptionMinZoom, subCaptionMaxZoom = subCaptionMaxZoom,
            subCaptionRequestedWidth = subCaptionRequestedWidth,
            isHideCollidedMarkers = isHideCollidedMarkers,
            isHideCollidedSymbols = isHideCollidedSymbols,
            isHideCollidedCaptions = isHideCollidedCaptions,
            isIconPerspectiveEnabled = isIconPerspectiveEnabled,
            iconTintColor = iconTintColor, tag = tag
        )
        LaunchedEffect(currentOptions) {
            state.marker?.applyOptions(currentOptions)
        }

        // 클릭 리스너 설정
        val currentMarker = state.marker
        LaunchedEffect(currentMarker, onClick) {
            currentMarker?.onClick { marker -> onClick?.invoke(marker) ?: false }
        }
    }

    // ── Polyline ────────────────────────────────────────────────

    @Composable
    override fun Polyline(
        coords: List<LatLng>, color: Int, width: Float, pattern: List<Float>, capType: LineCap,
        joinType: LineJoin, zIndex: Int, isVisible: Boolean, tag: Any?,
        onClick: ((PolylineOverlay) -> Boolean)?
    ) {
        val polyline = remember { mutableStateOf<PolylineOverlay?>(null) }

        DisposableEffect(pattern) {
            val options = PolylineOptions(
                coords = coords, color = color, width = width, pattern = pattern,
                capType = capType, joinType = joinType, zIndex = zIndex,
                isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addPolyline(options)
            polyline.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removePolyline(overlay)
                polyline.value = null
            }
        }

        val currentOptions = PolylineOptions(
            coords = coords, color = color, width = width, pattern = pattern,
            capType = capType, joinType = joinType, zIndex = zIndex,
            isVisible = isVisible, tag = tag
        )
        LaunchedEffect(currentOptions) {
            polyline.value?.applyOptions(currentOptions)
        }

        val currentOverlay = polyline.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    // ── Polygon ─────────────────────────────────────────────────

    @Composable
    override fun Polygon(
        coords: List<LatLng>, holes: List<List<LatLng>>, fillColor: Int, outlineColor: Int,
        outlineWidth: Float, zIndex: Int, isVisible: Boolean, tag: Any?,
        onClick: ((PolygonOverlay) -> Boolean)?
    ) {
        val polygon = remember { mutableStateOf<PolygonOverlay?>(null) }

        DisposableEffect(Unit) {
            val options = PolygonOptions(
                coords = coords, holes = holes, fillColor = fillColor,
                outlineColor = outlineColor, outlineWidth = outlineWidth,
                zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addPolygon(options)
            polygon.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removePolygon(overlay)
                polygon.value = null
            }
        }

        val currentOptions = PolygonOptions(
            coords = coords, holes = holes, fillColor = fillColor,
            outlineColor = outlineColor, outlineWidth = outlineWidth,
            zIndex = zIndex, isVisible = isVisible, tag = tag
        )
        LaunchedEffect(currentOptions) {
            polygon.value?.applyOptions(currentOptions)
        }

        val currentOverlay = polygon.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    // ── Circle ──────────────────────────────────────────────────

    @Composable
    override fun Circle(
        center: LatLng, radius: Double, fillColor: Int, outlineColor: Int, outlineWidth: Float,
        zIndex: Int, isVisible: Boolean, tag: Any?, onClick: ((CircleOverlay) -> Boolean)?
    ) {
        val circle = remember { mutableStateOf<CircleOverlay?>(null) }

        DisposableEffect(Unit) {
            val options = CircleOptions(
                center = center, radius = radius, fillColor = fillColor,
                outlineColor = outlineColor, outlineWidth = outlineWidth,
                zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addCircle(options)
            circle.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removeCircle(overlay)
                circle.value = null
            }
        }

        val currentOptions = CircleOptions(
            center = center, radius = radius, fillColor = fillColor,
            outlineColor = outlineColor, outlineWidth = outlineWidth,
            zIndex = zIndex, isVisible = isVisible, tag = tag
        )
        LaunchedEffect(currentOptions) {
            circle.value?.applyOptions(currentOptions)
        }

        val currentOverlay = circle.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    // ── Path ────────────────────────────────────────────────────

    @Composable
    override fun Path(
        coords: List<LatLng>, width: Float, outlineWidth: Float, color: Int, outlineColor: Int,
        passedColor: Int, passedOutlineColor: Int, progress: Double, patternInterval: Float,
        isHideCollidedSymbols: Boolean, isHideCollidedMarkers: Boolean,
        isHideCollidedCaptions: Boolean,
        zIndex: Int, isVisible: Boolean, tag: Any?, onClick: ((PathOverlay) -> Boolean)?
    ) {
        val path = remember { mutableStateOf<PathOverlay?>(null) }

        DisposableEffect(Unit) {
            val options = PathOptions(
                coords = coords, width = width, outlineWidth = outlineWidth,
                color = color, outlineColor = outlineColor,
                passedColor = passedColor, passedOutlineColor = passedOutlineColor,
                progress = progress, patternInterval = patternInterval,
                isHideCollidedSymbols = isHideCollidedSymbols,
                isHideCollidedMarkers = isHideCollidedMarkers,
                isHideCollidedCaptions = isHideCollidedCaptions,
                zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addPath(options)
            path.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removePath(overlay)
                path.value = null
            }
        }

        val currentOptions = PathOptions(
            coords = coords, width = width, outlineWidth = outlineWidth,
            color = color, outlineColor = outlineColor,
            passedColor = passedColor, passedOutlineColor = passedOutlineColor,
            progress = progress, patternInterval = patternInterval,
            isHideCollidedSymbols = isHideCollidedSymbols,
            isHideCollidedMarkers = isHideCollidedMarkers,
            isHideCollidedCaptions = isHideCollidedCaptions,
            zIndex = zIndex, isVisible = isVisible, tag = tag
        )
        LaunchedEffect(currentOptions) {
            path.value?.applyOptions(currentOptions)
        }

        val currentOverlay = path.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    // ── ArrowheadPath ───────────────────────────────────────────

    @Composable
    override fun ArrowheadPath(
        coords: List<LatLng>, width: Float, outlineWidth: Float, color: Int, outlineColor: Int,
        elevation: Float, headSizeRatio: Float, zIndex: Int, isVisible: Boolean, tag: Any?,
        onClick: ((ArrowheadPathOverlay) -> Boolean)?
    ) {
        val arrowheadPath = remember { mutableStateOf<ArrowheadPathOverlay?>(null) }

        DisposableEffect(Unit) {
            val options = ArrowheadPathOptions(
                coords = coords, width = width, outlineWidth = outlineWidth,
                color = color, outlineColor = outlineColor,
                elevation = elevation, headSizeRatio = headSizeRatio,
                zIndex = zIndex, isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addArrowheadPath(options)
            arrowheadPath.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removeArrowheadPath(overlay)
                arrowheadPath.value = null
            }
        }

        val currentOptions = ArrowheadPathOptions(
            coords = coords, width = width, outlineWidth = outlineWidth,
            color = color, outlineColor = outlineColor,
            elevation = elevation, headSizeRatio = headSizeRatio,
            zIndex = zIndex, isVisible = isVisible, tag = tag
        )
        LaunchedEffect(currentOptions) {
            arrowheadPath.value?.applyOptions(currentOptions)
        }

        val currentOverlay = arrowheadPath.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }

    // ── InfoWindow ──────────────────────────────────────────────

    @Composable
    override fun InfoWindow(
        position: LatLng, text: String, alpha: Float, zIndex: Int,
        anchor: Anchor, offsetX: Int, offsetY: Int,
        textColor: Int, textSize: Float, backgroundColor: Int,
        cornerRadiusDp: Float, isVisible: Boolean, tag: Any?,
        onClick: ((InfoWindow) -> Boolean)?
    ) {
        val infoWindow = remember { mutableStateOf<InfoWindow?>(null) }

        DisposableEffect(Unit) {
            val options = InfoWindowOptions(
                position = position, text = text, alpha = alpha, zIndex = zIndex,
                anchor = anchor, offsetX = offsetX, offsetY = offsetY,
                textColor = textColor, textSize = textSize,
                backgroundColor = backgroundColor, cornerRadiusDp = cornerRadiusDp,
                isVisible = isVisible, tag = tag
            )
            val overlay = this@NaverMapScopeImpl.state.addInfoWindow(options)
            infoWindow.value = overlay
            onDispose {
                this@NaverMapScopeImpl.state.removeInfoWindow(overlay)
                infoWindow.value = null
            }
        }

        val currentOptions = InfoWindowOptions(
            position = position, text = text, alpha = alpha, zIndex = zIndex,
            anchor = anchor, offsetX = offsetX, offsetY = offsetY,
            textColor = textColor, textSize = textSize,
            backgroundColor = backgroundColor, cornerRadiusDp = cornerRadiusDp,
            isVisible = isVisible, tag = tag
        )
        LaunchedEffect(currentOptions) {
            infoWindow.value?.applyOptions(currentOptions)
        }

        val currentOverlay = infoWindow.value
        LaunchedEffect(currentOverlay, onClick) {
            currentOverlay?.onClick { overlay -> onClick?.invoke(overlay) ?: false }
        }
    }
}
