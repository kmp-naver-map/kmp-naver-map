package io.github.kmp.maps.naver.compose.internal

import platform.UIKit.UIColor

internal fun Int.toUIColor(): UIColor {
    val alpha = ((this shr 24) and 0xFF) / 255.0
    val red = ((this shr 16) and 0xFF) / 255.0
    val green = ((this shr 8) and 0xFF) / 255.0
    val blue = (this and 0xFF) / 255.0
    return UIColor.colorWithRed(red, green, blue, alpha)
}