package com.ylabz.fixme

import android.graphics.Bitmap

// All direct subclasses of a sealed class are known at compile time.
sealed class MLEvent {
    data class StartCaptureSpeech2Txt(val updateText: (String) -> Unit, val finished: () -> Unit) : MLEvent()
    data class GetTextFromImg(val imgText: String) : MLEvent()
    data class GenAiPromptResponseImg(val prompt: String, val image: Bitmap, val index: Int) : MLEvent()
    data class GenAiChatResponseImg(val prompt: String, val image: Bitmap, val index: Int) : MLEvent()
    data class GenAiChatResponseTxt(val prompt: String, val index: Int) : MLEvent()
    object resetChat : MLEvent()

    object GetLocation : MLEvent()
}

