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
                sendChatWithImage(event.prompt, event.index, event.image)
                Log.d("FixMe", "Called with image")
            }

            is MLEvent.SetMemo -> {
                val old = FixMeUiState.Success().geminiResponses
                viewModelScope.launch {
                    _FixMe_uiState.value = FixMeUiState.Success(
                        geminiResponses = old,
                        memo = "Needs Fixing"
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


    private fun sendChatWithImage(prompt: String, index: Int, image: Bitmap? = null) {
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
                    val currentState = (_FixMe_uiState.value as? FixMeUiState.Success) ?: FixMeUiState.Success()
                    currentState.geminiResponses[index] = outputContent
                    _FixMe_uiState.value = currentState
                }
                Log.d("FixMe", "sendChatWithImage: ${response.text}")
            } catch (e: Exception) {
                _FixMe_uiState.value = FixMeUiState.Error(e.localizedMessage ?: "Error")
                Log.d("FixMe", "sendChatWithImage: ${e.localizedMessage}")
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
