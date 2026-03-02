package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun PathDemoScreen() {
    val seoulCityHall = LatLng(37.5666102, 126.9783881)
    val gwanghwamun = LatLng(37.5758772, 126.9768121)
    
    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = seoulCityHall, zoom = 14.0)
    )

    val routePoints = remember {
        listOf(
            seoulCityHall,
            LatLng(37.5695, 126.9780),
            LatLng(37.5720, 126.9775),
            gwanghwamun
        )
    }

    NaverMap(
        modifier = Modifier.fillMaxSize(),
        state = mapState
    ) {
        Path(
            coords = routePoints,
            width = 10f,
            outlineWidth = 2f,
            color = 0xFF00FF00.toInt(),
            passedColor = 0xFF888888.toInt(),
            progress = 0.5,
            outlineColor = 0xFFFFFFFF.toInt()
        )
    }
}
