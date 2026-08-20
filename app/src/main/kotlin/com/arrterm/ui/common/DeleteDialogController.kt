package com.arrterm.ui.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DeleteRequest(val title: String, val onConfirm: (deleteFiles: Boolean) -> Unit)

/**
 * The delete-confirmation overlay dims the whole app, including the top bar and bottom
 * nav (per the imported design), so it's hosted once at the nav root rather than scoped
 * to whichever detail screen triggered it.
 */
object DeleteDialogController {
    private val _request = MutableStateFlow<DeleteRequest?>(null)
    val request: StateFlow<DeleteRequest?> = _request.asStateFlow()

    fun request(title: String, onConfirm: (Boolean) -> Unit) {
        _request.value = DeleteRequest(title, onConfirm)
    }

    fun dismiss() {
        _request.value = null
    }
}
