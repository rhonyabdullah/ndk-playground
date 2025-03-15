//
// Created by Rhony on 15/03/25.
//

#include "logging.h"
#include <android/log.h>
#include <jni.h>

void log_print(JNIEnv* env, jint priority, jstring tag, jstring message) {
    const char* tagStr = env->GetStringUTFChars(tag, nullptr);
    const char* msgStr = env->GetStringUTFChars(message, nullptr);

    if (tagStr && msgStr) {
        __android_log_print(priority, tagStr, "%s", msgStr);
    }

    env->ReleaseStringUTFChars(tag, tagStr);
    env->ReleaseStringUTFChars(message, msgStr);
}
