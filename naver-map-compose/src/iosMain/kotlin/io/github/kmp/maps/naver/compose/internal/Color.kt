package io.github.kmp.maps.naver.compose.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreGraphics.CGFloatVar
import platform.UIKit.UIColor

internal fun Int.toUIColor(): UIColor {
    val alpha = ((this shr 24) and 0xFF) / 255.0
    val red = ((this shr 16) and 0xFF) / 255.0
    val green = ((this shr 8) and 0xFF) / 255.0
    val blue = (this and 0xFF) / 255.0
    return UIColor.colorWithRed(red, green, blue, alpha)
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIColor.toArgbInt(): Int = memScoped {
    val r = alloc<CGFloatVar>()
    val g = alloc<CGFloatVar>()
    val b = alloc<CGFloatVar>()
    val a = alloc<CGFloatVar>()
    getRed(r.ptr, green = g.ptr, blue = b.ptr, alpha = a.ptr)
    val alpha = (a.value * 255).toInt() and 0xFF
    val red = (r.value * 255).toInt() and 0xFF
    val green = (g.value * 255).toInt() and 0xFF
    val blue = (b.value * 255).toInt() and 0xFF
    (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}