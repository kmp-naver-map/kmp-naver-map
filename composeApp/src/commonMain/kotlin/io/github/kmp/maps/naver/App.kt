package io.github.kmp.maps.naver

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.kmp.maps.naver.component.SampleScaffold
import io.github.kmp.maps.naver.compose.NaverMapSdkProvider
import io.github.kmp.maps.naver.screen.ArrowPathDemoScreen
import io.github.kmp.maps.naver.screen.CircleDemoScreen
import io.github.kmp.maps.naver.screen.HomeScreen
import io.github.kmp.maps.naver.screen.InfoWindowDemoScreen
import io.github.kmp.maps.naver.screen.LocationDemoScreen
import io.github.kmp.maps.naver.screen.MapControlsDemoScreen
import io.github.kmp.maps.naver.screen.MarkerDemoScreen
import io.github.kmp.maps.naver.screen.PathDemoScreen

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Naver Map Compose")
    data object Marker : Screen("marker", "Marker Demo")
    data object Path : Screen("path", "Path Overlay Demo")
    data object ArrowPath : Screen("arrow_path", "Arrowhead Path Demo")
    data object InfoWindow : Screen("info_window", "Info Window Demo")
    data object Circle : Screen("circle", "Circle Overlay Demo")
    data object MapControls : Screen("map_controls", "Map Controls Demo")
    data object Location : Screen("location", "Location Tracking Demo")
}

@Composable
fun App() {
    val navController = rememberNavController()

    // 1. Secrets 객체(빌드 시 생성됨)를 사용하여 클라이언트 ID 참조
    NaverMapSdkProvider(clientId = "3hxpuj0ssz") {
        MaterialTheme {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    HomeScreen(onNavigate = { screen ->
                        navController.navigate(screen.route)
                    })
                }
                composable(Screen.Marker.route) {
                    SampleScaffold(Screen.Marker.title, onBack = { navController.popBackStack() }) {
                        MarkerDemoScreen()
                    }
                }
                composable(Screen.Path.route) {
                    SampleScaffold(Screen.Path.title, onBack = { navController.popBackStack() }) {
                        PathDemoScreen()
                    }
                }
                composable(Screen.ArrowPath.route) {
                    SampleScaffold(
                        Screen.ArrowPath.title,
                        onBack = { navController.popBackStack() }) {
                        ArrowPathDemoScreen()
                    }
                }
                composable(Screen.InfoWindow.route) {
                    SampleScaffold(
                        Screen.InfoWindow.title,
                        onBack = { navController.popBackStack() }) {
                        InfoWindowDemoScreen()
                    }
                }
                composable(Screen.Circle.route) {
                    SampleScaffold(Screen.Circle.title, onBack = { navController.popBackStack() }) {
                        CircleDemoScreen()
                    }
                }
                composable(Screen.MapControls.route) {
                    SampleScaffold(
                        Screen.MapControls.title,
                        onBack = { navController.popBackStack() }) {
                        MapControlsDemoScreen()
                    }
                }
                composable(Screen.Location.route) {
                    SampleScaffold(
                        Screen.Location.title,
                        onBack = { navController.popBackStack() }) {
                        LocationDemoScreen()
                    }
                }
            }
        }
    }
}
