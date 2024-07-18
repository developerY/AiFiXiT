package com.ylabz.fixme

import android.app.Application
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.gms.location.LocationServices
import com.ylabz.fixme.sys.AudioSysImpl
import com.ylabz.fixme.sys.DefaultLocationTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch




class FixMeViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val audioFun = AudioSysImpl(context) // TODO move to Repo

    private val _fixMeUiState = MutableStateFlow<FixMeUiState>(FixMeUiState.Success())
    val fixMeUiState: StateFlow<FixMeUiState> = _fixMeUiState.asStateFlow()

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private val locationTracker = DefaultLocationTracker(fusedLocationProviderClient, application)


    var currentLocation by mutableStateOf<Location?>(null)

    /*
    private val _FixMe_uiState: MutableStateFlow<FixMeUiState> =
        MutableStateFlow(FixMeUiState.Success())
    val fixMeUiState: StateFlow<FixMeUiState> = _FixMe_uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = BuildConfig.apiKeyGem
    )*/

    private val generativeModelChat = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = BuildConfig.apiKeyGem
    )

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.apiKeyGem
    )

    private val chat = generativeModelChat.startChat(
        /*history = listOf(
            content(role = "user") { text("Hello, Need this fixed.") },
            content(role = "model") { text("Great to meet you. What would you like fixed?") }
        )*/
    )

    fun onEvent(event: MLEvent) {
        when (event) {
            is MLEvent.GenAiPromptResponseImg -> {
                sendPromptWithImage(event.prompt, event.index, event.image)
                Log.d("FixMe", "Called with image")
            }

            is MLEvent.GenAiChatResponseImg -> {
                sendChatWithImage(event.prompt, event.index, event.image)
                Log.d("FixMe", "Called Chat with image")
            }

            is MLEvent.GenAiChatResponseTxt -> {
                continueChatWithoutImage(event.prompt, event.index)
                Log.d("FixMe", "Called Chat without image")
            }

            is MLEvent.GetLocation -> {
                //---- get Current location
                // Now we have to create a variable that will hold the current location state and it will be updated with the getCurrentLocation function.

                    viewModelScope.launch(Dispatchers.IO) {
                        currentLocation = locationTracker.getCurrentLocation() // Location
                        val currentState = (_fixMeUiState.value as? FixMeUiState.Success) ?: FixMeUiState.Success()
                        _fixMeUiState.value = currentState.copy(currLocation = currentLocation)
                        Log.d("FixMe", "current location $currentLocation}")
                    }

            }

            /*is MLEvent.SetMemo -> {
                val old = FixMeUiState.Success().geminiResponses
                viewModelScope.launch {
                    _FixMe_uiState.value = FixMeUiState.Success(
                        geminiResponses = old,
                    )
                }
            }*/

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


    private fun sendPromptWithImage(prompt: String, index: Int, image: Bitmap? = null) {
        Log.d("FixMe", "sendWithImage: $prompt")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content(role = "user") {
                        text(prompt)
                        image?.let { image(it) }
                    }
                )
                response.text?.let { outputContent : String ->
                    val currentState = (_fixMeUiState.value as? FixMeUiState.Success) ?: FixMeUiState.Success()
                    val updatedResponses = currentState.geminiResponses.toMutableList().apply {
                        this[index] = outputContent
                    }
                    _fixMeUiState.value = currentState.copy(geminiResponses = updatedResponses)
                }
                Log.d("FixMe", "sendChatWithImage: ${response.text}")
            } catch (e: Exception) {
                _fixMeUiState.value = FixMeUiState.Error(e.localizedMessage ?: "Error")
                Log.d("FixMe", "sendChatWithImage: ${e.localizedMessage}")
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
                response.text?.let { outputContent : String ->
                    val currentState = (_fixMeUiState.value as? FixMeUiState.Success) ?: FixMeUiState.Success()
                    val updatedResponses = currentState.geminiResponses.toMutableList().apply {
                        this[index] = outputContent
                    }
                    _fixMeUiState.value = currentState.copy(geminiResponses = updatedResponses)
                }
                Log.d("FixMe", "sendChatWithImage: ${response.text}")
            } catch (e: Exception) {
                _fixMeUiState.value = FixMeUiState.Error(e.localizedMessage ?: "Error")
                Log.d("FixMe", "sendChatWithImage: ${e.localizedMessage}")
            }
        }
    }

    private fun continueChatWithoutImage(prompt: String, index: Int) {
        Log.d("FixMe", "sendChatWithImage: $prompt")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(
                    content(role = "user") {
                        text(prompt)
                    }
                )
                response.text?.let { outputContent : String ->
                    val currentState = (_fixMeUiState.value as? FixMeUiState.Success) ?: FixMeUiState.Success()
                    val updatedResponses = currentState.geminiResponses.toMutableList().apply {
                        this[index] = outputContent
                    }
                    _fixMeUiState.value = currentState.copy(geminiResponses = updatedResponses)
                }
                Log.d("FixMe", "sendChatWithoutImage: ${response.text}")
            } catch (e: Exception) {
                _fixMeUiState.value = FixMeUiState.Error(e.localizedMessage ?: "Error")
                Log.d("FixMe", "sendChatWithoutImage: ${e.localizedMessage}")
            }
        }
    }


}
