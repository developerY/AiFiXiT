package com.ylabz.fixme

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * Still loading
     */
    object Loading : UiState

    /**
     * Text has been generated
     */
    data class Success(val outputText: String) : UiState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiState
}

