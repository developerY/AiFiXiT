package com.ylabz.fixme

import android.graphics.Bitmap

// All direct subclasses of a sealed class are known at compile time.
sealed class MLEvent {
    data class GenAiResponseTxt(val value: String) : MLEvent()
    data class GenAiResponseImg(val value: Bitmap, val prompt: String) : MLEvent()
}
