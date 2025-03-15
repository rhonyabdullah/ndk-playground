#include <jni.h>
#include <string>
#include <logging.h>
#include <android/log.h>

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
