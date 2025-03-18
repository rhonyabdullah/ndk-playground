//
// Created by Rhony on 16/03/25.
//

#include <jni.h>
#include "android/bitmap.h"
#include <vector>
#include <logging.h>

#define LOG_TAG "NDK_BitmapCompress"

#ifndef ANDROID_DATASPACE_SRGB
#define ANDROID_DATASPACE_SRGB 142671872 // Equivalent to Dataspace SRGB
#endif

// Struct to store compression data
struct CompressionContext {
    JNIEnv *env;
    std::vector<uint8_t> buffer;
};

// Write callback function for compressed data
bool writeCompressedData(void *userContext, const void *data, size_t size) {
    if (userContext == nullptr || data == nullptr || size == 0) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG, "Invalid write callback parameters");
        return false;
    }
    auto *ctx = static_cast<CompressionContext *>(userContext);
    ctx->buffer.insert(ctx->buffer.end(), (uint8_t *) data, (uint8_t *) data + size);

    LogPrint(ANDROID_LOG_DEBUG, LOG_TAG, "Compressed Data Written");
    return true;
}

jbyteArray compressBitmap(JNIEnv *env, jobject bitmap, jint format, jint quality) {
    if (bitmap == nullptr) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG, "Bitmap is null");
        return nullptr;
    }

    if (format != ANDROID_BITMAP_COMPRESS_FORMAT_JPEG &&
        format != ANDROID_BITMAP_COMPRESS_FORMAT_WEBP_LOSSY) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG,
                 "Invalid format, use JPEG or WebP LOSSY for compression");
        return nullptr;
    }

    AndroidBitmapInfo info;
    void *pixels;

    // Get Bitmap Info
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG, "Failed to get bitmap info");
        return nullptr;
    }

    // Check if the format is supported
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 &&
        info.format != ANDROID_BITMAP_FORMAT_RGB_565) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG, "Unsupported bitmap format");
        return nullptr;
    }

    // Lock Pixels
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG, "Failed to lock pixels");
        return nullptr;
    }

    // Prepare compression context
    CompressionContext ctx = {env, {}};

    // Set correct dataspace
    int32_t dataspace = ANDROID_DATASPACE_SRGB;

    // Compress the Bitmap
    int result = AndroidBitmap_compress(&info, dataspace, pixels, format, quality, &ctx,
                                        writeCompressedData);

    // Unlock the Bitmap pixels
    AndroidBitmap_unlockPixels(env, bitmap);

    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG, "Failed to compress bitmap");
        return nullptr;
    }

    // Convert buffer to Java byte array
    jbyteArray compressedData = env->NewByteArray(static_cast<jsize>(ctx.buffer.size()));
    if (!compressedData) {
        LogPrint(ANDROID_LOG_ERROR, LOG_TAG, "Failed to create jbyteArray");
        return nullptr;
    }

    env->SetByteArrayRegion(compressedData, 0, static_cast<jsize>(ctx.buffer.size()),
                            (jbyte *) ctx.buffer.data());

    return compressedData;
}
