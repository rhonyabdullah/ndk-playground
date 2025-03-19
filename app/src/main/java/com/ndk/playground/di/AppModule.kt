package com.ndk.playground.di

import com.ndk.nativelib.BitmapUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun bindBitmapUtils(): BitmapUtils = BitmapUtils.instance

}
