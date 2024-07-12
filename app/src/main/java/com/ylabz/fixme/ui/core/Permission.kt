package com.ylabz.fixme.ui.core

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FeatureThatRequireMicPermission(
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {

    // Mic permission state
    val micPermissionState = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )

    if (micPermissionState.status.isGranted) {
        //content()
    } else {
        Column {
            val textToShow = if (micPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The Mic is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Mic permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                onClick = { micPermissionState.launchPermissionRequest() }
            ) {
                Text("Request Mic Permission")
            }
        }
    }
}


@ExperimentalPermissionsApi
@Composable
fun Permission(
    //modifier: Modifier,
    permission: String, // = android.Manifest.permission.CAMERA,
    rationale: String = "This permission is important for this app. Please grant the permission.",
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(permission)

    if (permissionState.status.isGranted) {
        content()
    } else {
        Rationale(rationale, {permissionState.launchPermissionRequest()})
        Column {

            val textToShow = if (permissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                rationale
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            //TODO: Fix the permission flow.
            Text(
                text = "Warning: Permissions flow of reject needs to be fix.",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.Black),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFF44336))
            )
            Text(textToShow)
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = "Permission request")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Ok")
            }
        }
    )
}

@ExperimentalPermissionsApi
@Preview
@Composable
private fun PermissionPreview() {
    Permission(
        //modifier = Modifier,
        permission = Manifest.permission.CAMERA,
        rationale = "This permission is important for this app. Please grant the permission.",
        permissionNotAvailableContent = {
            Text("Permission not available")
        }
    ) {
        Text("Permission granted")
    }
}
