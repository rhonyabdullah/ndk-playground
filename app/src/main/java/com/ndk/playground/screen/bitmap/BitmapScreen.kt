package com.ndk.playground.screen.bitmap

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ndk.playground.NdkPlaygroundScreen
import com.ndk.playground.ui.theme.Padding
import com.ndk.playground.utils.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    val numberRegex = Regex("[^0-9.]")
    var compressedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showFlash by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var compressionQuality: String by remember { mutableStateOf("0") }

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

    LaunchedEffect(capturedBitmap, compressedBitmap) {
        showBottomSheet = capturedBitmap != null && compressedBitmap != null
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
                },
                actions = {
                    TextField(
                        modifier = Modifier.width(60.dp),
                        value = compressionQuality,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            // regex to remove non number value
                            compressionQuality = it.replace(numberRegex, "").let { cleanedInput ->
                                when {
                                    cleanedInput.isEmpty() -> "0"
                                    cleanedInput.toInt() > 100 -> "100"
                                    cleanedInput.toInt() < 0 -> "0"
                                    else -> cleanedInput.removePrefix("0")
                                }
                            }
                        },
                        singleLine = true,
                    )
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
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
                        .background(if (isLoading) Color.Gray else Color.Red)
                        .clickable {
                            if (isLoading) return@clickable
                            scope.launch(Dispatchers.IO) {
                                isLoading = true
                                showFlash = true
                                delay(115)
                                showFlash = false
                                capturedBitmap = imageProcessor.captureImage(imageCapture)
                                compressedBitmap = imageProcessor.compressBitmap(
                                    bitmap = capturedBitmap!!,
                                    compressQuality = compressionQuality.toInt()
                                )
                                isLoading = false
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(70.dp),
                            color = Color.White,
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = showFlash,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.7f))
                )
            }

            if (showBottomSheet) {
                ShowCapturedImage(
                    originalBitmap = capturedBitmap!!,
                    compressedBitmap = compressedBitmap!!,
                    imageProcessor = imageProcessor,
                    onDismiss = {
                        capturedBitmap = null
                        compressedBitmap = null
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowCapturedImage(
    originalBitmap: Bitmap,
    compressedBitmap: Bitmap,
    imageProcessor: ImageProcessor,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    val hideModalBottomSheet: () -> Unit = {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion { onDismiss() }
    }
    val context = LocalContext.current
    ModalBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        containerColor = Color.White,
        onDismissRequest = hideModalBottomSheet
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val (
                titleText,
                titleDivider,
                imageRow,
                btnSaveImage
            ) = createRefs()

            Text("Image Comparisons",
                Modifier
                    .constrainAs(titleText) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(titleDivider.top)
                    }
                    .padding(bottom = Padding.large),
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(Modifier.constrainAs(titleDivider) {
                top.linkTo(titleText.bottom)
            })

            Row(modifier = Modifier
                .constrainAs(imageRow) {
                    top.linkTo(titleDivider.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .wrapContentHeight()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),

                    ) {
                    Text(
                        "Original",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Padding.medium),
                        textAlign = TextAlign.Center
                    )
                    Image(
                        modifier = Modifier
                            .padding(top = Padding.medium),
                        bitmap = originalBitmap.asImageBitmap(),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.size(Padding.small))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                ) {
                    Text(
                        "Compressed",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Padding.medium),
                        textAlign = TextAlign.Center
                    )
                    Image(
                        modifier = Modifier
                            .padding(top = Padding.medium),
                        bitmap = compressedBitmap.asImageBitmap(),
                        contentDescription = null
                    )
                }
            }

            Button(
                modifier = Modifier.constrainAs(btnSaveImage) {
                    top.linkTo(imageRow.bottom, margin = Padding.medium)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = Padding.large)
                },
                onClick = {
                    imageProcessor.saveBitmapToGallery(originalBitmap, "original")
                    imageProcessor.saveBitmapToGallery(compressedBitmap, "compressed")
                    Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
                    hideModalBottomSheet()
                }
            ) {
                Text("Save Image & Dismiss")
            }

        }
    }
}

