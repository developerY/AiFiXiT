package com.ylabz.fixme.ui.camera

import android.Manifest
import android.graphics.Rect
import android.media.FaceDetector
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ylabz.fixme.ui.core.Permission
import java.util.concurrent.Executors


@OptIn(ExperimentalGetImage::class)
@kotlin.OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MLFaceDetectScreen(
    paddingVals: PaddingValues,
    modifier: Modifier = Modifier
) {
    val currContext = LocalContext.current
    //cameraController comes from viewmodel from repo from system implementation

    // all this happening in the System Repo
    /*
        val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }

    // Setup  LifecycleCameraController from ViewModel
    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // is being setup here
    previewView.controller = cameraController
     */
    val cameraController = remember { LifecycleCameraController(currContext) }
    val lifecycleOwner = LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val executor = remember { Executors.newSingleThreadExecutor() }


    var faceList by remember {
        mutableStateOf(emptyList<Any?>())
    }
    var currBondingBox : android.graphics.Rect?  = null

    val faceBoundingBox = convertRect(currBondingBox ?: Rect())


    cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
        imageProxy.image?.let { image ->
        }
    }


    Box(modifier = modifier
        /*.fillMaxSize()
        .drawWithContent {
            drawRect(
                color = Color.Red, // Change color as desired
                topLeft = faceBoundingBox.topLeft,
                size = faceBoundingBox.size
            )
        }*/) {
        Permission(
            permission = Manifest.permission.CAMERA,
            rationale = "You said you wanted to take a photo, so I'm going to have to ask for permission.",
            permissionNotAvailableContent = {
                Column(modifier.padding(/*paddingValues*/)) {
                    Text("O noes! No Camera!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {}
                    ) { Text("Open Settings") }
                }
            }
        )

        // cameraController is LifecycleCameraController
        // controller =  LifecycleCameraController from ViewModel
        CameraPreview(controller = cameraController, Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {

        }
    }
}
private fun convertRect(boundingBox: android.graphics.Rect): androidx.compose.ui.geometry.Rect {
    return androidx.compose.ui.geometry.Rect(
        left = boundingBox.left.toFloat(),
        top = boundingBox.top.toFloat(),
        right = boundingBox.right.toFloat(),
        bottom = boundingBox.bottom.toFloat()
    )
}



/*
 // Real-time contour detection
    val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    val detector = FaceDetection.getClient(realTimeOpts)
    // Or, to use the default option:
    // val detector = FaceDetection.getClient();

    val result = detector.process(image)
        .addOnSuccessListener { faces ->
            // Task completed successfully
            // ...
        }
        .addOnFailureListener { e ->
            // Task failed with an exception
            // ...
        }
 */