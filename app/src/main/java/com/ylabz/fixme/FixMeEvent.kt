package com.ylabz.fixme

import android.graphics.Bitmap

// All direct subclasses of a sealed class are known at compile time.
sealed class MLEvent {
    data class StartCaptureSpeech2Txt(val updateText: (String) -> Unit, val finished: () -> Unit) : MLEvent()
    data class SetMemo(val value: String) : MLEvent()
    data class GetTextFromImg(val imgText: String) : MLEvent()
    //data class GenAiResponseTxt(val prompt: String, val index: Int) : MLEvent()
    data class GenAiResponseImg(val prompt: String, val image: Bitmap, val index: Int) : MLEvent()
}
