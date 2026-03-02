package io.github.kmp.maps.naver.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Naver Map SDK Configurator.
 */
object NaverMapSdk {
    /**
     * The Naver Cloud Platform Client ID.
     */
    var clientId: String = ""
        private set

    /**
     * Initializes the Naver Map SDK with the given client ID.
     * This should be called once before any NaverMapView is rendered.
     */
    fun initialize(clientId: String) {
        this.clientId = clientId
        initializePlatform(clientId)
    }
}

/**
 * Composable function to initialize the Naver Map SDK.
 * This should be wrapped around your application's content.
 *
 * @param clientId The Naver Cloud Platform Client ID.
 * @param content The composable content that will use Naver Map.
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
 * Internal platform-specific initialization.
 */
internal expect fun initializePlatform(clientId: String)
