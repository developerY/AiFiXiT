package com.ylabz.fixme

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.ylabz.fixme.sys.AudioSysImpl
import com.ylabz.fixme.sys.AudioSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch




class FixMeViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val audioFun = AudioSysImpl(context) // TODO move to Repo

    private val _FixMe_uiState: MutableStateFlow<FixMeUiState> =
        MutableStateFlow(FixMeUiState.Success(""))
    val fixMeUiState: StateFlow<FixMeUiState> = _FixMe_uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = BuildConfig.apiKeyGem
    )

    private val chat = generativeModel.startChat(
        /*history = listOf(
            content(role = "user") { text("Hello, Need this fixed.") },
            content(role = "model") { text("Great to meet you. What would you like fixed?") }
        )*/
    )

    fun onEvent(event: MLEvent) {
        when (event) {

            is MLEvent.GenAiResponseImg -> {
                sendChatWithImage(event.prompt, event.value)
                Log.d("FixMe", "Called")
                //sendPromptNOTUSED(event.value, event.prompt)
            }

            is MLEvent.GenAiResponseTxt -> {
                sendChat(event.value)
            }

            is MLEvent.SetMemo -> {
                viewModelScope.launch {
                    _FixMe_uiState.value = FixMeUiState.Success(
                        outputText = "String",
                        memo = "Needs Fixing",
                    )
                }
            }

            is MLEvent.GetTextFromImg -> {
                // Update the notes field.
                event.imgText
            }

            is MLEvent.StartCaptureSpeech2Txt -> {
                viewModelScope.launch {
                    audioFun.startSpeechToText(event.updateText, event.finished)
                }
            }
        }
    }

    private fun sendChatWithImage(prompt: String, image: Bitmap? = null) {
        //_FixMe_uiState.value = FixMeUiState.Loading
        Log.d("FixMe", "sendChatWithImage: $prompt")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(
                    content(role = "user") {
                        text(prompt)
                        image?.let { image(it) }
                    }
                )
                response.text?.let { outputContent ->
                    _FixMe_uiState.value = FixMeUiState.Success(outputContent)
                }
                Log.d("FixMe", "sendChatWithImage: ${response.text}")
            } catch (e: Exception) {
                _FixMe_uiState.value = FixMeUiState.Success(e.localizedMessage ?: "Error")
                Log.d("FixMe", "sendChatWithImage: ${e.localizedMessage}")
            }
        }
    }

    private fun sendChat(prompt: String) {
        _FixMe_uiState.value = FixMeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(content(role = "user") { text(prompt) })
                response.text?.let { outputContent ->
                    _FixMe_uiState.value = FixMeUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _FixMe_uiState.value = FixMeUiState.Error(e.localizedMessage ?: "Error")
            }
        }
    }

    private fun sendPromptNOTUSED(bitmap: Bitmap, prompt: String) {
        _FixMe_uiState.value = FixMeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _FixMe_uiState.value = FixMeUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _FixMe_uiState.value = FixMeUiState.Error(e.localizedMessage ?: "Error")
            }
        }
    }
}
