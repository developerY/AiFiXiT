package com.ylabz.fixme.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    val texts = remember {
        listOf(
            mutableStateOf(TextFieldValue(geminiText)),
            mutableStateOf(TextFieldValue(geminiText1)),
            mutableStateOf(TextFieldValue(geminiText2)),
            mutableStateOf(TextFieldValue(geminiText3))
        )
    }

    val tabNames = listOf("Text", "Bug", "Local", "Vid")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            repeat(4) { index ->
                Tab(
                    text = { Text(tabNames[index]) },
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

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    var prompt by remember { mutableStateOf("This is the prompt") }
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp), // Use vertical padding for better spacing
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
                                            .size(43.dp) // Adjust size as needed
                                            .clip(MaterialTheme.shapes.medium)
                                            .shadow(4.dp, MaterialTheme.shapes.medium)
                                            .background(MaterialTheme.colorScheme.background) // Use background color
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(0.1f)) // Add a small spacer for separation

                            TextField(
                                value = prompt,
                                label = { Text(stringResource(R.string.label_prompt)) },
                                onValueChange = { prompt = it },
                                modifier = Modifier
                                    //.weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) // Subtle background
                                    .shadow(4.dp, MaterialTheme.shapes.medium)
                            )

                            Spacer(modifier = Modifier.weight(0.1f)) // Add another small spacer
                        }
                        Button(
                            onClick = {
                                val prompt =
                                    texts[selectedTabIndex].value.text + "\n" + speechText + "\nInfo: " + textFieldValue.text +
                                            "\n Please explain steps to fix it."
                                try {
                                    onEvent(MLEvent.GenAiResponseTxt(prompt))
                                } catch (e: Exception) {
                                    onErrorDismiss()
                                }
                            },
                            enabled = texts[selectedTabIndex].value.text.isNotEmpty(),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Fix")
                        }
                    }
                }
                1 -> {
                    Row {
                        var prompt by remember { mutableStateOf("This is the prompt") }
                        TextField(
                            value = prompt,
                            label = { Text(stringResource(R.string.label_prompt)) },
                            onValueChange = { prompt = it },
                            modifier = Modifier
                                //.weight(1f)
                                .padding(horizontal = 8.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) // Subtle background
                                .shadow(4.dp, MaterialTheme.shapes.medium)
                        )

                        Button(
                            onClick = {
                                val prompt =
                                    texts[selectedTabIndex].value.text + "\n" + speechText + "\nInfo: " + textFieldValue.text +
                                            "\n Please explain steps to fix it."
                                try {
                                    onEvent(MLEvent.GenAiResponseTxt(prompt))
                                } catch (e: Exception) {
                                    onErrorDismiss()
                                }
                            },
                            enabled = texts[selectedTabIndex].value.text.isNotEmpty(),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Bug")
                        }
                    }
                }
                2 -> {
                    Row {
                        var prompt by remember { mutableStateOf("This is the prompt") }
                        TextField(
                            value = prompt,
                            label = { Text(stringResource(R.string.label_prompt)) },
                            onValueChange = { prompt = it },
                            modifier = Modifier
                                //.weight(1f)
                                .padding(horizontal = 8.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) // Subtle background
                                .shadow(4.dp, MaterialTheme.shapes.medium)
                        )

                        Button(
                            onClick = {
                                val prompt =
                                    texts[selectedTabIndex].value.text + "\n" + speechText + "\nInfo: " + textFieldValue.text +
                                            "\n Please explain steps to fix it."
                                try {
                                    onEvent(MLEvent.GenAiResponseTxt(prompt))
                                } catch (e: Exception) {
                                    onErrorDismiss()
                                }
                            },
                            enabled = texts[selectedTabIndex].value.text.isNotEmpty(),
                            modifier = Modifier

                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Local")
                        }
                    }
                }
                3 -> {
                    Row {
                        var prompt by remember { mutableStateOf("This is the prompt") }
                        TextField(
                            value = prompt,
                            label = { Text(stringResource(R.string.label_prompt)) },
                            onValueChange = { prompt = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) // Subtle background
                                .shadow(4.dp, MaterialTheme.shapes.medium)
                        )

                        Button(
                            onClick = {
                                val prompt =
                                    texts[selectedTabIndex].value.text + "\n" + speechText + "\nInfo: " + textFieldValue.text +
                                            "\n Please explain steps to fix it."
                                try {
                                    onEvent(MLEvent.GenAiResponseTxt(prompt))
                                } catch (e: Exception) {
                                    onErrorDismiss()
                                }
                            },
                            enabled = texts[selectedTabIndex].value.text.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Vid")
                        }
                    }
                }
            }
        }
        BasicTextField(
            value = texts[selectedTabIndex].value,
            onValueChange = { texts[selectedTabIndex].value = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        //Spacer(modifier = Modifier.height(16.dp))
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
