# Naver Map Compose for Kotlin Multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kmp-naver-map/naver-map-compose)](https://search.maven.org/artifact/io.github.kmp-naver-map/naver-map-compose)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg)

[네이버 지도 SDK](https://navermaps.github.io/android-map-sdk/guide-ko/)를 [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)에서 선언적으로 사용할 수 있는 라이브러리입니다.

A declarative Naver Map SDK wrapper for Compose Multiplatform (Android & iOS).

---

## Features / 주요 기능

- **Overlay DSL** — `NaverMap { }` 스코프 내에서 Marker, Polyline, Polygon, Circle, Path, ArrowheadPath, InfoWindow를 선언적으로 추가
- **Camera Control** — `MapEffect`를 통한 카메라 애니메이션, `fitBounds`, 줌 제한 설정
- **Location Tracking** — `LocationTrackingMode`를 통한 사용자 위치 추적 통합
- **MapUiSettings** — 줌 버튼, 나침반, 스케일 바, 레이어 그룹 등 UI 요소 제어
- **Night Mode & Indoor** — 야간 모드, 실내 지도 지원
- **Gradle Plugin** — 저장소 설정과 의존성을 자동 관리하는 Gradle 플러그인 제공

---

## Installation / 설치

### 1. Gradle 플러그인 추가

**사용하려는 모듈(예: `composeApp`)**의 `build.gradle.kts`에 플러그인을 추가하세요.

```kotlin
// composeApp/build.gradle.kts 또는 shared/build.gradle.kts
plugins {
    id("io.github.kmp-naver-map") version "1.0.1"
}
```

> **Version Catalog 사용 시:**
> ```toml
> # libs.versions.toml
> [plugins]
> naver-map-compose = { id = "io.github.kmp-naver-map", version = "1.0.1" }
> ```
> ```kotlin
> // build.gradle.kts
> plugins {
>     alias(libs.plugins.naver.map.compose)
> }
> ```

### 2. iOS 설정

iOS 앱에서 지도를 사용하려면 `Podfile`에 다음을 추가하세요.

```ruby
pod 'NMapsMap', '3.23.1'
```

---

## Quick Start / 빠른 시작

`NaverMapSdkProvider`로 SDK를 초기화하고, `NaverMap` 컴포저블로 지도를 표시합니다.

```kotlin
import io.github.kmp.maps.naver.compose.NaverMapSdkProvider
import io.github.kmp.maps.naver.compose.ui.NaverMap
import io.github.kmp.maps.naver.compose.model.LatLng
import io.github.kmp.maps.naver.compose.state.rememberMarkerState

@Composable
fun App() {
    NaverMapSdkProvider(clientId = "YOUR_CLIENT_ID") {
        NaverMap(modifier = Modifier.fillMaxSize()) {
            Marker(
                state = rememberMarkerState(position = LatLng(37.566, 126.978)),
                caption = "서울시청"
            )
        }
    }
}
```

---

## Usage / 사용법

### Marker / 마커

```kotlin
NaverMap {
    // 방법 1: MarkerState 사용 (위치 변경 가능)
    val markerState = rememberMarkerState(position = LatLng(37.566, 126.978))
    Marker(state = markerState, caption = "서울시청")

    // 방법 2: 위치 직접 지정
    Marker(position = LatLng(37.551, 126.988), caption = "남산타워")
}
```

### Polyline / 폴리라인

```kotlin
NaverMap {
    Polyline(
        coords = listOf(
            LatLng(37.566, 126.978),
            LatLng(37.551, 126.988),
            LatLng(37.540, 127.000)
        ),
        width = 5f
    )
}
```

### Polygon / 폴리곤

```kotlin
NaverMap {
    Polygon(
        coords = listOf(
            LatLng(37.566, 126.978),
            LatLng(37.551, 126.988),
            LatLng(37.540, 126.968)
        )
    )
}
```

### Circle / 원

```kotlin
NaverMap {
    Circle(
        center = LatLng(37.566, 126.978),
        radius = 500.0 // 미터
    )
}
```

### Path / 경로

```kotlin
NaverMap {
    Path(
        coords = listOf(
            LatLng(37.566, 126.978),
            LatLng(37.551, 126.988)
        ),
        progress = 0.5 // 50% 진행
    )
}
```

### ArrowheadPath / 화살표 경로

```kotlin
NaverMap {
    ArrowheadPath(
        coords = listOf(
            LatLng(37.566, 126.978),
            LatLng(37.551, 126.988)
        ),
        width = 15f
    )
}
```

### InfoWindow / 정보 창

```kotlin
NaverMap {
    InfoWindow(
        position = LatLng(37.566, 126.978),
        text = "정보 창 내용"
    )
}
```

### Camera Control / 카메라 제어

`MapEffect`를 사용하면 지도 준비 후 `NaverMapState`에 접근하여 카메라를 제어할 수 있습니다.

```kotlin
NaverMap {
    MapEffect(selectedPlace) {
        animateCamera(
            position = CameraPosition(target = selectedPlace.latLng, zoom = 14.0),
            durationMs = 1000
        )
    }
}
```

### MapUiSettings / 지도 UI 설정

```kotlin
NaverMap(
    uiSettings = MapUiSettings(
        isZoomControlEnabled = true,
        isCompassEnabled = false,
        isLocationButtonEnabled = true
    )
)
```

### Location Tracking / 위치 추적

```kotlin
NaverMap(
    locationTrackingMode = LocationTrackingMode.Follow
)
```

---

## Troubleshooting / 문제 해결

| 증상 | 해결 방법 |
|------|----------|
| 지도가 표시되지 않음 | `NaverMapSdkProvider`가 `NaverMap`을 감싸고 있는지, Client ID가 올바른지 확인하세요. |
| iOS에서 빌드 오류 | `pod install` 실행 후 `.xcworkspace`로 열어야 합니다. |
| `Could not resolve` 의존성 오류 | Gradle 플러그인 사용 시 네이버 저장소가 자동 추가됩니다. 수동 설정 시 `https://repository.map.naver.com/archive/maven`을 추가하세요. |

---

## Contributing / 기여하기

기여는 언제나 환영합니다! 자세한 내용은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요.

- 버그 보고 및 기능 제안: [Issues](https://github.com/kmp-naver-map/kmp-naver-map/issues)
- Pull Request 환영합니다.

---

## License / 라이선스

### 프로젝트 라이선스

본 라이브러리는 **Apache License 2.0**을 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

### 의존성 라이선스 및 주의사항

본 라이브러리는 **네이버 지도 SDK**를 사용합니다. 이 라이브러리를 사용하는 개발자는 다음 사항을 준수해야 합니다:

1.  **네이버 지도 SDK 라이선스**: 네이버 지도 SDK의 저작권은 (주)네이버 및 네이버클라우드(주)에 있습니다. SDK 사용과 관련된 자세한 라이선스는 [네이버 지도 SDK 공식 문서](https://navermaps.github.io/android-map-sdk/guide-ko/1.html)를 참조하세요.
2.  **이용 약관 준수**: 본 라이브러리를 사용하는 서비스는 [네이버 클라우드 플랫폼 이용 약관](https://www.ncloud.com/policy/terms) 및 [Maps 서비스 이용 약관](https://www.ncloud.com/product/applicationService/maps)을 준수해야 합니다. 특히 **로고 노출 의무** 및 **사용량 제한**에 주의하시기 바랍니다.
3.  **책임 제한**: 본 라이브러리는 오픈 소스 소프트웨어로서 "있는 그대로" 제공되며, 네이버 지도 SDK 자체의 장애나 약관 위반으로 인한 문제에 대해서는 책임을 지지 않습니다.

---

Copyright 2026 Jun Cho
