package io.github.kmp.maps.naver.compose.overlay

import androidx.compose.runtime.Composable
import kotlinx.coroutines.sync.Semaphore

/**
 * URL 마커 이미지 **원본 바이트**의 디스크 캐시.
 *
 * [OverlayImageCache]는 메모리 전용(프로세스 생존 동안)이라 앱을 재시작하면
 * 모든 마커 이미지를 다시 다운로드해야 한다. 이 캐시는 다운로드한 원본 바이트를
 * cacheKey 기반으로 디스크에 보관해, 재시작 후 첫 지도 진입에서도 네트워크 없이
 * 즉시 복원되게 한다.
 *
 * - 합성 결과(teardrop)가 아니라 **원본**을 저장한다 — 스타일 파라미터가 달라도 재사용 가능.
 * - 키는 호출부가 넘긴 cacheKey. Signed URL은 서명 쿼리를 뗀 안정 키를 넘겨야
 *   재시작 간 캐시가 적중한다(그렇지 않으면 세션마다 키가 바뀌어 캐시가 무의미해짐).
 * - 저장/조회 실패는 조용히 무시된다(베스트 에포트). 실패 시 기존 네트워크 경로로 동작.
 */
internal expect object ImageDiskCache {
    /** 캐시된 원본 이미지 바이트. 없거나 읽기 실패 시 null. */
    fun read(key: String): ByteArray?

    /** 원본 이미지 바이트 저장. 실패는 무시된다. */
    fun write(key: String, bytes: ByteArray)
}

/**
 * 플랫폼별 디스크 캐시 준비 훅. 컴포지션에서 1회 호출된다.
 * Android는 여기서 applicationContext를 확보한다(iOS는 no-op).
 */
@Composable
internal expect fun InitImageDiskCache()

/**
 * 마커 이미지 원본을 **디스크 캐시에만** 미리 받아둔다.
 *
 * 오버레이 합성이나 지도 SDK 객체 생성을 하지 않으므로 지도 진입 전
 * 백그라운드 프리페치에 안전하다. (지도 SDK 객체를 지도 초기화 전에
 * 백그라운드 스레드에서 만들면 SDK 네이티브가 잘못된 스레드에서 초기화되어
 * 이후 지도 진입 시 크래시할 수 있다 — Android에서 실제 발생.)
 *
 * 이미 캐시돼 있으면 네트워크 없이 즉시 반환한다. 실패는 조용히 무시된다.
 *
 * @param cacheKey Signed URL은 서명 쿼리를 뗀 안정 키를 전달할 것.
 */
suspend fun prefetchOverlayImageToDisk(url: String, cacheKey: String = url) {
    prefetchSemaphore.acquire()
    try {
        if (ImageDiskCache.read(cacheKey) != null) return
        val bytes = downloadImageBytes(url) ?: return
        ImageDiskCache.write(cacheKey, bytes)
    } finally {
        prefetchSemaphore.release()
    }
}

/** 프리페치 동시 다운로드 상한 — [OverlayImageCache]의 다운로드 제한과 동일한 취지. */
private val prefetchSemaphore = Semaphore(4)

/** URL에서 이미지 바이트를 내려받는다. 실패(네트워크·HTTP 오류) 시 null. */
internal expect suspend fun downloadImageBytes(url: String): ByteArray?

/**
 * 캐시 키를 파일시스템에 안전한 파일명으로 변환 (FNV-1a 64bit hex).
 * 키에는 URL 경로 등 임의 문자가 들어올 수 있으므로 해시로 고정한다.
 */
internal fun diskCacheFileName(key: String): String {
    var hash = 0xcbf29ce484222325uL
    for (ch in key) {
        hash = hash xor ch.code.toULong()
        hash *= 0x100000001b3uL
    }
    return hash.toString(16)
}
