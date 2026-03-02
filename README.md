# Naver Map Compose for Kotlin Multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kmp-naver-map/naver-map-compose)](https://search.maven.org/artifact/io.github.kmp-naver-map/naver-map-compose)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## 소개

**Naver Map Compose**는 [네이버 지도 SDK](https://navermaps.github.io/android-map-sdk/guide-ko/)를 [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)에서 사용할 수 있도록 래핑한 라이브러리입니다.

## 설치 방법

### 1. Gradle 플러그인 추가
네이버 지도 저장소 설정과 의존성을 자동으로 관리하기 위해 **사용하려는 모듈(예: `composeApp`)**의 `build.gradle.kts`에 플러그인을 추가하세요.

```kotlin
// composeApp/build.gradle.kts 또는 shared/build.gradle.kts
plugins {
    id("io.github.kmp-naver-map") version "1.0.0"
}
```

> **참고**: 버전 카탈로그(`libs.versions.toml`)를 사용 중이라면 다음과 같이 설정할 수 있습니다:
> ```kotlin
> // libs.versions.toml
> [plugins]
> naver-map-compose = { id = "io.github.kmp-naver-map", version = "1.0.0" }
>
> // build.gradle.kts
> plugins {
>     alias(libs.plugins.naver.map.compose)
> }
> ```

### 2. 플랫폼별 설정 (iOS)
iOS 앱에서 지도를 사용하기 위해 `Podfile`에 다음 설정을 추가하세요.
```ruby
pod 'NMapsMap', '3.23.1'
```

## 기본 사용법

애플리케이션의 최상위 컴포저블에서 `NaverMapSdkProvider`를 사용하여 SDK를 초기화합니다.

```kotlin
import io.github.kmp.maps.naver.compose.NaverMapSdkProvider
import io.github.kmp.maps.naver.compose.ui.NaverMap

@Composable
fun App() {
    // 네이버 클라우드 플랫폼에서 발급받은 Client ID 입력
    NaverMapSdkProvider(clientId = "YOUR_NAVER_CLIENT_ID") {
        NaverMap(modifier = Modifier.fillMaxSize()) {
            Marker(
                state = rememberMarkerState(position = LatLng(37.566, 126.978)),
                caption = "서울시청"
            )
        }
    }
}
```

## 고급 사용법

### MapEffect를 이용한 카메라 제어
지도가 준비된 후 `MapEffect`를 사용하여 `NaverMapState`에 안전하게 접근하고 카메라 이동과 같은 작업을 수행할 수 있습니다.

```kotlin
NaverMap(...) {
    MapEffect(selectedPlace) {
        animateCamera(
            position = CameraPosition(target = selectedPlace.latLng, zoom = 14.0),
            durationMs = 1000
        )
    }
}
```

## 주요 컴포넌트

- **`NaverMapSdkProvider`**: 클라이언트 ID로 SDK를 초기화하는 루트 프로바이더입니다.
- **`NaverMap`**: 지도를 표시하는 메인 컴포저블입니다.
- **`NaverMapState`**: 카메라 위치 및 애니메이션과 같은 지도의 상태를 제어합니다.
- **`MapUiSettings`**: 지도 UI 요소(줌 버튼, 나침반, 로고 등)를 설정합니다.

## 기여하기

기여는 언제나 환영합니다! 버그 보고나 기능 제안은 Issue를 통해 남겨주세요.


## 라이선스 및 이용 약관

### 프로젝트 라이선스
본 라이브러리는 **Apache License 2.0**을 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

### 의존성 라이선스 및 주의사항
본 라이브러리는 **네이버 지도 SDK**를 사용합니다. 이 라이브러리를 사용하는 개발자는 다음 사항을 준수해야 합니다:

1.  **네이버 지도 SDK 라이선스**: 네이버 지도 SDK의 저작권은 (주)네이버 및 네이버클라우드(주)에 있습니다. SDK 사용과 관련된 자세한 라이선스는 [네이버 지도 SDK 공식 문서](https://navermaps.github.io/android-map-sdk/guide-ko/1.html)를 참조하세요.
2.  **이용 약관 준수**: 본 라이브러리를 사용하는 서비스는 [네이버 클라우드 플랫폼 이용 약관](https://www.ncloud.com/policy/terms) 및 [Maps 서비스 이용 약관](https://www.ncloud.com/product/applicationService/maps)을 준수해야 합니다. 특히 **로고 노출 의무** 및 **사용량 제한**에 주의하시기 바랍니다.
3.  **책임 제한**: 본 라이브러리는 오픈 소스 소프트웨어로서 "있는 그대로" 제공되며, 네이버 지도 SDK 자체의 장애나 약관 위반으로 인한 문제에 대해서는 책임을 지지 않습니다.

Copyright 2026 Jun Cho
