package com.ylabz.fixme.ui

import android.graphics.BitmapFactory
import android.location.Location
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.fixme.MLEvent
import com.ylabz.fixme.R

@Composable
fun FourTextAreasTabs(
    geminiText: List<String>,
    image: String,
    speechText: String,
    location: Location?,
    textFieldValue: String,
    onEvent: (MLEvent) -> Unit,
    errorMessage: String,
    showError: Boolean = false,
    onErrorDismiss: () -> Unit,
    isLoading: Boolean = false // Add this parameter
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabNames = listOf("How", "Parts", "Steps", "Local")

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
            tabNames.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, color = MaterialTheme.colorScheme.onPrimary) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        if (showError) {
            Snackbar(
                action = {
                    TextButton(onClick = { onErrorDismiss() }) {
                        Text(
                            text = "Dismiss",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White
            ) {
                Text(
                    text = errorMessage,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        if (isLoading) {
            // Display a progress indicator if loading
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
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
                0 -> PromptSection(
                    selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "",
                    " 🔧   Fix It  ⚒️ ", "How to fix ", onEvent, textFieldValue, speechText, onErrorDismiss
                )
                1 -> PromptSection(
                    selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "",
                    " ✅   Parts   🧰 ", "What are the parts with price and budget ", onEvent, textFieldValue, speechText, onErrorDismiss
                )
                2 -> PromptSection(
                    selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "",
                    " 🏗️   Steps   📝 ", "What are the steps to fix ", onEvent, textFieldValue, speechText, onErrorDismiss
                )
                3 -> PromptSection(
                    selectedTabIndex, image, geminiText.getOrNull(selectedTabIndex) ?: "",
                    " 🗺️   Local    📍 ", "What is a local business around ${location.toString()} to get this ", onEvent, textFieldValue, speechText, onErrorDismiss
                )
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
    noteText: String,
    speechText: String,
    onErrorDismiss: () -> Unit
) {
    var isPromptVisible by rememberSaveable { mutableStateOf(false) }
    val icon = if (isPromptVisible) Icons.TwoTone.UnfoldLess else Icons.TwoTone.UnfoldMore
    var prompt = "Please explain $initialPrompt $speechText. Notes:$noteText. Thank you for your help!"

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
                    try {
                        if (images.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeFile(images)
                            onEvent(MLEvent.GenAiPromptResponseImg(prompt, bitmap, index))
                            //GenAiChatResponseImg
                            //onEvent(MLEvent.GenAiPromptResponseImg(promptState.value, bitmap, index))
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
        location = Location(""),
        textFieldValue = "textFieldValue",
        onEvent = {},
        errorMessage = "Error occurred",
        showError = true,
        onErrorDismiss = {}
    )
}
