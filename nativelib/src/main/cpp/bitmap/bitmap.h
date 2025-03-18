//
// Created by Rhony on 12/03/25.
//

#ifndef HI_NDK_BITMAP_H
#define HI_NDK_BITMAP_H

#include <jni.h>

jbyteArray compressBitmap(JNIEnv *env, jobject bitmap, jint format, jint quality);

#endif //HI_NDK_BITMAP_H
