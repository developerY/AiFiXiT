package com.ylabz.fixme

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
        val outputText: String,
        var memo: String = "Needs Fixing",
        var geminiResponse : String = ""
    ) : FixMeUiState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : FixMeUiState
}

