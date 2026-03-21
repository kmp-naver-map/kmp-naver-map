@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.controller

import cocoapods.NMapsMap.NMFCameraUpdate
import cocoapods.NMapsMap.NMFCameraUpdateAnimation
import cocoapods.NMapsMap.NMFMapView
import io.github.kmp.maps.naver.compose.internal.toCommon
import io.github.kmp.maps.naver.compose.internal.toIos
import io.github.kmp.maps.naver.compose.internal.toNaver
import io.github.kmp.maps.naver.compose.model.CameraAnimation
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.MapType
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake

class IosNaverMapController(
    private val mapView: NMFMapView
) : INaverMapController {
    override val cameraPosition: CameraPosition
        get() = mapView.cameraPosition.toCommon()

    override val contentBounds: LatLngBounds
        get() = mapView.contentBounds.toCommon()

    override val coveringTileIds: List<Long>
        get() = mapView.getCoveringTileIds().map { (it as Number).toLong() }

    override fun moveCamera(
        position: CameraPosition,
        animation: CameraAnimation,
        durationMs: Int,
        onFinish: (() -> Unit)?
    ) {
        val update = NMFCameraUpdate.cameraUpdateWithPosition(position.toNaver())
        update.animation = animation.toIos()
        update.animationDuration = durationMs.toDouble() / 1000.0

        mapView.moveCamera(update) { isCancelled ->
            if (!isCancelled) onFinish?.invoke()
        }
    }

    override fun fitBounds(
        bounds: LatLngBounds,
        paddingDp: Int,
        animation: CameraAnimation,
        durationMs: Int
    ) {
        val update = NMFCameraUpdate.cameraUpdateWithFitBounds(
            bounds.toNaver(),
            padding = paddingDp.toDouble()
        )
        update.animation = animation.toIos()
        update.animationDuration = durationMs.toDouble() / 1000.0
        mapView.moveCamera(update)
    }

    override fun setMapType(mapType: MapType) {
        mapView.mapType = mapType.toIos()
    }

    override fun setNightMode(enabled: Boolean) {
        mapView.nightModeEnabled = enabled
    }

    override fun setIndoorEnabled(enabled: Boolean) {
        mapView.indoorMapEnabled = enabled
    }

    override fun setBuildingHeight(height: Float) {
        mapView.buildingHeight = height
    }

    override fun latLngToScreen(latLng: LatLng): Pair<Float, Float> {
        val point = mapView.projection.pointFromLatLng(latLng.toNaver())
        return point.useContents { x.toFloat() to y.toFloat() }
    }

    override fun screenToLatLng(x: Float, y: Float): LatLng {
        val latLng = mapView.projection.latlngFromPoint(CGPointMake(x.toDouble(), y.toDouble()))
        return latLng.toCommon()
    }
}

private fun CameraAnimation.toIos(): NMFCameraUpdateAnimation = when (this) {
    CameraAnimation.None -> NMFCameraUpdateAnimation.NMFCameraUpdateAnimationNone
    CameraAnimation.Linear -> NMFCameraUpdateAnimation.NMFCameraUpdateAnimationLinear
    CameraAnimation.Easing -> NMFCameraUpdateAnimation.NMFCameraUpdateAnimationEaseOut
    CameraAnimation.Fly -> NMFCameraUpdateAnimation.NMFCameraUpdateAnimationFly
}
