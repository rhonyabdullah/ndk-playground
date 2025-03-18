package com.ndk.nativelib

import android.graphics.Bitmap

internal object NativeLib : BitmapUtils {

    init {
        System.loadLibrary("nativelib")
    }

    external fun logV(tag: String, message: String)
    external fun logD(tag: String, message: String)
    external fun logI(tag: String, message: String)
    external fun logW(tag: String, message: String)
    external fun logE(tag: String, message: String)

    external override fun compressBitmap(bitmap: Bitmap, format: Int, quality: Int): ByteArray?
}