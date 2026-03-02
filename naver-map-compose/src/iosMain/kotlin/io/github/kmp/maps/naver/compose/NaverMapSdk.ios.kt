package io.github.kmp.maps.naver.compose

import cocoapods.NMapsMap.NMFAuthManager
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal actual fun initializePlatform(clientId: String) {
    // iOS SDK 최신 규격: clientId 대신 ncpKeyId를 사용합니다.
    NMFAuthManager.shared().ncpKeyId = clientId
}
