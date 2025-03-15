package com.ndk.nativelib

@Suppress("FunctionName")
fun LOG_V(tag: String, message: String) = NativeLib.logV(tag, message)

@Suppress("FunctionName")
fun<T : Any> T.LOG_V(message: String) = NativeLib.logV(this::class.java.simpleName, message)

@Suppress("FunctionName")
fun<T : Any> T.LOG_D(message: String) = NativeLib.logD(this::class.java.simpleName, message)

@Suppress("FunctionName")
fun<T : Any> T.LOG_I(message: String) = NativeLib.logI(this::class.java.simpleName, message)

@Suppress("FunctionName")
fun<T : Any> T.LOG_W(message: String) = NativeLib.logW(this::class.java.simpleName, message)

@Suppress("FunctionName")
fun<T : Any> T.LOG_E(message: String) = NativeLib.logE(this::class.java.simpleName, message)
