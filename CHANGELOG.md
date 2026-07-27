# Changelog

이 프로젝트의 주요 변경 사항을 기록합니다. / Notable changes to this project.

## [1.2.2]

### Added (기능 추가)
- **createPawprintOverlayImage(widthDp, heightDp, color)**: 경로 패턴용 동물 발바닥(paw)을 코드로 그려 반환하는 함수 추가(Android/iOS). 발가락 4개와 둥근 삼각형 패드로 이루어진 발바닥 두 개가 ±12°로 번갈아 기울어져 걷는 듯한 좌우 리듬을 만듭니다. 산책로·반려동물 경로를 표현할 때 `patternImage`와 함께 사용합니다.

### Changed (동작 변경)
- **createFootprintOverlayImage 렌더링 및 크기 규칙 변경**: 자국 모양을 앞창·뒤굽이 분리된 구두 밑창으로 다시 그리고, 좌우 한 쌍이 ±8°로 번갈아 기울어지도록 했습니다. 함께 **크기 규칙이 바뀌었습니다** — 자국 크기는 이제 `widthDp`(=경로선 두께)에서만 결정되고, `heightDp`는 걸음 간격만 조절합니다(걸음 간격 = `heightDp / 2`).
  - 1.2.1에서 이 함수를 쓰던 코드는 `heightDp`와 `patternInterval` 값을 다시 잡아야 합니다.
  - 권장: `widthDp` = 경로선 두께, `heightDp` ≥ `widthDp` × 5.6, `patternInterval` = `heightDp`.
  - `heightDp`가 최소치보다 작으면 자국이 잘리는 대신 자동으로 축소됩니다.

### Fixed (버그 수정)
- **패턴 자국 간격이 두 개씩 뭉쳐 보이던 문제 수정**: 네이버 지도가 패턴 이미지의 **높이만** 약 0.788배로 줄여 그리는데(너비는 경로선 두께에 맞춤, 가로세로 비율 미보존) 이 축소가 `patternInterval`에는 적용되지 않아, 이미지 안 두 자국의 간격이 이미지 사이 간격보다 좁아지는 문제가 있었습니다. 두 자국을 이미지 높이의 약 18.3%/81.7% 지점에 배치해 축소를 상쇄했습니다.
  - 축소 비율은 Android 에뮬레이터(밀도 320~560dpi, 선 두께 12·16·20dp, 이미지 높이 24~40dp)와 iOS 시뮬레이터에서 0.784~0.793으로 측정되어, 플랫폼·밀도·크기와 무관한 SDK 상수임을 확인했습니다.
  - 보정 후 실측 걸음 간격: Android 14.75~14.93dp, iOS 14.67~15.00pt (목표 15).

## [1.2.1]

### Added (기능 추가)
- **createDirectionArrowOverlayImage strokeWidthDp**: 화살표 함수에 `strokeWidthDp` 파라미터 추가. 0(기본)이면 기존과 동일한 꽉 찬 chevron 삼각형, 0보다 크면 해당 굵기(dp)의 열린 꺾쇠(˄) 스트로크로 그립니다.
- **createFootprintOverlayImage(widthDp, heightDp, color)**: 경로 패턴용 발자국 한 쌍(왼발 아래·오른발 위, 발바닥 타원+발가락 원)을 코드로 그려 반환하는 함수 추가(Android/iOS). 도보 경로를 걸어가는 발자국으로 표현할 때 `patternImage`와 함께 사용합니다.

## [1.2.0]

### Added (기능 추가)
- **Path patternImage**: `PathOverlay`/`PathOptions`/`Path` 컴포저블에 `patternImage: OverlayImage?` 추가. 경로 위에 진행 방향으로 회전하며 반복되는 패턴 이미지를 표시합니다(네이버지도 앱 길찾기 화살표 스타일). Android는 네이티브 `patternImage`, iOS는 `patternIcon`에 바인딩되며, `patternInterval`과 함께 사용합니다.
- **createDirectionArrowOverlayImage(widthDp, heightDp, color)**: 경로 패턴용 진행 방향 화살표(chevron)를 에셋 없이 코드로 그려 `OverlayImage`로 반환하는 함수 추가(Android/iOS). 크기는 dp 기준으로 양 플랫폼 동일하게 렌더링됩니다.

## [1.1.0]

### Fixed (버그 수정)
- **MapEffect(vararg)**: 가변 인자 오버로드가 매 recomposition마다 effect를 취소·재시작하던 문제 수정 (`LaunchedEffect(keys)` → `LaunchedEffect(*keys)`).
- **Marker(position=…)**: 편의 오버로드가 `position` 인자 변경을 반영하지 못해 마커가 초기 위치에 고정되던 문제 수정. 이제 `position`이 바뀌면 마커가 따라 이동합니다.
- **onCameraChangeStarted (Android)**: Android에서 콜백이 전혀 호출되지 않던 문제 수정. 이제 카메라 이동 시작 시 1회 발화합니다(iOS와 동일).
- **moveCamera/animateCamera onFinish (Android)**: 임의의 카메라 idle에 `onFinish`가 조기·중복 호출될 수 있던 문제를, 해당 카메라 업데이트의 완료 콜백(`finishCallback`)으로 교체해 수정. 취소 시에는 호출되지 않습니다(iOS와 동일).
- **subCaptionRequestedWidth (Android)**: `captionRequestedWidth`와 달리 dp 변환이 누락돼 단위가 어긋나던 문제 수정.
- **Polyline pattern (Android)**: 점선 패턴이 네이티브에 반영되지 않아 무시되던 문제 수정. 래퍼가 `setPattern(...)`을 호출하지 않고 내부 값만 저장하고 있었음(실제 SDK는 `setPattern(int...)` 지원). 패턴 값은 dp로 해석합니다.

### Changed (동작 변경 — ⚠️ 업그레이드 시 확인)
- **도형 오버레이 선 두께 단위 통일 (Android)**: `Polyline`/`Polygon`/`Circle`/`Path`/`ArrowheadPath`의 `width`·`outlineWidth`·`patternInterval`·`elevation` 입력이 이제 마커와 동일하게 **dp**로 해석됩니다(기존에는 사실상 px). 고밀도 화면에서 선이 더 굵게 보일 수 있으니, 픽셀 값을 직접 넘기던 경우 dp 기준으로 조정하세요. (`Circle.radius`는 미터, `Path.progress`·`ArrowheadPath.headSizeRatio`는 비율이라 변환하지 않습니다.)
- **iOS Path/ArrowheadPath z-order**: 로컬 `zIndex`로 `globalZIndex`를 덮어쓰던 동작 제거 → SDK 기본 z-order 유지(Android와 일치).
- **latLngToScreen / screenToLatLng**: 투영 불가(화면 밖/지평선 너머 등) 좌표에서 이제 명세대로 `null`을 반환합니다.

### Internal (내부 리팩토링 — 동작 동일)
- 모든 오버레이의 옵션→네이티브 속성 매핑을 `applyOptions` 한 곳으로 일원화해 생성 시 이중 적용을 제거.
- iOS `PolygonOverlay`의 `coords`/`holes`를 backing field로 관리하도록 변경(상호 역참조로 인한 잠재 NPE 제거).
- iOS SDK 인증(`ncpKeyId`) 적용 경로를 `NaverMapView` 한 곳으로 일원화(이중 설정 제거).
