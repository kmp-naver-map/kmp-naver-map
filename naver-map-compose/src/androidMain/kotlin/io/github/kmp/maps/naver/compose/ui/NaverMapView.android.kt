package io.github.kmp.maps.naver.compose.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.map.MapView
import com.naver.maps.map.util.FusedLocationSource
import io.github.kmp.maps.naver.compose.NaverMapSdk
import io.github.kmp.maps.naver.compose.controller.AndroidNaverMapController
import io.github.kmp.maps.naver.compose.controller.INaverMapController
import io.github.kmp.maps.naver.compose.state.NaverMapState
import com.naver.maps.map.NaverMapSdk as NativeNaverMapSdk

private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
actual fun NaverMapView(
    modifier: Modifier,
    state: NaverMapState,
    onMapReady: (INaverMapController) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = remember(context) { context.findActivity() }
    val mapView = remember {
        if (NaverMapSdk.clientId.isNotEmpty()) {
            NativeNaverMapSdk.getInstance(context).client =
                NativeNaverMapSdk.NcpKeyClient(NaverMapSdk.clientId)
        }
        MapView(context)
    }
    val currentOnMapReady by rememberUpdatedState(onMapReady)

    // Lifecycle 관리
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        // 이미 진행된 lifecycle 상태 수동 보정
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            mapView.onCreate(null)
        }
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            mapView.onResume()
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync { naverMap ->
                    state._context = context
                    state.locationSourceFactory = { activity?.let { FusedLocationSource(it, LOCATION_PERMISSION_REQUEST_CODE) } }
                    state.naverMap = naverMap
                    currentOnMapReady(AndroidNaverMapController(naverMap))
                }
            }
        },
        modifier = modifier
    )
}