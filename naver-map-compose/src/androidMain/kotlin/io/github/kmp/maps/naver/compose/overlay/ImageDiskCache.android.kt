package io.github.kmp.maps.naver.compose.overlay

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import java.io.File

/**
 * 디스크 캐시가 사용할 applicationContext.
 * [InitImageDiskCache]가 컴포지션 시점에 채운다 — 마커 이미지 로드는 항상
 * 컴포저블([rememberRoundOverlayImageFromUrl])에서 시작되므로 로드 전에 준비된다.
 */
internal object AppContextHolder {
    @Volatile
    var appContext: Context? = null
}

/**
 * 디스크 캐시를 컴포지션 밖(앱 시작 직후 프리페치 등)에서도 쓰려면 앱 시작 시 호출하세요.
 * 컴포저블 경로([rememberRoundOverlayImageFromUrl] 등)만 쓴다면 자동 초기화되므로 불필요합니다.
 */
fun initNaverMapImageDiskCache(context: Context) {
    AppContextHolder.appContext = context.applicationContext
}

@Composable
internal actual fun InitImageDiskCache() {
    val context = LocalContext.current.applicationContext
    SideEffect { AppContextHolder.appContext = context }
}

internal actual object ImageDiskCache {
    private const val DIR_NAME = "kmp_naver_map_images"

    /** 파일 수 상한. 초과 시 먼저 저장된 파일부터 정리한다(마커 썸네일 수백 KB 수준 가정). */
    private const val MAX_FILES = 300

    /**
     * 캐시 유효 기간. 서버가 같은 경로로 이미지를 교체해도 이 기간이 지나면
     * 다시 받아온다. mtime(=저장 시각) 기준이므로 read에서 mtime을 갱신하지 않는다.
     */
    private const val TTL_MILLIS = 7L * 24 * 60 * 60 * 1000

    private fun dir(): File? = AppContextHolder.appContext?.cacheDir
        ?.let { File(it, DIR_NAME) }
        ?.apply { if (!exists()) mkdirs() }

    actual fun read(key: String): ByteArray? = try {
        dir()?.let { d ->
            val file = File(d, diskCacheFileName(key))
            when {
                !file.exists() -> null
                System.currentTimeMillis() - file.lastModified() > TTL_MILLIS -> {
                    runCatching { file.delete() }
                    null
                }
                else -> file.readBytes()
            }
        }
    } catch (_: Throwable) {
        null
    }

    actual fun write(key: String, bytes: ByteArray) {
        try {
            val d = dir() ?: return
            // 임시 파일 후 rename — 도중 크래시로 깨진 파일이 남지 않도록.
            // 같은 이미지를 다른 스타일 파라미터로 동시에 로드하면 같은 key에 동시 쓰기가
            // 가능하므로(in-flight 중복 방지는 fullCacheKey 단위) tmp 파일명은 유니크하게.
            val tmp = File.createTempFile("dl", ".tmp", d)
            tmp.writeBytes(bytes)
            if (!tmp.renameTo(File(d, diskCacheFileName(key)))) {
                runCatching { tmp.delete() }
            }
            prune(d)
        } catch (_: Throwable) {
            // 베스트 에포트: 저장 실패는 무시
        }
    }

    private fun prune(d: File) {
        val files = d.listFiles() ?: return
        if (files.size <= MAX_FILES) return
        files.sortedBy { it.lastModified() }
            .take(files.size - MAX_FILES)
            .forEach { runCatching { it.delete() } }
    }
}
