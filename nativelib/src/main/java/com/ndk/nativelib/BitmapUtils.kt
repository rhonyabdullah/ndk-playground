package com.ndk.nativelib

import android.graphics.Bitmap

interface BitmapUtils {
    fun compressBitmap(bitmap: Bitmap, format: Int, quality: Int): ByteArray?

    companion object {
        val instance: BitmapUtils by lazy { NativeLib }
    }
}
