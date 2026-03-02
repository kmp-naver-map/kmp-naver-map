package io.github.kmp.maps.naver.compose.event

import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.Marker

sealed class MapEvent {
    data class OnMapClick(val latLng: LatLng) : MapEvent()
    data class OnMapLongClick(val latLng: LatLng) : MapEvent()
    data class OnMarkerClick(val marker: Marker) : MapEvent()
    data class OnCameraIdle(val position: CameraPosition) : MapEvent()
    data class OnCameraMove(val position: CameraPosition) : MapEvent()
    object OnMapReady : MapEvent()
}