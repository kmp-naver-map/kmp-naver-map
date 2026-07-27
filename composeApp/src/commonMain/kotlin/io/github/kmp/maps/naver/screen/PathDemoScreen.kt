package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.maps.naver.compose.model.CameraPosition
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.createDirectionArrowOverlayImage
import io.github.kmp.maps.naver.compose.overlay.createFootprintOverlayImage
import io.github.kmp.maps.naver.compose.overlay.createPawprintOverlayImage
import io.github.kmp.maps.naver.compose.state.rememberNaverMapState
import io.github.kmp.maps.naver.compose.ui.NaverMap

/** 경로 패턴 종류 */
private enum class PathPattern(val label: String) {
    None("기본 선"),
    Arrow("화살표"),
    Footprint("발자국"),
    Pawprint("발바닥"),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PathDemoScreen() {
    val seoulCityHall = LatLng(37.5666102, 126.9783881)
    val gwanghwamun = LatLng(37.5758772, 126.9768121)

    val mapState = rememberNaverMapState(
        initialPosition = CameraPosition(target = LatLng(37.5712, 126.9776), zoom = 15.0)
    )

    val routePoints = remember {
        listOf(
            seoulCityHall,
            LatLng(37.5695, 126.9780),
            LatLng(37.5720, 126.9775),
            gwanghwamun
        )
    }

    // 패턴 이미지는 에셋 없이 코드로 생성 (진행 방향으로 자동 회전됨).
    // 네이버 지도는 패턴 이미지를 선 두께에 맞게 축소하므로, 이미지 너비를 선 두께와
    // 똑같이 잡아 축소가 아예 일어나지 않게 한다(그린 그대로 찍힘).
    // 높이는 걸음 간격만 결정하며, 실제 걸음 간격은 높이의 1/2이다.
    val arrowImage = remember { createDirectionArrowOverlayImage(widthDp = 10f, heightDp = 12f) }
    val footprintImage = remember { createFootprintOverlayImage(widthDp = 8f, heightDp = 46f) }
    val pawprintImage = remember { createPawprintOverlayImage(widthDp = 12f, heightDp = 30f) }

    var pattern by remember { mutableStateOf(PathPattern.Pawprint) }

    val patternImage = when (pattern) {
        PathPattern.None -> null
        PathPattern.Arrow -> arrowImage
        PathPattern.Footprint -> footprintImage
        PathPattern.Pawprint -> pawprintImage
    }
    // 선 두께 = 패턴 이미지 너비.
    val pathWidth = when (pattern) {
        PathPattern.None -> 8f
        PathPattern.Arrow -> 10f
        PathPattern.Footprint -> 8f
        PathPattern.Pawprint -> 12f
    }
    // 지도 SDK는 패턴 이미지를 patternInterval마다 찍는다.
    // 두 발이 이미지의 18.4%/81.6% 지점에 있으므로 heightDp와 같게 주면
    // 걸음 간격이 heightDp의 절반으로 균일해진다.
    val patternInterval = when (pattern) {
        PathPattern.None -> 0f
        PathPattern.Arrow -> 16f
        PathPattern.Footprint -> 46f
        PathPattern.Pawprint -> 30f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            state = mapState
        ) {
            Path(
                coords = routePoints,
                width = pathWidth,
                outlineWidth = 1f,
                color = 0xFF00C853.toInt(),
                outlineColor = 0xFFFFFFFF.toInt(),
                passedColor = 0xFFBDBDBD.toInt(),
                passedOutlineColor = 0xFFFFFFFF.toInt(),
                progress = 0.4,
                patternImage = patternImage,
                patternInterval = patternInterval,
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("경로 패턴")
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PathPattern.entries.forEach { p ->
                        FilterChip(
                            selected = pattern == p,
                            onClick = { pattern = p },
                            label = { Text(p.label) },
                        )
                    }
                }
            }
        }
    }
}
