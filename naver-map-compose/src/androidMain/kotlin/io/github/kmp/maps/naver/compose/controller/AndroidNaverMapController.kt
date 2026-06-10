package io.github.kmp.maps.naver.compose.controller

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
        if (onFinish != null) {
            // 임의의 idle이 아니라 이 카메라 업데이트가 정상 완료됐을 때만 호출한다.
            // (취소 시에는 호출되지 않음 → iOS의 moveCamera(update){ isCancelled }와 동일)
            update.finishCallback { onFinish() }
        }
        naverMap.moveCamera(update)
    }

    override fun fitBounds(
        bounds: LatLngBounds,
        paddingDp: Int,
        animation: io.github.kmp.maps.naver.compose.model.CameraAnimation,
        durationMs: Int
    ) {
        val paddingPx = paddingDp.toFloat().dpToPx().toInt()
        val update = CameraUpdate.fitBounds(bounds.toNaver(), paddingPx)
            .animate(animation.toNaver(), durationMs.toLong())
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
        // 투영 불가(틸트 상태에서 지평선 너머 등) 시 SDK는 NaN 좌표를 반환한다.
        if (point.x.isNaN() || point.y.isNaN()) return null
        return point.x to point.y
    }

    override fun screenToLatLng(x: Float, y: Float): LatLng? {
        val latLng = naverMap.projection.fromScreenLocation(android.graphics.PointF(x, y))
        // 투영 불가 좌표는 LatLng.INVALID(NaN)로 반환되므로 null로 변환한다.
        if (latLng.latitude.isNaN() || latLng.longitude.isNaN()) return null
        return latLng.toCommon()
    }
}
