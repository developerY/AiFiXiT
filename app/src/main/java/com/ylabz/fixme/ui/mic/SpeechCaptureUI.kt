package com.ylabz.fixme.ui.mic

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ylabz.fixme.MLEvent
import com.ylabz.fixme.R
import com.ylabz.fixme.ui.core.FeatureThatRequireMicPermission
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalCoroutinesApi
@Composable
fun SpeechCaptureUI(
    hasPermission: Boolean,
    modifier: Modifier = Modifier,
    updateText: (String) -> Unit,
    onEvent: (MLEvent) -> Unit,
    isRec: () -> Unit
) {
    if (hasPermission) {
        SpeechCaptureUIContent(modifier = modifier, updateText = updateText, onEvent = onEvent, isRec)
    } else {
        val context = LocalContext.current
        FeatureThatRequireMicPermission(
            //permission = Manifest.permission.RECORD_AUDIO,
            //rationale = "You said you wanted speech reorganization, so I'm going to have to ask for permission.",
            permissionNotAvailableContent = {
                Column(modifier) {
                    Text("O noes! Mic!")
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
            SpeechCaptureUI(
                hasPermission = hasPermission,
                updateText = { desTxt -> onEvent(MLEvent.SetMemo(desTxt)) },
                onEvent = onEvent,
                isRec = isRec
            )
        }
    }
}

@Composable
fun SpeechCaptureUIContent(
    modifier: Modifier = Modifier,
    updateText: (String) -> Unit,
    onEvent: (MLEvent) -> Unit,
    isRec: () -> Unit
) {
    IconButton(

        onClick = {
            onEvent(MLEvent.StartCaptureSpeech2Txt(updateText) {})
            isRec()
        },
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(64.dp) // Adjust the size as needed
    ) {
        Icon(
            imageVector = Icons.Default.Mic, // Use the Material3 camera icon
            contentDescription = stringResource(R.string.action_go),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
@Preview
@Composable
fun SpeechCapturePreview() {
    // val localSpeechState = compositionLocalOf { SpeechState() }
    SpeechCaptureUIContent(
        modifier = Modifier,
        updateText = {},
        onEvent = {},
        isRec = {}
    )
}