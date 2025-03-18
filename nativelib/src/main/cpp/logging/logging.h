//
// Created by Rhony on 15/03/25.
//

#ifndef NDK_PLAYGROUND_LOGGING_H
#define NDK_PLAYGROUND_LOGGING_H

#include <jni.h>
#include <android/log.h>

namespace {
    inline void LogPrint(jint priority, const char *tag, const char *message) {
        __android_log_print(priority, tag, "%s", message);
    }
}

void log_print(JNIEnv *env, jint priority, jstring tag, jstring message);

#endif //NDK_PLAYGROUND_LOGGING_H
