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

**사용하려는 모듈(예: `composeApp`)**의 `build.gradle.kts`에 플러그인을 추가하세요. 플러그인을 적용하면 네이버 지도 저장소 설정과 라이브러리 의존성이 자동으로 추가됩니다.

```kotlin
// composeApp/build.gradle.kts 또는 shared/build.gradle.kts
plugins {
    id("io.github.kmp-naver-map") version "1.0.2"
}

// (선택 사항) 라이브러리 버전을 직접 지정하고 싶은 경우
naverMapCompose {
    version.set("1.0.2")
}
```

### 2. iOS 설정 (CocoaPods)

iOS에서 지도를 정상적으로 표시하려면 네이버 지도 SDK(`NMapsMap`) 의존성이 필요합니다. 자세한 내용은 [Kotlin Multiplatform CocoaPods 공식 문서](https://kotlinlang.org/docs/multiplatform-cocoapods-overview.html)를 참조하세요.

#### 옵션 A: build.gradle.kts에서 설정 (권장)
Kotlin Multiplatform의 `cocoapods` 플러그인을 사용 중이라면 아래와 같이 추가하세요.

```kotlin
kotlin {
    cocoapods {
        ios.deploymentTarget = "13.0"
        
        pod("NMapsMap") {
            version = "3.23.1"
        }
    }
}
```

#### 옵션 B: Podfile에 직접 추가
기존 iOS 프로젝트 형식을 유지하고 싶다면 `iosApp/Podfile`에 다음을 추가하세요.

```ruby
target 'iosApp' do
  use_frameworks!
  pod 'NMapsMap', '3.23.1'
end
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

---

## Troubleshooting / 문제 해결

| 증상 | 해결 방법 |
|------|----------|
| 지도가 표시되지 않음 | `NaverMapSdkProvider`가 `NaverMap`을 감싸고 있는지, Client ID가 올바른지 확인하세요. |
| iOS에서 빌드 오류 | `pod install` 실행 후 `.xcworkspace`로 열어야 합니다. `cocoapods` 플러그인 사용 시 `generateComposeResClass` 태스크 관련 오류가 나면 Gradle 버전을 확인하세요. |
| `Could not resolve` 의존성 오류 | Gradle 플러그인 사용 시 네이버 저장소가 자동 추가됩니다. 수동 설정 시 `https://repository.map.naver.com/archive/maven`을 추가하세요. |

---

## Contributing / 기여하기

기여는 언제나 환영합니다! 자세한 내용은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요.

---

## License / 라이선스

### 프로젝트 라이선스
본 라이브러리는 **Apache License 2.0**을 따릅니다.

---
Copyright 2026 Jun Cho
