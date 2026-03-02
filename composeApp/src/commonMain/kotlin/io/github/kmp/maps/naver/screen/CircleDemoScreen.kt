package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun CircleDemoScreen() {
    val seoulCityHall = LatLng(37.5666102, 126.9783881)
    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = seoulCityHall, zoom = 14.0)
    )

    NaverMap(
        modifier = Modifier.fillMaxSize(),
        state = mapState
    ) {
        Circle(
            center = seoulCityHall,
            radius = 500.0,
            fillColor = 0x40FF0000,
            outlineColor = 0xFFFF0000.toInt(),
            outlineWidth = 2f
        )
    }
}
