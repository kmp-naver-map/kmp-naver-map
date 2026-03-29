package io.github.kmp.maps.naver.compose.overlay

import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

/**
 * URL 기반 OverlayImage 전용 LRU 캐시.
 *
 * - 최대 [MAX_SIZE]개 항목 유지 (초과 시 가장 오래된 항목 제거)
 * - 동일 키에 대한 동시 요청은 하나의 다운로드만 실행하고 나머지는 결과를 공유 (in-flight 중복 방지)
 */
internal object OverlayImageCache {

    private const val MAX_SIZE = 50

    /**
     * 동시 다운로드 수 상한. 카테고리 전환 시 수십 개 마커가 한꺼번에 요청해도
     * IO 스레드·메모리 사용이 한꺼번에 폭발하지 않도록 제어합니다.
     * 캐시 히트·in-flight 대기는 permit을 소모하지 않습니다.
     */
    private const val MAX_CONCURRENT_DOWNLOADS = 4
    private val downloadSemaphore = Semaphore(MAX_CONCURRENT_DOWNLOADS)

    // 접근 순서 기반 LRU: 조회 시 키를 삭제 후 재삽입
    private val map = LinkedHashMap<String, OverlayImage>()
    private val inFlight = HashMap<String, CompletableDeferred<OverlayImage?>>()
    private val mutex = Mutex()

    /**
     * [key]에 해당하는 이미지를 캐시에서 반환하거나, 없으면 [loader]를 실행하여 캐싱 후 반환합니다.
     *
     * 동일 [key]로 동시에 여러 코루틴이 호출되면, 첫 번째 코루틴만 [loader]를 실행하고
     * 나머지는 그 결과를 기다립니다.
     */
    suspend fun getOrLoad(key: String, loader: suspend () -> OverlayImage?): OverlayImage? {
        // 캐시 히트 또는 in-flight deferred 획득
        val existingDeferred: CompletableDeferred<OverlayImage?>? = mutex.withLock {
            // 캐시 히트: LRU 순서 갱신 후 즉시 반환
            map[key]?.let {
                map.remove(key)
                map[key] = it
                return it
            }
            // 이미 다운로드 중: 기존 deferred 반환
            inFlight[key]?.let { return@withLock it }
            // 새 요청: deferred 등록 후 로딩 담당 신호(null 반환)
            inFlight[key] = CompletableDeferred()
            null
        }

        // 다른 코루틴이 로딩 중이면 결과 대기
        if (existingDeferred != null) {
            return existingDeferred.await()
        }

        // 이 코루틴이 실제 로딩 담당 (Semaphore로 동시 다운로드 수 제한)
        val result = try {
            downloadSemaphore.withPermit { loader() }
        } catch (e: CancellationException) {
            // 구조적 동시성 유지: in-flight 정리 후 취소 재전파
            mutex.withLock { inFlight.remove(key)?.complete(null) }
            throw e
        } catch (e: Exception) {
            null
        }

        mutex.withLock {
            if (result != null) {
                map[key] = result
                if (map.size > MAX_SIZE) {
                    map.remove(map.keys.first())
                }
            }
            inFlight.remove(key)?.complete(result)
        }
        return result
    }

    /** 테스트 또는 메모리 해제 목적으로 캐시를 전체 초기화합니다. */
    suspend fun clear() = mutex.withLock {
        map.clear()
    }
}

/**
 * Placeholder(흰색 teardrop) 전역 동기 캐시.
 *
 * Compose의 `remember`는 컴포저블 인스턴스 스코프이므로, 동일 스타일의 마커가 100개 있으면
 * 흰색 teardrop 비트맵을 100번 그립니다. 이 캐시는 스타일 파라미터를 키로 하여
 * 동일 스타일 인스턴스를 전역에서 공유합니다.
 *
 * Compose 컴포지션은 항상 메인 스레드에서 실행되므로 별도 동기화가 필요 없습니다.
 */
internal object PlaceholderCache {
    private val map = HashMap<String, OverlayImage>()

    fun getOrCreate(key: String, creator: () -> OverlayImage?): OverlayImage? {
        map[key]?.let { return it }
        val value = creator() ?: return null
        map[key] = value
        return value
    }

    fun clear() = map.clear()
}
