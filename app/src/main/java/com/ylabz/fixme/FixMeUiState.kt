package com.ylabz.fixme

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface FixMeUiState {

    /**
     * Still loading
     */
    object Loading : FixMeUiState


    /**
     * Text has been generated
     */
    data class Success(
        var geminiResponses: List<String> = listOf("", "", "", ""),
        var currLocation : Location? = null
    ) : FixMeUiState {
        var responses by mutableStateOf(geminiResponses)
    }

    /**
     * There was an error generating text
     */
    data class Error(
        val errorMessage: String,
        var currLocation : Location? = null
    ) : FixMeUiState
}

