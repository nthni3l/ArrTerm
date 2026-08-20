package com.arrterm.ui.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** App-wide floating toast, matching the design's single bottom-center toast slot. */
object ToastBus {
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    fun show(message: String) {
        _events.tryEmit(message)
    }
}
