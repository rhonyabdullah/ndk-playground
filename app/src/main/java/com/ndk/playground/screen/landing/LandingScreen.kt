package com.ndk.playground.screen.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ndk.playground.NdkPlaygroundScreen
import com.ndk.playground.ui.theme.NDKPlaygroundTheme
import com.ndk.playground.ui.theme.Padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onBack: () -> Unit,
    onBitmapClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(NdkPlaygroundScreen.LANDING.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LandingItems(
                name = "Bitmap Compression",
                action = onBitmapClicked
            )
        }
    }
}

@Composable
private fun LandingItems(
    name: String,
    action: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.small)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.padding(vertical = Padding.small),
            onClick = action
        ) {
            Text(name)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LandingScreenPreview() {
    NDKPlaygroundTheme {
        LandingScreen(
            onBack = { },
            onBitmapClicked = { }
        )
    }
}
