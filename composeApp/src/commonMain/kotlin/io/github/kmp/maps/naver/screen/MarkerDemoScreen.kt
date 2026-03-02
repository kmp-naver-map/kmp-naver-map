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
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun MarkerDemoScreen() {
    val seoulCityHall = LatLng(37.5666102, 126.9783881)
    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = seoulCityHall, zoom = 12.0)
    )

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
            // 1. [고급] 마커 전체 줌 제약 (13~16 레벨에서만 마커 자체가 보임)
            Marker(
                position = LatLng(37.5666, 126.9783),
                caption = "줌 13~16에서만 보임",
                minZoom = 13.0,
                maxZoom = 16.0,
                iconTintColor = Color.Red.toArgb(),
                onClick = { addLog("제약 마커 클릭"); false }
            )

            // 2. [고급] 캡션 오프셋 및 원근감
            Marker(
                position = LatLng(37.5690, 126.9750),
                icon = Res.drawable.ic_marker,
                caption = "오프셋 + 원근감",
                captionOffset = 20f, // 캡션을 아이콘에서 20pt 띄움
                captionPerspectiveEnabled = true, // 지도를 기울이면 캡션도 누움
                isIconPerspectiveEnabled = true,
                onClick = { addLog("오프셋 마커 클릭"); false }
            )

            // 3. [고급] 글로벌 Z-Index (심벌 아래에 그리기)
            // globalZIndex < 0 이면 지도상의 건물 이름(심벌) 뒤로 숨습니다.
            Marker(
                position = LatLng(37.5710, 126.9770),
                caption = "심벌 아래 마커",
                globalZIndex = -100000,
                iconTintColor = Color.Blue.toArgb()
            )

            // 4. [고급] 캡션 강제 노출 (다른 마커와 겹쳐도 표시)
            Marker(
                position = LatLng(37.5640, 126.9720),
                caption = "무조건 보이는 캡션",
                isForceShowCaption = true,
                isForceShowIcon = true,
                iconTintColor = Color.Yellow.toArgb()
            )

            // 5. [기본] 복합 설정 테스트
            Marker(
                position = LatLng(37.5620, 126.9780),
                icon = Res.drawable.compose_multiplatform,
                caption = "복합 설정",
                subCaption = "서브 캡션 테스트",
                subCaptionColor = Color.Magenta.toArgb(),
                isFlat = true,
                angle = 90f,
                width = 40f,
                height = 40f
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
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(eventLogs) { log ->
                        Text("- $log", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
