package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun MapControlsDemoScreen() {
    val seoulCityHall = LatLng(37.5666102, 126.9783881)
    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = seoulCityHall, zoom = 14.0)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            state = mapState
        )
        
        Column(
            modifier = Modifier.padding(16.dp).align(androidx.compose.ui.Alignment.BottomStart)
        ) {
            Button(onClick = {
                mapState.animateCamera(CameraPosition(target = LatLng(37.5172, 127.0473), zoom = 15.0))
            }) {
                Text("강남구청으로 이동")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                mapState.animateCamera(CameraPosition(target = seoulCityHall, zoom = 14.0))
            }) {
                Text("서울시청으로 이동")
            }
        }
    }
}
