package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composenavermap.composeapp.generated.resources.Res
import composenavermap.composeapp.generated.resources.ic_marker
import composenavermap.composeapp.generated.resources.compose_multiplatform
import composenavermap.composeapp.generated.resources.marker_attraction
import composenavermap.composeapp.generated.resources.marker_food_truck
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun MarkerDemoScreen() {
    val center = LatLng(35.8362304, 129.2831314)
    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = center, zoom = 16.0, bearing = -80.0),

        )

    LaunchedEffect(mapState.isMapReady) {
        if (mapState.isMapReady) {
            mapState.setCustomStyleId("21b7d6c0-9b36-47d6-9390-949f42b02818")
            mapState.extent = LatLngBounds(
                southwest = LatLng(35.8341273, 129.2767851),
                northeast = LatLng(35.840167568414394, 129.2855720756695)
            )
        }
    }

    val eventLogs = remember { mutableStateListOf<String>() }
    fun addLog(msg: String) {
        eventLogs.add(0, "${eventLogs.size + 1}: $msg")
        if (eventLogs.size > 15) eventLogs.removeAt(eventLogs.size - 1)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            state = mapState
        ) {
            // marker_attraction: center 기준 약간 북서쪽
            Marker(
                position = LatLng(35.8370, 129.2800),
                icon = Res.drawable.marker_attraction,
                caption = "명소",
                width = 40f,
                height = 40f,
                onClick = { addLog("명소 마커 클릭"); false }
            )

            // marker_food_truck: center 기준 약간 남동쪽
            Marker(
                position = LatLng(35.8355, 129.2850),
                icon = Res.drawable.marker_food_truck,
                caption = "푸드트럭",
                width = 40f,
                height = 40f,
                onClick = { addLog("푸드트럭 마커 클릭"); false }
            )
        }

        // 로그 및 정보 UI
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(180.dp),
            color = Color.Black.copy(alpha = 0.7f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Marker Advanced Features Demo", color = Color.Cyan, fontSize = 14.sp)
                Text(
                    "현재 줌: ${(mapState.cameraPosition.zoom)}",
                    color = Color.White, fontSize = 12.sp
                )
                HorizontalDivider(
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(eventLogs) { log ->
                        Text("- $log", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
