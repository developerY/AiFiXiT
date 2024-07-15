package com.ylabz.fixme.ui

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TabsVid() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val texts = remember {
        List(3) { mutableStateOf(TextFieldValue("Text ${it + 1}")) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            repeat(4) { index ->
                Tab(
                    text = { Text("Tab ${index + 1}") },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
        when (selectedTabIndex) {
            in 0..2 -> {
                val scrollState = rememberScrollState()
                BasicTextField(
                    value = texts[selectedTabIndex].value,
                    onValueChange = { texts[selectedTabIndex].value = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                )
            }
            3 -> Text("here")//YouTubeVideoView(videoId = "gBP1nP4NtCk")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTabsVid() {
    TabsVid()
}

