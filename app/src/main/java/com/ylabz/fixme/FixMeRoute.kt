package com.ylabz.fixme

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ylabz.fixme.ui.FixImage
import com.ylabz.fixme.ui.camera.CameraNoteUIScreen
import com.ylabz.fixme.ui.core.Loading
import com.ylabz.fixme.ui.mic.SpeechCaptureUI
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream


@Composable
fun MLRoute(
    //paddingValues: PaddingValues, // not using tabs yet.
    fixMeViewModel: FixMeViewModel = viewModel()
) {
    val onEvent = fixMeViewModel::onEvent
    val mlState by fixMeViewModel.fixMeUiState.collectAsStateWithLifecycle()
    //val work: () -> LifecycleCameraController = fixMeViewModel::buildAna

    MLScreen(
        //paddingValues = paddingValues,
        //lifeCycCamCont = work,
        onEvent = onEvent,
        fixMeUiState = mlState,
        //navTo = navTo,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun MLScreen(
    modifier: Modifier = Modifier,
    //paddingValues: PaddingValues,
    //lifeCycCamCont: () -> LifecycleCameraController,
    onEvent: (MLEvent) -> Unit,
    fixMeUiState: FixMeUiState,
    //navTo: (String) -> Unit,
) {

    when (fixMeUiState) {
        /*
        if (fixMeUiState is FixMeUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
         } else {
         */
        FixMeUiState.Loading -> Loading(modifier)


        /*
        else if (fixMeUiState is FixMeUiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (fixMeUiState as FixMeUiState.Success).outputText
            }
         */
        is FixMeUiState.Success -> MLContent(
            modifier,
            //paddingValues = paddingValues,
            //lifeCycCamCont = lifeCycCamCont,
            onEvent = onEvent,
            result = fixMeUiState.outputText
            //imgClassID = mlUiState.data,
            //screenAI = mlUiState.currentScreen,
            //genAIRes = mlUiState.aiResponse,
            //navTo = navTo
        )

        //fixMeUiState.Error -> TODO()
        /*
        if (fixMeUiState is FixMeUiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (fixMeUiState as FixMeUiState.Error).errorMessage
         */
        is FixMeUiState.Error -> {
            val textColor = MaterialTheme.colorScheme.error
            val result = (fixMeUiState as FixMeUiState.Error).errorMessage
        }
        // FixMeUiState.Initial -> TODO()
        // is FixMeUiState.Success -> TODO()
        // TODO()
    }
}

@Composable
fun InitImagePaths(context: Context): Array<String> {
    return arrayOf(
        drawableToFilePath(context, R.drawable.baked_goods_1, "baked_goods_1.png"),
        drawableToFilePath(context, R.drawable.baked_goods_2, "baked_goods_2.png")
        // Add more images as needed
    )
}

fun drawableToFilePath(context: Context, drawableId: Int, fileName: String): String {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    val bitmap = (drawable as BitmapDrawable).bitmap

    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    return file.path
}



fun drawableToFilePathList(context: Context, drawableId: Int, fileName: String): String {
    // Get the drawable as a bitmap
    val drawable = ContextCompat.getDrawable(context, drawableId)
    val bitmap = (drawable as BitmapDrawable).bitmap

    // Save the bitmap to a file
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    // Return the file path
    return file.path
}


private val initialImages = arrayOf(
    R.drawable.baked_goods_1,
    R.drawable.baked_goods_2,
    // Add more initial images as needed
)

private val initialImageDescriptions = arrayOf(
    "Image 1 description",
    "Image 2 description",
    // Add more initial descriptions as needed
)



@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MLContent(
    modifier: Modifier = Modifier,
    onEvent: (MLEvent) -> Unit,
    result: String,
) {
    val context = LocalContext.current
    val initialImagePaths = InitImagePaths(context)

    val selectedImage = remember { mutableIntStateOf(0) }
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(result) }
    var images = remember { mutableStateListOf(*initialImagePaths) }
    var isCameraVisible by remember { mutableStateOf(false) }
    var isCameraNoteVisible by remember { mutableStateOf(true) }
    var isRecording by remember { mutableStateOf(false) }
    var speechText by rememberSaveable { mutableStateOf("") }
    var seconds by remember { mutableStateOf(0) }

    var textFieldValue = remember { mutableStateOf(TextFieldValue()) }

    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    // Observe changes in the permission state and trigger recomposition
    val hasPermission by remember { derivedStateOf { permissionState.status.isGranted } }


    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Add a progress bar to indicate recording progress
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            progress += 0.1f
            if (progress >= 1f) {
                progress = 0f
            }
            seconds++
            delay(100L)

            if (seconds >= 50) {
                isRecording = false
                seconds = 0
            }
        }
    }

    if (showError) {
        Snackbar(
            action = {
                TextButton(onClick = { showError = false }) {
                    Text(text = "dismiss")// stringResource(R.string.dismiss))
                }
            },
            modifier = Modifier.padding(16.dp)
        ) { Text(text = errorMessage) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.baking_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (isCameraVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Adjust the height as needed
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
            ) {
                FixImage(
                    modifier = Modifier.fillMaxSize(),
                    onImageFile = { file ->
                        // Replace the current list of images with the new image file path
                        images.add(file.path)
                        isCameraVisible = false // Hide the camera after capturing the image
                    },
                    /*onError = { message ->
                        errorMessage = message
                        showError = true
                    }*/
                )
            }
            if(isCameraNoteVisible){
                CameraNoteUIScreen(
                    onEvent = onEvent,
                    textFieldValue = textFieldValue,
                    isExpanded = { } //isCameraNoteVisible = false }
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = { isCameraVisible = true },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(64.dp) // Adjust the size as needed
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera, // Use the Material3 camera icon
                        contentDescription = stringResource(R.string.action_go),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box() {

                    SpeechCaptureUI(
                        hasPermission = hasPermission,
                        updateText = { desTxt -> speechText = desTxt },
                        onEvent = onEvent,
                        isRec = {isRecording = true}
                    )

                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.align(Alignment.Center),
                        //.fillMaxSize() // Set your desired size here
                        //.padding(16.dp), // Optional padding
                        color = Color.Red,
                        strokeWidth = 4.dp,
                    )
                }


                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { isCameraVisible = true },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(64.dp) // Adjust the size as needed
                ) {
                    Icon(
                        imageVector = Icons.Default.PostAdd, // Use the Material3 camera icon
                        contentDescription = stringResource(R.string.action_go),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { /* call GPS */ },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(64.dp) // Adjust the size as needed
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocation, // Use the Material3 camera icon
                        contentDescription = stringResource(R.string.action_go),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column {
                val imagePath = images.last()
                val bitmap = BitmapFactory.decodeFile(imagePath)
                LazyRow(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                        ) {
                            Text(
                                text = "Captured Image",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                            if (images.isNotEmpty()) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Captured Image",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            BorderStroke(
                                                4.dp,
                                                MaterialTheme.colorScheme.primary
                                            )
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                        ) {
                            Text(
                                text = "Speech Text",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                            TextField(
                                value = speechText,
                                onValueChange = { speechText = it },
                                label = { Text(text = speechText) }, //stringResource(R.string.speech_text_label)) },
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                        ) {
                            Text(
                                text = "Notes Text",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                            TextField(
                                value = textFieldValue.value,
                                onValueChange = { textFieldValue.value = it },
                                label = { textFieldValue.value }, //stringResource(R.string.speech_text_label)) },
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = prompt,
                label = { Text(stringResource(R.string.label_prompt)) },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .shadow(4.dp, MaterialTheme.shapes.medium)
            )

            Button(
                onClick = {
                    val imagePath = images.last()
                    if (imagePath.isNotEmpty()) {
                        try {
                            val bitmap = BitmapFactory.decodeFile(imagePath)
                            onEvent(MLEvent.GenAiResponseImg(bitmap, prompt))
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Unknown error"
                            showError = true
                        }
                    }
                },
                enabled = prompt.isNotEmpty(),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .shadow(4.dp, MaterialTheme.shapes.medium)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Display the image preview
                    if (images.isNotEmpty()) {
                        val imagePath = images.last()
                        val bitmap = BitmapFactory.decodeFile(imagePath)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Image Preview",
                                modifier = Modifier
                                    .size(24.dp) // Adjust size as needed
                                    .padding(end = 8.dp) // Adjust padding as needed
                            )
                        }
                    }
                    Text(text = "AI") // stringResource(R.string.action_go))
                }
            }

        }

        var textColor = MaterialTheme.colorScheme.onSurface

        val scrollState = rememberScrollState()
        Text(
            text = result,
            textAlign = TextAlign.Start,
            color = textColor,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .shadow(4.dp, MaterialTheme.shapes.medium)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun MLContentPreview() {
    MaterialTheme {
        MLContent(
            onEvent = {},
            result = stringResource(R.string.results_placeholder)
        )
    }
}

