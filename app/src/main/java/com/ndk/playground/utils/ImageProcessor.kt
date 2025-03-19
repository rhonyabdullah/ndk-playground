package com.ndk.playground.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.ndk.nativelib.BitmapUtils
import com.ndk.nativelib.LOG_D
import com.ndk.nativelib.LOG_E
import com.ndk.playground.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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
    ): Pair<Bitmap, File>? = suspendCoroutine { continuation ->
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ),
            "captured-${getFormattedTimestamp()}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    this@ImageProcessor.LOG_D("Image successfully captured")
                    continuation.resume(BitmapFactory.decodeFile(file.absolutePath) to file)
                }

                override fun onError(exception: ImageCaptureException) {
                    this@ImageProcessor.LOG_E("Image capture failed: ${exception.message}")
                    continuation.resume(null)
                }

            })
    }

    suspend fun processImage(
        originalBitmap: Bitmap,
        originalFile: File,
        compressQuality: Int
    ): Bitmap? = suspendCoroutine { continuation ->
        val rotatedBitmap = rotateBitmapIfRequired(originalFile, originalBitmap)
        saveBitmapToGallery(rotatedBitmap, originalFile.name)

        LOG_D("Original Image (Fixed) Saved: ${originalFile.absolutePath} | Size: ${originalFile.length()} bytes")

        val compressedBytes = bitmapUtils.compressBitmap(
            rotatedBitmap,
            Bitmap.CompressFormat.JPEG.ordinal,
            compressQuality
        )

        if (compressedBytes != null) {
            val compressedFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "compressed-${getFormattedTimestamp()}.jpg"
            )
            FileOutputStream(compressedFile).use { it.write(compressedBytes) }

            LOG_D("Compressed Image Saved: ${compressedFile.absolutePath} | Size: ${compressedFile.length()} bytes")

            val originalSize = originalFile.length()
            val compressedSize = compressedFile.length()
            val reduction = ((originalSize - compressedSize) / originalSize.toFloat()) * 100

            LOG_D("Compression Reduction: $reduction% (from $originalSize bytes to $compressedSize bytes)")

            val newBitmap = BitmapFactory.decodeFile(compressedFile.absolutePath)
            saveBitmapToGallery(newBitmap, compressedFile.name)
            continuation.resume(newBitmap)
        } else {
            LOG_D("Compression failed!")
            continuation.resume(null)
        }
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

    private fun rotateBitmapIfRequired(file: File, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(FileInputStream(file))
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val rotationAngle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        return if (rotationAngle != 0f) {
            val matrix = Matrix()
            matrix.postRotate(rotationAngle)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap, name: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
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
