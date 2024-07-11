package com.ylabz.fixme

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ylabz.fixme.ui.FixImage
import com.ylabz.fixme.ui.camera.CameraPreview
import com.ylabz.fixme.ui.camera.FixCamCap
import com.ylabz.fixme.ui.camera.MLFaceDetectScreen
import com.ylabz.fixme.ui.core.Loading
import com.ylabz.fixme.ui.core.Permission
import java.io.File
import java.io.FileOutputStream


@Composable
fun MLRoute(
    //paddingValues: PaddingValues, // not using tabs yet.
    fixMeViewModel: FixMeViewModel = viewModel()
) {
    val onEvent = fixMeViewModel::onEvent
    val mlState by fixMeViewModel.uiState.collectAsStateWithLifecycle()
    //val work: () -> LifecycleCameraController = fixMeViewModel::buildAna

    MLScreen(
        //paddingValues = paddingValues,
        //lifeCycCamCont = work,
        onEvent = onEvent,
        uiState = mlState,
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
    uiState: UiState,
    //navTo: (String) -> Unit,
) {

    when (uiState) {
        /*
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
         } else {
         */
        UiState.Loading -> Loading(modifier)


        /*
        else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }
         */
        is UiState.Success -> MLContent(
            modifier,
            //paddingValues = paddingValues,
            //lifeCycCamCont = lifeCycCamCont,
            onEvent = onEvent,
            result = uiState.outputText
            //imgClassID = mlUiState.data,
            //screenAI = mlUiState.currentScreen,
            //genAIRes = mlUiState.aiResponse,
            //navTo = navTo
        )

        //uiState.Error -> TODO()
        /*
        if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
         */
        is UiState.Error -> {
            val textColor = MaterialTheme.colorScheme.error
            val result = (uiState as UiState.Error).errorMessage
        }
        // UiState.Initial -> TODO()
        // is UiState.Success -> TODO()
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
    var isRecording by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showError) {
        Snackbar(
            action = {
                TextButton(onClick = { showError = false }) {
                    Text(text = "dismiss")//stringResource("dismiss"))
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
                    }
                    /*onError = { message ->
                        errorMessage = message
                        showError = true
                    }*/
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
                        imageVector = Icons.Default.Build, // Use the Material3 camera icon
                        contentDescription = stringResource(R.string.action_go),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { isRecording = !isRecording },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(64.dp) // Adjust the size as needed
                ) {
                    Icon(
                        imageVector = Icons.Default.Face, // Use the Material3 mic icon
                        contentDescription = stringResource(R.string.action_go),
                        tint = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (images.isNotEmpty()) {
                val imagePath = images.last()
                val bitmap = BitmapFactory.decodeFile(imagePath)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary))
                )
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
                    try {
                        val bitmap = BitmapFactory.decodeFile(images[selectedImage.intValue])
                        onEvent(MLEvent.GenAiResponseImg(bitmap, prompt))
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Unknown error"
                        showError = true
                    }
                },
                enabled = prompt.isNotEmpty(),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .shadow(4.dp, MaterialTheme.shapes.medium)
            ) {
                Text(text = stringResource(R.string.action_go))
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
            result = "found" //stringResource(R.string.results_placeholder)
        )
    }
}
