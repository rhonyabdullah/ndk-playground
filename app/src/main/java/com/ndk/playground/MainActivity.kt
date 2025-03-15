package com.ndk.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ndk.nativelib.LOG_D
import com.ndk.nativelib.LOG_E
import com.ndk.nativelib.LOG_I
import com.ndk.nativelib.LOG_V
import com.ndk.nativelib.LOG_W
import com.ndk.playground.ui.theme.NDKPlaygroundTheme

class MainActivity : ComponentActivity() {

    private val logMessage = "Hello from MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NDKPlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        LOG_V("MainActivity", "Using tag: $logMessage")
        LOG_V(logMessage)
        LOG_D(logMessage)
        LOG_I(logMessage)
        LOG_W(logMessage)
        LOG_E(logMessage)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NDKPlaygroundTheme {
        Greeting("Android")
    }
}
