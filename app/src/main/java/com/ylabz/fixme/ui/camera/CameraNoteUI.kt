package com.ylabz.fixme.ui.camera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ylabz.fixme.MLEvent
import com.ylabz.fixme.R
import com.ylabz.fixme.ui.core.Permission
import java.util.concurrent.Executors

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraNoteUIScreen(
    modifier: Modifier = Modifier,
    onEvent: (MLEvent) -> Unit,
    textFieldValue: MutableState<TextFieldValue>,
    isExpanded: () -> Unit
) {
    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val executor = remember { Executors.newSingleThreadExecutor() }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    var text by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider = cameraProviderFuture.get()
    }

    Box(modifier = modifier) {
        Permission(
            permission = Manifest.permission.CAMERA,
            rationale = "You said you wanted to take a photo, so I'm going to have to ask for permission.",
            permissionNotAvailableContent = {
                Column(modifier.padding(16.dp)) {
                    Text("O noes! No Camera!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    }) { Text("Open Settings") }
                }
            }
        ) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            } else {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    onClick = {
                        textFieldValue.value = TextFieldValue("Loading ... ")
                        isLoading = true

                        cameraProvider?.let { provider ->
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                                val image = imageProxy.image
                                if (image != null) {
                                    val img = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                                    textRecognizer.process(img)
                                        .addOnCompleteListener { task ->
                                            isLoading = false
                                            text = if (!task.isSuccessful) {
                                                task.exception?.localizedMessage.toString()
                                            } else {
                                                task.result.text
                                            }

                                            onEvent(MLEvent.GetTextFromImg(text))
                                            textFieldValue.value = TextFieldValue(text)
                                            Log.d("CameraXSample", "Text: $text")

                                            imageProxy.close()
                                            provider.unbind(imageAnalysis)
                                        }
                                }
                            }

                            try {
                                provider.unbindAll()
                                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis)
                            } catch (exc: Exception) {
                                Log.e("CameraXSample", "Use case binding failed", exc)
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_a_photo),// .photodo_icon),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }
    }

    if (text.isNotEmpty()) {
        AlertDialog(onDismissRequest = {
            text = ""
            isExpanded()
        }) {
            Card(modifier = Modifier.fillMaxWidth(0.8f)) {
                Column {
                    Text(
                        text = text,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = {
                            text = ""
                            isExpanded()
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text(text = "Done")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CSP() {
    val textFieldValue = remember { mutableStateOf(TextFieldValue()) }
    CameraNoteUIScreen(onEvent = { }, textFieldValue = textFieldValue, isExpanded = {})
}
