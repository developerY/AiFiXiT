package com.ylabz.fixme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
fun FourTextAreas(
    texts: List<String>
) {
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        repeat(4) { index ->
            val scrollState = rememberScrollState()
            var text by remember { mutableStateOf(texts.getOrElse(index) { "" }) }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor, textAlign = TextAlign.Start),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            )
        }
    }
}

@Composable
@Preview
fun FourTextAreasPreview() {
    MaterialTheme {
        FourTextAreas(texts = listOf("Text 1", "Text 2", "Text 3", "Text 4"))
    }
}


@Composable
fun FourTextAreasTabs(
    geminiText: String,
    geminiText1: String,
    geminiText2: String,
    geminiText3: String
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val texts = remember {
        listOf(
            mutableStateOf(TextFieldValue(geminiText)),
            mutableStateOf(TextFieldValue(geminiText1)),
            mutableStateOf(TextFieldValue(geminiText2)),
            mutableStateOf(TextFieldValue(geminiText3))
        )
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
        val scrollState = rememberScrollState()
        /*if (selectedTabIndex == 3) {
            YouTubeVideoView(videoId = "dQw4w9WgXcQ")
        } else {*/
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
       // }
    }
}

@Composable
fun YouTubeVideoView(videoId: String) {
    val context = LocalContext.current

    AndroidView(factory = { ctx ->
        val webView = android.webkit.WebView(ctx).apply {
            settings.javaScriptEnabled = true
            webViewClient = android.webkit.WebViewClient()
            loadUrl("https://www.youtube.com/embed/$videoId")
        }
        webView
    }, update = { webView ->
        webView.loadUrl("https://www.youtube.com/embed/$videoId")
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewFourTextAreasTabs() {
    FourTextAreasTabs("text1", "text2", "text3", "text4")
}


@Composable
fun FourTextAreasVertical() {
    val texts = remember {
        List(4) { mutableStateOf(TextFieldValue("Text ${it + 1}")) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        texts.forEachIndexed { index, textState ->
            BasicTextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFourTextAreasVertical() {
    FourTextAreasVertical()
}

