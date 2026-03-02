package io.github.kmp.maps.naver.compose.controller

import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import io.github.kmp.maps.naver.compose.internal.dpToPx
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.MapType

class AndroidNaverMapController(
    private val naverMap: NaverMap
) : INaverMapController {
    override val cameraPosition: CameraPosition
        get() = naverMap.cameraPosition.toCommon()
    
    override val contentBounds: LatLngBounds
        get() = naverMap.contentBounds.toCommon()
    
    override val coveringTileIds: List<Long>
        get() = naverMap.coveringTileIds.map { it }

    override fun moveCamera(
        position: CameraPosition,
        animation: io.github.kmp.maps.naver.compose.model.CameraAnimation,
        durationMs: Int,
        onFinish: (() -> Unit)?
    ) {
        val update = CameraUpdate.toCameraPosition(position.toNaver())
            .animate(animation.toNaver(), durationMs.toLong())
        naverMap.moveCamera(update)
    }

    override fun fitBounds(
        bounds: LatLngBounds,
        paddingDp: Int,
        animation: io.github.kmp.maps.naver.compose.model.CameraAnimation
    ) {
        val paddingPx = paddingDp.toFloat().dpToPx().toInt()
        val update = CameraUpdate.fitBounds(bounds.toNaver(), paddingPx)
            .animate(animation.toNaver())
        naverMap.moveCamera(update)
    }

    override fun setMapType(mapType: MapType) {
        naverMap.mapType = mapType.toNaver()
    }

    override fun setNightMode(enabled: Boolean) {
        naverMap.isNightModeEnabled = enabled
    }

    override fun setIndoorEnabled(enabled: Boolean) {
        naverMap.isIndoorEnabled = enabled
    }

    override fun setBuildingHeight(height: Float) {
        naverMap.buildingHeight = height
    }

    override fun latLngToScreen(latLng: LatLng): Pair<Float, Float>? {
        val point = naverMap.projection.toScreenLocation(latLng.toNaver())
        return point.x to point.y
    }

    override fun screenToLatLng(x: Float, y: Float): LatLng? {
        val latLng = naverMap.projection.fromScreenLocation(android.graphics.PointF(x, y))
        return latLng.toCommon()
    }
}

private fun io.github.kmp.maps.naver.compose.model.CameraAnimation.toNaver(): CameraAnimation =
    when (this) {
        io.github.kmp.maps.naver.compose.model.CameraAnimation.None -> CameraAnimation.None
        io.github.kmp.maps.naver.compose.model.CameraAnimation.Linear -> CameraAnimation.Linear
        io.github.kmp.maps.naver.compose.model.CameraAnimation.Easing -> CameraAnimation.Easing
        io.github.kmp.maps.naver.compose.model.CameraAnimation.Fly -> CameraAnimation.Fly
    }
