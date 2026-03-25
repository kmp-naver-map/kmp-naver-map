package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.overlay.rememberRoundOverlayImageFromUrl
import io.github.kmp.maps.naver.compose.overlay.tearDropAnchor
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.model.LatLngBounds
import io.github.kmp.maps.naver.compose.model.LocationTrackingMode
import io.github.kmp.maps.naver.compose.options.LocationOverlayOptions
import io.github.kmp.maps.naver.compose.options.LogoAlign
import io.github.kmp.maps.naver.compose.options.MapUiSettings
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap
import kotlin.random.Random

@Composable
fun MarkerDemoScreen(paddingValues: PaddingValues = PaddingValues()) {
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

    var trackingMode by remember { mutableStateOf(LocationTrackingMode.None) }

    // 네이티브 지도가 트래킹 모드를 변경할 때(드래그 등) UI에 반영.
    // None은 무시: _locationTrackingMode 초기값이 None이어서 첫 실행 시
    // trackingMode = Follow가 None으로 덮어씌워져 버튼이 깜빡이는 문제 방지.
    // (사용자가 직접 OFF 누를 때는 onClick에서 trackingMode = None으로 직접 처리됨)
    LaunchedEffect(mapState.locationTrackingMode) {
        if (mapState.locationTrackingMode != LocationTrackingMode.None) {
            trackingMode = mapState.locationTrackingMode
        }
    }

    // 현재 선택된 카테고리 ("명소" 또는 "푸드트럭")
    var selectedCategory by remember { mutableStateOf("명소") }

    // 카테고리별 마커 데이터 생성
    // 각 마커마다 고유한 이미지를 위해 seed 값을 부여함
    val attractions = remember {
        List(20) { i ->
            val lat = center.latitude + (Random.nextDouble() - 0.5) * 0.005
            val lng = center.longitude + (Random.nextDouble() - 0.5) * 0.005
            // 홀수 id(1, 3, 5 ...)는 url null → 흰 마커로 표시
            MarkerData(
                id = i + 1,
                position = LatLng(lat, lng),
                imageUrl = if ((i + 1) % 2 == 0) "https://picsum.photos/seed/attraction_$i/100" else null
            )
        }
    }

    val foodTrucks = remember {
        List(10) { i ->
            val lat = center.latitude + (Random.nextDouble() - 0.5) * 0.005
            val lng = center.longitude + (Random.nextDouble() - 0.5) * 0.005
            // 짝수 id(2, 4, 6 ...)는 url null → 흰 마커로 표시
            MarkerData(
                id = i + 1,
                position = LatLng(lat, lng),
                imageUrl = if ((i + 1) % 2 != 0) "https://picsum.photos/seed/food_$i/100" else null
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 지도 레이어
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            state = mapState,
            uiSettings = MapUiSettings(
                logoAlign = LogoAlign.RightTop,
                logoMarginTop = paddingValues.calculateTopPadding().value.toInt(),
            ),
            locationTrackingMode = trackingMode,
            locationOverlayOptions = LocationOverlayOptions(isVisible = trackingMode != LocationTrackingMode.None)
        ) {
            if (selectedCategory == "명소") {
                attractions.forEach { data ->
                    val icon = rememberRoundOverlayImageFromUrl(
                        url = data.imageUrl,
                        sizePx = 120,
                        borderWidthPx = 10,
                        shadowRadiusPx = 10f,
                        shadowDy = 20f,
                        tailHeightPx = 15
                    )
                    Marker(
                        position = data.position,
                        icon = icon,
                        width = 60f,
                        height = 65f,
                        anchor = tearDropAnchor(
                            sizePx = 120, tailHeightPx = 15,
                            shadowRadiusPx = 10f, shadowDx = 0f, shadowDy = 20f,
                        ),
                        onClick = { addLog("명소 ${data.id} 클릭"); false }
                    )
                }
            }

            if (selectedCategory == "푸드트럭") {
                foodTrucks.forEach { data ->
                    val icon = rememberRoundOverlayImageFromUrl(
                        url = data.imageUrl,
                        sizePx = 100,
                        borderWidthPx = 8,
                        shadowRadiusPx = 10f,
                        shadowDy = 4f,
                    )
                    Marker(
                        position = data.position,
                        icon = icon,
                        caption = "푸드트럭 ${data.id}",
                        width = 40f,
                        height = 40f,
                        anchor = tearDropAnchor(
                            sizePx = 100, tailHeightPx = 0,
                            shadowRadiusPx = 10f, shadowDx = 0f, shadowDy = 4f,
                        ),
                        onClick = { addLog("푸드트럭 ${data.id} 클릭"); false }
                    )
                }
            }
        }

        // 2. 하단 컨트롤 레이어 (카테고리 선택 버튼 + 로그 창)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            Surface(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("명소", "푸드트럭").forEach { category ->
                        val isSelected = selectedCategory == category
                        Button(
                            onClick = { selectedCategory = category },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0),
                                contentColor = if (isSelected) Color.White else Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                            shape = RoundedCornerShape(25.dp),
                            elevation = null
                        ) {
                            Text(
                                text = if (category == "명소") "명소 (20)" else "푸드트럭 (10)",
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                color = Color.Black.copy(alpha = 0.75f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("선택된 카테고리: $selectedCategory", color = Color.Yellow, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            mapState.lastLocation?.let {
                                Text(
                                    "${(kotlin.math.round(it.latitude * 10000) / 10000.0)}, ${(kotlin.math.round(it.longitude * 10000) / 10000.0)}",
                                    color = Color.Cyan, fontSize = 10.sp
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            Button(
                                onClick = {
                                    trackingMode = if (trackingMode == LocationTrackingMode.None)
                                        LocationTrackingMode.NoFollow else LocationTrackingMode.None
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (trackingMode != LocationTrackingMode.None) Color(0xFF3182F6) else Color.DarkGray
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    if (trackingMode != LocationTrackingMode.None) "위치 ON" else "위치 OFF",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 6.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(eventLogs) { log ->
                            Text("- $log", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

private data class MarkerData(
    val id: Int,
    val position: LatLng,
    val imageUrl: String?
)
