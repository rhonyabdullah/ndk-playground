#include <jni.h>
#include <string>
#include <logging.h>
#include <android/log.h>
#include <bitmap.h>

extern "C" JNIEXPORT void JNICALL
Java_com_ndk_nativelib_NativeLib_logV(
        JNIEnv *env,
        jobject /* this */,
        jstring tag,
        jstring message) {
    log_print(env, ANDROID_LOG_VERBOSE, tag, message);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ndk_nativelib_NativeLib_logD(
        JNIEnv *env,
        jobject /* this */,
        jstring tag,
        jstring message) {
    log_print(env, ANDROID_LOG_DEBUG, tag, message);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ndk_nativelib_NativeLib_logI(
        JNIEnv *env,
        jobject /* this */,
        jstring tag,
        jstring message) {
    log_print(env, ANDROID_LOG_INFO, tag, message);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ndk_nativelib_NativeLib_logW(
        JNIEnv *env,
        jobject /* this */,
        jstring tag,
        jstring message) {
    log_print(env, ANDROID_LOG_WARN, tag, message);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ndk_nativelib_NativeLib_logE(
        JNIEnv *env,
        jobject /* this */,
        jstring tag,
        jstring message) {
    log_print(env, ANDROID_LOG_ERROR, tag, message);
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_ndk_nativelib_NativeLib_compressBitmap(
        JNIEnv *env,
        jobject /* this */,
        jobject bitmap,
        jint format,
        jint quality) {
    return compressBitmap(env, bitmap, format, quality);
}
