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
            isNativeAccessibilityEnabled = true
        )
    )
}
