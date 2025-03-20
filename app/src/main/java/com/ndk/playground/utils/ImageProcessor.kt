package com.ndk.playground.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.takePicture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.ndk.nativelib.BitmapUtils
import com.ndk.nativelib.LOG_D
import com.ndk.playground.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bitmapUtils: BitmapUtils
) {

    suspend fun captureImage(
        imageCapture: ImageCapture
    ): Bitmap {
        val proxy = imageCapture.takePicture()

        val bitmapResult = if (proxy.imageInfo.rotationDegrees != 0) {
            val bitmap = proxy.toBitmap()
            val matrix = Matrix()
            matrix.postRotate(proxy.imageInfo.rotationDegrees.toFloat())
            LOG_D("bitmap rotated: ${proxy.imageInfo.rotationDegrees} degrees")
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else proxy.toBitmap()

        LOG_D("bitmap result: ${bitmapResult.byteCount}")

        proxy.close()
        return bitmapResult
    }

    suspend fun compressBitmap(
        bitmap: Bitmap,
        compressQuality: Int
    ): Bitmap = suspendCoroutine { continuation ->
        val compressedBytes = bitmapUtils.compressBitmap(
            bitmap,
            Bitmap.CompressFormat.JPEG.ordinal,
            compressQuality
        )!!
        continuation.resume(
            BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(context).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(context))
            }
        }

    fun saveBitmapToGallery(bitmap: Bitmap, name: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$name-${getFormattedTimestamp()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/${context.resources.getString(R.string.app_name)}"
            )
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.let { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            }

            context.contentResolver.update(uri, contentValues, null, null)
            LOG_D("Image saved to gallery: $uri")
        }

    }


    private fun getFormattedTimestamp(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("EEE-dd-MM-yyyy-hh-mm-a", Locale.getDefault())
        return dateFormat.format(Date(currentTimeMillis)).lowercase()
    }
}
