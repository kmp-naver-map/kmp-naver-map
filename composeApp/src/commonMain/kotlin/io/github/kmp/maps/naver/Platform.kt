package io.github.kmp.maps.naver

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform