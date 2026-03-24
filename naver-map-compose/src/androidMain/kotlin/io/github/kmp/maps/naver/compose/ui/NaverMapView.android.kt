package io.github.kmp.maps.naver.compose.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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

internal fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

@Composable
actual fun NaverMapView(
    modifier: Modifier,
    state: NaverMapState,
    onMapReady: (INaverMapController) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnMapReady by rememberUpdatedState(onMapReady)
    val currentState by rememberUpdatedState(state)

    // 컴포지션 단계에 등록 → NavBackStackEntry LifecycleOwner와 함께 안전하게 사용 가능
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            currentState.onPermissionGranted()
        } else {
            currentState.onPermissionDenied()
        }
    }

    SideEffect {
        currentState.permissionLauncher = permissionLauncher
    }

    // FusedLocationSource를 지도 초기화 시점에 미리 생성해 두고 재사용
    // Activity 참조가 필요하므로 remember로 생성 (재생성 방지)
    val fusedLocationSource = remember {
        context.findActivity()?.let { FusedLocationSource(it, LOCATION_PERMISSION_REQUEST_CODE) }
    }

    val mapView = remember {
        if (NaverMapSdk.clientId.isNotEmpty()) {
            NativeNaverMapSdk.getInstance(context).client =
                NativeNaverMapSdk.NcpKeyClient(NaverMapSdk.clientId)
        }
        MapView(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            mapView.onCreate(null)
        }
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            mapView.onStart()
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
                    // FusedLocationSource를 먼저 설정해야 Follow/Face 모드가 즉시 동작
                    state.locationSource = fusedLocationSource
                    state.naverMap = naverMap
                    currentOnMapReady(AndroidNaverMapController(naverMap))
                }
            }
        },
        modifier = modifier
    )
}
