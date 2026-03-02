package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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

    // LocationOverlay 옵션 상태 관리
    var locationOptions by remember {
        mutableStateOf(LocationOverlayOptions(
            circleRadius = 100.0,
            circleColor = Color.Cyan.copy(alpha = 0.2f).toArgb(),
            circleOutlineWidth = 2f,
            circleOutlineColor = Color.Cyan.toArgb()
        ))
    }

    // UI 설정
    var uiSettings by remember {
        mutableStateOf(MapUiSettings(isLocationButtonEnabled = true))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            state = state,
            uiSettings = uiSettings,
            locationOverlayOptions = locationOptions,
            locationTrackingMode = state.locationTrackingMode
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
                Text("내 위치 오버레이 테스트", fontWeight = FontWeight.Bold)
                state.lastLocation?.let {
                    Text("위도: ${it.latitude}, 경도: ${it.longitude}", fontSize = 10.sp)
                }
            }
        }

        // 하단 컨트롤러 패널 (스크롤 가능)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            color = Color.White.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. 트래킹 모드 선택
                Text("위치 추적 모드", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocationTrackingMode.values().forEach { mode ->
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { state.locationTrackingMode = mode },
                            colors = if (state.locationTrackingMode == mode) 
                                ButtonDefaults.buttonColors() 
                            else ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text(mode.name, fontSize = 9.sp)
                        }
                    }
                }

                Divider()

                // 2. 정확도 원(Circle) 설정
                Text("정확도 원 설정", style = MaterialTheme.typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("반경 (meters): ${locationOptions.circleRadius.toInt()}", modifier = Modifier.weight(1f))
                    Slider(
                        value = locationOptions.circleRadius.toFloat(),
                        onValueChange = { locationOptions = locationOptions.copy(circleRadius = it.toDouble()) },
                        valueRange = 0f..500f,
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("보조 아이콘 표시", modifier = Modifier.weight(1f))
                    Switch(
                        checked = locationOptions.isSubIconVisible,
                        onCheckedChange = { locationOptions = locationOptions.copy(isSubIconVisible = it) }
                    )
                }

                // 3. 원근감 설정
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("아이콘 원근감 적용", modifier = Modifier.weight(1f))
                    Switch(
                        checked = locationOptions.isIconPerspectiveEnabled,
                        onCheckedChange = { locationOptions = locationOptions.copy(isIconPerspectiveEnabled = it) }
                    )
                }
            }
        }
    }
}
