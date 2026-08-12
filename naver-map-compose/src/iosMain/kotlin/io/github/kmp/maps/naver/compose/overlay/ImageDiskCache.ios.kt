@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.kmp.maps.naver.compose.overlay

import androidx.compose.runtime.Composable
import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithURL
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileModificationDate
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.posix.memcpy

@Composable
internal actual fun InitImageDiskCache() {
    // iOS는 Caches 디렉터리 접근에 별도 준비가 필요 없다.
}

internal actual suspend fun downloadImageBytes(url: String): ByteArray? =
    suspendCancellableCoroutine { continuation ->
        val nsUrl = NSURL.URLWithString(url)
            ?: run { continuation.resume(null); return@suspendCancellableCoroutine }
        val task = NSURLSession.sharedSession.dataTaskWithURL(nsUrl) { data, response, error ->
            val statusCode = (response as? NSHTTPURLResponse)?.statusCode?.toInt() ?: 200
            if (error != null || data == null || statusCode !in 200..299) {
                continuation.resume(null)
            } else {
                continuation.resume(data.toByteArray().takeIf { it.isNotEmpty() })
            }
        }
        task.resume()
        continuation.invokeOnCancellation { task.cancel() }
    }

internal actual object ImageDiskCache {
    private const val DIR_NAME = "kmp_naver_map_images"
    private const val MAX_FILES = 300

    /**
     * 캐시 유효 기간(초). 서버가 같은 경로로 이미지를 교체해도 이 기간이 지나면
     * 다시 받아온다. 수정 시각(=저장 시각) 기준.
     */
    private const val TTL_SECONDS = 7.0 * 24 * 60 * 60

    private fun dirPath(): String? {
        val base = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String ?: return null
        val path = "$base/$DIR_NAME"
        NSFileManager.defaultManager.createDirectoryAtPath(
            path, withIntermediateDirectories = true, attributes = null, error = null
        )
        return path
    }

    actual fun read(key: String): ByteArray? {
        val path = dirPath() ?: return null
        val filePath = "$path/${diskCacheFileName(key)}"
        val fm = NSFileManager.defaultManager
        val modified = fm.attributesOfItemAtPath(filePath, null)
            ?.get(NSFileModificationDate) as? NSDate
        if (modified != null &&
            NSDate().timeIntervalSince1970 - modified.timeIntervalSince1970 > TTL_SECONDS
        ) {
            fm.removeItemAtPath(filePath, null)
            return null
        }
        val data = NSData.dataWithContentsOfFile(filePath) ?: return null
        return data.toByteArray()
    }

    actual fun write(key: String, bytes: ByteArray) {
        val path = dirPath() ?: return
        // atomically=true — 도중 실패 시 깨진 파일이 남지 않도록
        bytes.toNSData().writeToFile("$path/${diskCacheFileName(key)}", atomically = true)
        prune(path)
    }

    private fun prune(path: String) {
        val fm = NSFileManager.defaultManager
        @Suppress("UNCHECKED_CAST")
        val names = fm.contentsOfDirectoryAtPath(path, null) as? List<String> ?: return
        if (names.size <= MAX_FILES) return
        // 정렬 기준 메타데이터 조회 비용을 피하고 이름순으로 단순 정리한다.
        // 해시 파일명이라 사실상 무작위 제거지만, 상한 유지 목적에는 충분하다.
        names.sorted().take(names.size - MAX_FILES).forEach { name ->
            fm.removeItemAtPath("$path/$name", null)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    if (isNotEmpty()) usePinned { memcpy(it.addressOf(0), bytes, length) }
}

@OptIn(ExperimentalForeignApi::class)
internal fun ByteArray.toNSData(): NSData = if (isEmpty()) {
    NSData()
} else {
    usePinned { NSData.dataWithBytes(it.addressOf(0), size.toULong()) }
}
