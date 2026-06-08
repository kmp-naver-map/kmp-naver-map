@file:OptIn(ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.NMapsMap.NMFAuthManager
import cocoapods.NMapsMap.NMFNaverMapView
import io.github.kmp.maps.naver.compose.NaverMapSdk
import io.github.kmp.maps.naver.compose.controller.INaverMapController
import io.github.kmp.maps.naver.compose.controller.IosNaverMapController
import io.github.kmp.maps.naver.compose.state.NaverMapState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification

@Composable
actual fun NaverMapView(
    modifier: Modifier,
    state: NaverMapState,
    onMapReady: (INaverMapController) -> Unit
) {
    val currentOnMapReady by rememberUpdatedState(onMapReady)

    // SDK 초기화 (ncpKeyId 사용)
    DisposableEffect(Unit) {
        if (NaverMapSdk.clientId.isNotEmpty()) {
            NMFAuthManager.shared().ncpKeyId = NaverMapSdk.clientId
        }
        onDispose { }
    }

    // 앱 생명주기에 위치 업데이트를 연동한다.
    // - 포그라운드 복귀: iOS는 포그라운드 전용 앱의 위치 업데이트를 백그라운드 동안 중단하므로,
    //   복귀 시 직접 재가동하지 않으면 위치 점이 나갈 때의 위치에 멈춰 있게 된다.
    // - 백그라운드 진입: 위치 업데이트를 명시적으로 중단해 배터리 소모를 줄인다.
    DisposableEffect(state) {
        val center = NSNotificationCenter.defaultCenter
        val foregroundObserver = center.addObserverForName(
            UIApplicationWillEnterForegroundNotification,
            null,
            NSOperationQueue.mainQueue,
        ) { _ -> state.onEnterForeground() }
        val backgroundObserver = center.addObserverForName(
            UIApplicationDidEnterBackgroundNotification,
            null,
            NSOperationQueue.mainQueue,
        ) { _ -> state.onEnterBackground() }
        onDispose {
            center.removeObserver(foregroundObserver)
            center.removeObserver(backgroundObserver)
        }
    }

    UIKitView(
        factory = {
            val container = NMFNaverMapView()
            state.naverMapView = container
            currentOnMapReady(IosNaverMapController(container.mapView))
            container
        },
        modifier = modifier,
        onRelease = {
            state.naverMapView = null
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = false
        )
    )
}
