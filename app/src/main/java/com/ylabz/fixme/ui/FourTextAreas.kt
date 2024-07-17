package com.ylabz.fixme.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.UnfoldLess
import androidx.compose.material.icons.twotone.UnfoldMore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.fixme.MLEvent
import com.ylabz.fixme.R

@Composable
fun FourTextAreasTabs(
    geminiText: List<String>,
    image: String,
    speechText: String,
    textFieldValue: String,
    onEvent: (MLEvent) -> Unit,
    errorMessage: String,
    showError: Boolean = false,
    onErrorDismiss: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabNames = listOf("How", "Parts", "Steps", "Local")

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabNames.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        if (showError) {
            Snackbar(
                action = {
                    TextButton(onClick = { onErrorDismiss() }) {
                        Text(text = "Dismiss", color = MaterialTheme.colorScheme.onError)
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) { Text(text = errorMessage, color = MaterialTheme.colorScheme.onError) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> PromptSection(selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "", "Fix", "How to make", onEvent, textFieldValue, speechText, onErrorDismiss)
                1 -> PromptSection(selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "", "Parts", "What are the parts with price and budget", onEvent, textFieldValue, speechText, onErrorDismiss)
                2 -> PromptSection(selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "", "Steps", "What are the steps to fix it", onEvent, textFieldValue, speechText, onErrorDismiss)
                3 -> PromptSection(selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "", "Local", "What is a local business to help fix it", onEvent, textFieldValue, speechText, onErrorDismiss)
            }
        }
    }
}

@Composable
fun PromptSection(
    index: Int,
    images: String,
    ansText: String,
    buttonText: String,
    initialPrompt: String,
    onEvent: (MLEvent) -> Unit,
    textFieldValue: String,
    speechText: String,
    onErrorDismiss: () -> Unit
) {
    var isPromptVisible by rememberSaveable { mutableStateOf(false) }
    val icon = if (isPromptVisible) Icons.TwoTone.UnfoldLess else Icons.TwoTone.UnfoldMore
    var prompt by remember { mutableStateOf(initialPrompt) }

    Column {
        if (isPromptVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (images.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeFile(images)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Image Preview",
                            modifier = Modifier
                                .size(43.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                TextField(
                    value = prompt,
                    label = { Text(stringResource(id = R.string.label_prompt)) },
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Expand/Collapse",
                modifier = Modifier
                    .size(43.dp)
                    .clickable { isPromptVisible = !isPromptVisible }
                    .padding(end = 8.dp)
            )
            Button(
                onClick = {
                    val completePrompt = "$prompt $speechText"
                    try {
                        if (images.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeFile(images)
                            onEvent(MLEvent.GenAiResponseImg(completePrompt, bitmap, index))
                        }
                    } catch (e: Exception) {
                        onErrorDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = buttonText)
            }
        }

        Text(
            text = ansText,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFourTextAreasTabs() {
    FourTextAreasTabs(
        geminiText = listOf("How text", "Parts text", "Steps text", "Local text"),
        image = "imagePath",
        speechText = "speechText",
        textFieldValue = "textFieldValue",
        onEvent = {},
        errorMessage = "Error occurred",
        showError = true,
        onErrorDismiss = {}
    )
}



