package com.ylabz.fixme.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ylabz.fixme.MLEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun LocationCaptureUI(
    hasPermission: Boolean,
    modifier: Modifier = Modifier,
    updateLocation: (Location?) -> Unit,
    onEvent: (MLEvent) -> Unit
) {
    if (hasPermission) {
        LocationCaptureUIContent(modifier = modifier, updateLocation = updateLocation, onEvent = onEvent)
    } else {
        val context = LocalContext.current
        val permissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }

        if (permissionState.allPermissionsGranted) {
            LocationCaptureUIContent(modifier = modifier, updateLocation = updateLocation, onEvent = onEvent)
        } else {
            Column(modifier) {
                Text("Location permission is needed to get the current location.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }
                ) {
                    Text("Open Settings")
                }
            }
        }
    }
}

@Composable
fun LocationCaptureUIContent(
    modifier: Modifier = Modifier,
    updateLocation: (Location?) -> Unit,
    onEvent: (MLEvent) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            onEvent(MLEvent.GetLocation)
        },
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AddLocation,
            contentDescription = "location",//stringResource("location"),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LocationCapturePreview() {
    LocationCaptureUIContent(
        updateLocation = {},
        onEvent = {}
    )
}
