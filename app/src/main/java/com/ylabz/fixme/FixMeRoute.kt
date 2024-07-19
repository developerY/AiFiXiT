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
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.twotone.UnfoldLess
import androidx.compose.material.icons.twotone.UnfoldMore
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.ylabz.fixme.ui.FixImage
import com.ylabz.fixme.ui.FourTextAreasTabs
import com.ylabz.fixme.ui.camera.CameraNoteUIScreen
import com.ylabz.fixme.ui.core.Loading
import com.ylabz.fixme.ui.location.LocationCaptureUI
import com.ylabz.fixme.ui.mic.SpeechCaptureUI
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.lang.Error


@Composable
fun MLRoute(
    fixMeViewModel: FixMeViewModel = viewModel()
) {
    val onEvent = fixMeViewModel::onEvent
    val mlState by fixMeViewModel.fixMeUiState.collectAsStateWithLifecycle()

    MLScreen(
        onEvent = onEvent,
        fixMeUiState = mlState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun MLScreen(
    modifier: Modifier = Modifier,
    onEvent: (MLEvent) -> Unit,
    fixMeUiState: FixMeUiState,
) {
    when (fixMeUiState) {

        is FixMeUiState.Loading -> MLContent(
            modifier,
            onEvent = onEvent,
            result = emptyList(),
            location = null,
            error = null,
            loading = true
        )

        is FixMeUiState.Success -> MLContent(
            modifier,
            onEvent = onEvent,
            result = fixMeUiState.geminiResponses,
            location = fixMeUiState.currLocation,
            error = null
        )

        is FixMeUiState.Error -> {
            MLContent(
                modifier,
                onEvent = onEvent,
                result = emptyList(),
                location = null,
                error = fixMeUiState.errorMessage
            )
        }
    }
}

@Composable
fun InitImagePath(context: Context): String {
    return drawableToFilePath(context, R.drawable.baked_goods_1, "backed_goods")
    //return drawableToFilePath(context, R.drawable.broken_screen, "cracked screen")
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

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun MLContent(
    modifier: Modifier = Modifier,
    onEvent: (MLEvent) -> Unit,
    result: List<String>,
    location: Location?,
    error: String?,
    loading: Boolean = false
) {
    val context = LocalContext.current
    val initialImagePath = InitImagePath(context)

    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }

    var image = remember { mutableStateOf(initialImagePath) }
    var isCameraVisible by remember { mutableStateOf(false) }
    var isCameraNoteVisible by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var speechText by rememberSaveable { mutableStateOf("") }
    var seconds by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }

    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val hasPermission by remember { derivedStateOf { permissionState.status.isGranted } }

    var showError by remember { mutableStateOf(error != null) }
    var isTopHalfVisible by remember { mutableStateOf(true) }

    var textNoteFieldValue = remember { mutableStateOf(TextFieldValue()) }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (isTopHalfVisible) Icons.TwoTone.UnfoldLess else Icons.TwoTone.UnfoldMore
            val textColor = if (isTopHalfVisible) Color(0xFF512DA8) else Color(0xFF0B7E71)
            val iconTint = if (isTopHalfVisible) Color(0xFF512DA8) else Color(0xFF0B7E71)

            Spacer(Modifier.weight(0.5f))

            Text(
                text = stringResource(R.string.baking_title),
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .weight(2f)
            )

            Icon(
                imageVector = icon,
                contentDescription = "Expand/Collapse",
                tint = iconTint,
                modifier = Modifier
                    .weight(0.5f)
                    .size(27.dp)
                    .clickable { isTopHalfVisible = !isTopHalfVisible }
            )
        }

        if (isTopHalfVisible) {
            if (isCameraVisible || isCameraNoteVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                ) {
                    when {
                        isCameraVisible -> FixImage(
                            modifier = Modifier.fillMaxSize(),
                            onImageFile = { file ->
                                image.value = file.path
                                isCameraVisible = false
                            }
                        )

                        isCameraNoteVisible -> CameraNoteUIScreen(
                            modifier = Modifier.fillMaxSize(),
                            onEvent = onEvent,
                            textFieldValue = textNoteFieldValue,
                            isExpanded = { isCameraNoteVisible = false }
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { isCameraVisible = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = stringResource(R.string.action_go),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box {
                        SpeechCaptureUI(
                            hasPermission = hasPermission,
                            updateText = { desTxt -> speechText = desTxt },
                            onEvent = onEvent,
                            isRec = { isRecording = true }
                        )

                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Red,
                            strokeWidth = 4.dp,
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = {
                            isCameraNoteVisible = true
                            isCameraVisible = false
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PostAdd,
                            contentDescription = stringResource(R.string.action_go),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box {
                        LocationCaptureUI(
                            hasPermission = hasPermission,
                            location = location,
                            onEvent = onEvent,
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    val imagePath = image.value
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    LazyRow(
                        modifier = Modifier
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
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Captured Image",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(8.dp)
                                )
                                if (image.value.isNotEmpty()) {
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
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Speech Text",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(8.dp)
                                )
                                TextField(
                                    value = speechText,
                                    onValueChange = { speechText = it },
                                    label = { Text(text = "Speech Text") },
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            BorderStroke(
                                                2.dp,
                                                MaterialTheme.colorScheme.primary
                                            )
                                        )
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
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Notes Text",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(8.dp)
                                )
                                TextField(
                                    value = textNoteFieldValue.value,
                                    onValueChange = { textNoteFieldValue.value = it },
                                    label = { Text(text = "Notes Text") },
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            BorderStroke(
                                                2.dp,
                                                MaterialTheme.colorScheme.primary
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showError) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showError = false }) {
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
                        text = error ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                }

        }

        FourTextAreasTabs(
            geminiText = result,
            speechText = speechText,
            location = location,
            image = image.value,
            textFieldValue = textNoteFieldValue.value.text,
            onEvent = onEvent,
            errorMessage = error?: "",
            showError = false,
            onErrorDismiss = {},
            isLoading = loading
        )
    }
}

/*@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun MLContentPreview() {
    MaterialTheme {
        MLContent(
            onEvent = {},
            result = emptyList(),
            location = null
        )
    }
}*/

