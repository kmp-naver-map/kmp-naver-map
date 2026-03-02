package io.github.kmp.maps.naver.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun ArrowPathDemoScreen() {
    val startPoint = LatLng(37.5666102, 126.9783881)
    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = startPoint, zoom = 14.0)
    )

    // 상태 관리: 클릭 시 변화를 주기 위함
    var isClicked by remember { mutableStateOf(false) }
    var clickedCount by remember { mutableIntStateOf(0) }

    // 애니메이션 설정
    val animatedWidth by animateFloatAsState(
        targetValue = if (isClicked) 25f else 15f,
        animationSpec = tween(durationMillis = 500)
    )
    val animatedColor by animateColorAsState(
        targetValue = if (isClicked) Color.Magenta else Color(0xFF007AFF),
        animationSpec = tween(durationMillis = 500)
    )

    val arrowPoints = remember {
        listOf(
            startPoint,
            LatLng(37.5695, 126.9780),
            LatLng(37.5720, 126.9775),
            LatLng(37.5758772, 126.9768121)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            state = mapState
        ) {
            ArrowheadPath(
                coords = arrowPoints,
                width = 15f,
                color = 0xFF007AFF.toInt(), // Blue
                outlineWidth = 2f,
                outlineColor = 0xFFFFFFFF.toInt(),
                headSizeRatio = 3f
            )
            // 클릭 이벤트와 애니메이션이 적용된 화살표 경로
            ArrowheadPath(
                coords = arrowPoints,
                width = animatedWidth, // 애니메이션 적용
                color = animatedColor.toArgb(), // 애니메이션 적용
                outlineWidth = 2f,
                outlineColor = Color.White.toArgb(),
                headSizeRatio = if (isClicked) 4f else 2.5f,
                onClick = {
                    isClicked = !isClicked
                    clickedCount++
                    true // 이벤트를 소비함
                }
            )

        }

        // 클릭 횟수 표시 UI
        if (clickedCount > 0) {
            Text(
                text = "클릭 횟수: $clickedCount",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                color = Color.Black
            )
        }
    }
}
