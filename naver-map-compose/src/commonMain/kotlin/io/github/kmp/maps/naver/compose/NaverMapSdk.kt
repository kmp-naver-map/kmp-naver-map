package io.github.kmp.maps.naver.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * 네이버 지도 SDK 설정을 관리하는 싱글턴 객체입니다.
 *
 * Singleton object that manages Naver Map SDK configuration.
 */
object NaverMapSdk {
    /**
     * 네이버 클라우드 플랫폼 클라이언트 ID.
     *
     * The Naver Cloud Platform Client ID.
     */
    var clientId: String = ""
        private set

    /**
     * 주어진 클라이언트 ID로 네이버 지도 SDK를 초기화합니다.
     * NaverMapView가 렌더링되기 전에 한 번 호출해야 합니다.
     *
     * Initializes the Naver Map SDK with the given client ID.
     * This should be called once before any NaverMapView is rendered.
     */
    fun initialize(clientId: String) {
        this.clientId = clientId
        initializePlatform(clientId)
    }
}

/**
 * 네이버 지도 SDK를 초기화하는 Composable 함수입니다.
 * 애플리케이션의 콘텐츠를 이 함수로 감싸야 합니다.
 *
 * Composable function to initialize the Naver Map SDK.
 * This should be wrapped around your application's content.
 *
 * @param clientId 네이버 클라우드 플랫폼 클라이언트 ID. / The Naver Cloud Platform Client ID.
 * @param content 네이버 지도를 사용하는 Composable 콘텐츠. / The composable content that will use Naver Map.
 */
@Composable
fun NaverMapSdkProvider(
    clientId: String,
    content: @Composable () -> Unit
) {
    remember(clientId) {
        NaverMapSdk.initialize(clientId)
    }
    content()
}

/**
 * 플랫폼별 내부 초기화 함수.
 *
 * Internal platform-specific initialization.
 */
internal expect fun initializePlatform(clientId: String)
