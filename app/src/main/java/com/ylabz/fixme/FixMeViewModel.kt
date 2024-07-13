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



class FixMeViewModel(application: Application) : AndroidViewModel(application) {//) : ViewModel() {
    private val context = getApplication<Application>().applicationContext

    private val audioFun = AudioSysImpl(context) //TODO move to Repo

    // Backing property to avoid state updates from other classes
    private val _FixMe_uiState: MutableStateFlow<FixMeUiState> = MutableStateFlow(FixMeUiState.Success(""))
    val fixMeUiState: StateFlow<FixMeUiState> = _FixMe_uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        // Access your API key as a Build Configuration variable
        apiKey = BuildConfig.apiKeyGem
    )

    fun onEvent(event: MLEvent) {
        when (event) {
            // This is for the title
            is MLEvent.GenAiResponseImg -> {
                event.value.let{ sendPrompt(it, "what is this") }
                // sendPrompt()
            }

            is MLEvent.GenAiResponseTxt -> {

            }

            is MLEvent.SetMemo -> {
                viewModelScope.launch {
                    _FixMe_uiState.value  = FixMeUiState.Success(
                        outputText ="String",
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
                    // actual work happens in the use case.
                    audioFun.startSpeechToText(event.updateText, event.finished)
                    //_eventFlow.emit(AddPhotodoEvent.getTextFromSpeach)
                    // Not sure what to do ...
                }
            }
        }
    }


    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {
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
                _FixMe_uiState.value = FixMeUiState.Success(e.localizedMessage ?: "")
                //_FixMe_uiState.value = FixMeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}