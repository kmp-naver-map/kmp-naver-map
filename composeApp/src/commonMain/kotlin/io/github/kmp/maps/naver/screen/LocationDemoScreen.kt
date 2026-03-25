package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LocationTrackingMode
import io.github.kmp.maps.naver.compose.options.LocationOverlayOptions
import io.github.kmp.maps.naver.compose.options.MapUiSettings
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun LocationDemoScreen() {
    val state = rememberNaverMapState(
        initialPosition = CameraPosition(LatLng(37.5665, 126.9780), 15.0)
    )

    var trackingMode by remember { mutableStateOf(LocationTrackingMode.None) }

    // 네이티브 지도가 트래킹 모드를 변경할 때(예: 드래그 → NoFollow) UI에 반영
    LaunchedEffect(state.locationTrackingMode) {
        trackingMode = state.locationTrackingMode
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            state = state,
            uiSettings = MapUiSettings(isLocationButtonEnabled = true),
            locationOverlayOptions = LocationOverlayOptions(isVisible = trackingMode != LocationTrackingMode.None),
            locationTrackingMode = trackingMode
        )

        // 상단 정보 패널
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            color = Color.White.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("내 위치 추적 데모", fontWeight = FontWeight.Bold)
                state.lastLocation?.let {
                    Text("위도: ${it.latitude}, 경도: ${it.longitude}", fontSize = 10.sp)
                }
            }
        }

        // 하단 트래킹 모드 선택 패널
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("위치 추적 모드", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocationTrackingMode.entries.forEach { mode ->
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { trackingMode = mode },
                            colors = if (trackingMode == mode)
                                ButtonDefaults.buttonColors()
                            else ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text(mode.name, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}
