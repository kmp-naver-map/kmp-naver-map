package io.github.kmp.maps.naver.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.overlay.Marker

/**
 * 마커의 상태를 관리하는 클래스입니다.
 */
class MarkerState(
    position: LatLng = LatLng(0.0, 0.0)
) {
    /**
     * 마커의 현재 위치입니다. 이 값을 변경하면 지도의 마커 위치가 실시간으로 업데이트됩니다.
     */
    var position by mutableStateOf(position)

    /**
     * 실제 지도에 붙은 네이티브 마커 객체입니다.
     */
    internal var marker: Marker? by mutableStateOf(null)

    /**
     * 마커가 현재 지도에 추가되어 있는지 여부입니다.
     */
    val isAttached: Boolean get() = marker != null
}

/**
 * [MarkerState]를 생성하고 기억합니다.
 *
 * @param position 초기 위치
 */
@Composable
fun rememberMarkerState(
    position: LatLng = LatLng(0.0, 0.0)
): MarkerState = remember { MarkerState(position = position) }
