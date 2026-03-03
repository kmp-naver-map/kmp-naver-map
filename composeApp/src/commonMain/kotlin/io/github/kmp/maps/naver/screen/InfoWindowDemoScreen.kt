package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.maps.naver.compose.model.Anchor
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun InfoWindowDemoScreen() {
    val gwanghwamun = LatLng(37.5758772, 126.9768121)
    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = gwanghwamun, zoom = 14.0)
    )

    // 테스트를 위한 상태값들
    var text by remember { mutableStateOf("광화문광장") }
    var alpha by remember { mutableStateOf(1f) }
    var colorToggle by remember { mutableStateOf(true) }
    var isVisible by remember { mutableStateOf(true) }
    var offsetX by remember { mutableStateOf(0) }
    var offsetY by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 컨트롤 바
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { text = if (text == "광화문광장") "이순신 장군 동상" else "광화문광장" }) {
                    Text("텍스트 변경")
                }
                Button(onClick = { colorToggle = !colorToggle }) {
                    Text("색상 변경")
                }
                Button(onClick = { isVisible = !isVisible }) {
                    Text(if (isVisible) "숨기기" else "보이기")
                }
            }
            
            // String.format 대신 소수점 한자리만 표시하도록 수식 사용
            val displayAlpha = (alpha * 10).toInt() / 10.0
            Text("투명도: $displayAlpha")
            Slider(value = alpha, onValueChange = { alpha = it })
            
            Text("오프셋 (X: $offsetX, Y: $offsetY)")
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(onClick = { offsetX -= 10 }) { Text("X-") }
                Button(onClick = { offsetX += 10 }) { Text("X+") }
                Button(onClick = { offsetY -= 10 }) { Text("Y-") }
                Button(onClick = { offsetY += 10 }) { Text("Y+") }
            }
        }

        NaverMap(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = mapState
        ) {
            InfoWindow(
                position = gwanghwamun,
                text = text,
                alpha = alpha,
                isVisible = isVisible,
                textColor = if (colorToggle) 0xFFFFFFFF.toInt() else 0xFFFF0000.toInt(),
                backgroundColor = if (colorToggle) 0xFF333333.toInt() else 0xFFF5F5F5.toInt(),
                offsetX = offsetX,
                offsetY = offsetY,
                zIndex = 500000,
                anchor = Anchor(0.5f, 1.0f),
                cornerRadiusDp = 12f,
                textSize = 16f,
                tag = "TestInfoWindow"
            )

            InfoWindow(
                position = LatLng(37.570, 126.976),
                text = "고정 인포윈도우",
                backgroundColor = 0xFF4CAF50.toInt(),
                textColor = 0xFFFFFFFF.toInt()
            )
        }
    }
}
