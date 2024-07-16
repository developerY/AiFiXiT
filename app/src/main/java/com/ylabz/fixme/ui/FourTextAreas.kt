package com.ylabz.fixme.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ylabz.fixme.MLEvent
import com.ylabz.fixme.R
import com.ylabz.fixme.images

@Composable
fun FourTextAreasTabs(
    geminiText: String,
    geminiText1: String,
    geminiText2: String,
    geminiText3: String,
    images: List<String>,
    speechText: String,
    textFieldValue: TextFieldValue,
    onEvent: (MLEvent) -> Unit,
    errorMessage: String,
    showError: Boolean = false,
    onErrorDismiss: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }


    val tabNames = listOf("Text", "Bug", "Local", "Vid")

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
                        Text(text = "Dismiss")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) { Text(text = errorMessage) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> PromptSection(images, geminiText, "Fix", "How to fix", onEvent, textFieldValue, speechText, onErrorDismiss)
                1 -> PromptSection(images, geminiText, "Parts", "What is the parts with price and budget", onEvent, textFieldValue, speechText, onErrorDismiss)
                2 -> PromptSection(images, geminiText, "Steps", "What are the steps to fix it", onEvent, textFieldValue, speechText, onErrorDismiss)
                3 -> PromptSection(images, geminiText, "Local", "What is a local business to help fix it", onEvent, textFieldValue, speechText, onErrorDismiss)
            }
        }
    }
}

@Composable
fun PromptSection(
    images: List<String>,
    ansText: String,
    buttonText: String,
    initialPrompt: String,
    onEvent: (MLEvent) -> Unit,
    textFieldValue: TextFieldValue,
    speechText: String,
    onErrorDismiss: () -> Unit
) {
    var isPromtVisible by rememberSaveable { mutableStateOf(false) }
    val icon = if (isPromtVisible) Icons.TwoTone.UnfoldLess else Icons.TwoTone.UnfoldMore
    val iconTint = if (isPromtVisible) Color.DarkGray else Color.Gray
    var prompt by remember { mutableStateOf(initialPrompt) }
    Column {
        if (isPromtVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (images.isNotEmpty()) {
                    val imagePath = images.last()
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Image Preview",
                            modifier = Modifier
                                .size(43.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shadow(4.dp, MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                TextField(
                    value = prompt,
                    label = { Text(stringResource(R.string.label_prompt)) },
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        .shadow(4.dp, MaterialTheme.shapes.medium)
                )
                //Spacer(modifier = Modifier.weight(0.1f))
            }
        }
        Row {
            Icon(
                imageVector = icon,
                contentDescription = "Expand/Collapse",
                //tint = iconTint,
                modifier = Modifier
                    //.weight(.5f)
                    //.size(9.dp) // Adjust the size as needed
                    .clickable {
                        isPromtVisible = !isPromtVisible
                    } // Toggle visibility on click
            )
            //Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val completePrompt = prompt + " " + speechText
                    try {
                        if (images.isNotEmpty()) {
                            val imagePath = images.last()
                            val bitmap = BitmapFactory.decodeFile(imagePath)
                            onEvent(MLEvent.GenAiResponseImg(completePrompt, bitmap))
                        } else {
                            onEvent(MLEvent.GenAiResponseTxt(completePrompt))
                        }
                    } catch (e: Exception) {
                        onErrorDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = buttonText)
            }
        }

        //val scrollState = rememberScrollState()
        Text(
            text = ansText,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                //.verticalScroll(scrollState)
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .shadow(4.dp, MaterialTheme.shapes.medium)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFourTextAreasTabs() {
    FourTextAreasTabs(
        geminiText = "text1",
        geminiText1 = "text2",
        geminiText2 = "text3",
        geminiText3 = "text4",
        images = listOf("imagePath"),
        speechText = "speechText",
        textFieldValue = TextFieldValue("textFieldValue"),
        onEvent = {},
        errorMessage = "Error occurred",
        showError = true,
        onErrorDismiss = {}
    )
}



