package com.ylabz.fixme

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FixMeViewModel : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Success(""))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
        }
    }


    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}