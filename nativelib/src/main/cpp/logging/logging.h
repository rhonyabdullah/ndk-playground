//
// Created by Rhony on 15/03/25.
//

#ifndef NDK_PLAYGROUND_LOGGING_H
#define NDK_PLAYGROUND_LOGGING_H

#include <jni.h>

void log_print(JNIEnv* env, jint priority, jstring tag, jstring message);

#endif //NDK_PLAYGROUND_LOGGING_H
