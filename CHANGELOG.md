# Changelog

이 프로젝트의 주요 변경 사항을 기록합니다. / Notable changes to this project.

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
