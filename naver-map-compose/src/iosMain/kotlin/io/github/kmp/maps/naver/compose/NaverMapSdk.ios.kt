package io.github.kmp.maps.naver.compose

internal actual fun initializePlatform(clientId: String) {
    // 실제 인증(ncpKeyId) 적용은 NaverMapView의 factory에서 NaverMapSdk.clientId를 사용해
    // 수행합니다(Android와 대칭). 여기서 NMFAuthManager에 또 설정하면 이중 설정이 됩니다.
}
