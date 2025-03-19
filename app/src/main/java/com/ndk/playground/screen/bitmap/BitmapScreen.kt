package com.ndk.playground.screen.bitmap

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ndk.playground.NdkPlaygroundScreen
import com.ndk.playground.utils.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.camera.core.Preview as CameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitmapScreen(
    onBack: () -> Unit,
    imageProcessor: ImageProcessor
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preview = remember { CameraPreview.Builder().build() }
    val previewView = remember {
        PreviewView(context)
    }

    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    var compressedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var isPermissionGranted = true

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            isPermissionGranted = granted
            if (!granted) {
                Log.e("CameraX", "Camera permission denied")
            }
        }
    )

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted) {
            imageProcessor.getCameraProvider()
                .bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
            preview.surfaceProvider = previewView.surfaceProvider
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(NdkPlaygroundScreen.BITMAP.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Blue)
        ) {
            val (previewRef, btnCapture) = createRefs()
            val bottomGuideline = createGuidelineFromBottom(0.05f)
            AndroidView(
                { previewView },
                modifier = Modifier
                    .constrainAs(previewRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .border(2.dp, Color.White, CircleShape)
                    .size(80.dp)
                    .clip(CircleShape)
                    .constrainAs(btnCapture) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(bottomGuideline)
                    }
                    .background(Color.Red)
                    .clickable {
                        scope.launch(Dispatchers.IO) {
                            val captured = imageProcessor.captureImage(imageCapture)
                            if (captured != null) {
                                compressedBitmap = imageProcessor.processImage(
                                    originalBitmap = captured.first,
                                    originalFile = captured.second,
                                    compressQuality = 80
                                )

                            }
                        }

                    }
            )
        }
    }
}
