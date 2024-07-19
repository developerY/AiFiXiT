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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.fixme.MLEvent
import com.ylabz.fixme.ui.core.FeatureThatRequireLocPermission
import com.ylabz.fixme.ui.core.FeatureThatRequireMicPermission
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun LocationCaptureUI(
    hasPermission: Boolean,
    modifier: Modifier = Modifier,
    location: Location? = null,
    onEvent: (MLEvent) -> Unit
) {
    if (hasPermission) {
        LocationCaptureUIContent(modifier = modifier, location, onEvent = onEvent)
    } else {
        val context = LocalContext.current
        FeatureThatRequireLocPermission(
            permissionNotAvailableContent = {
                Column(modifier) {
                    Text("O noes! Location!")
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
        ) {
            LocationCaptureUIContent(modifier = modifier, location = location, onEvent = onEvent)

        }
    }
}

@Composable
fun LocationCaptureUIContent(
    modifier: Modifier = Modifier,
    location: Location? = null,
    onEvent: (MLEvent) -> Unit,
) {
    val iconTint = if (location != null) Color(0xFF22BB22) else MaterialTheme.colorScheme.primary

    IconButton(
        onClick = {
            onEvent(MLEvent.GetLocation)
        },
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AddLocation,
            contentDescription = "location",
            tint = iconTint
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LocationCapturePreview() {
    LocationCaptureUIContent(
        location = null,
        onEvent = {},
    )
}
