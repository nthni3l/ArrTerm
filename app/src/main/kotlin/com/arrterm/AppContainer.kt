package com.arrterm

import android.content.Context
import com.arrterm.data.settings.ServerConfigRepository

class AppContainer(context: Context) {
    val serverConfigRepository = ServerConfigRepository(context.applicationContext)
}
