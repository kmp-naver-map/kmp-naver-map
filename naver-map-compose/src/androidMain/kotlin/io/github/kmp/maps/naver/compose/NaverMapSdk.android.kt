package io.github.kmp.maps.naver.compose

internal actual fun initializePlatform(clientId: String) {
    // Android는 Context가 필요하므로 NaverMapView의 factory에서 
    // LocalContext를 사용하여 초기화합니다.
}
