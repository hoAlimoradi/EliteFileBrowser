package com.alimoradi.elitefilebrowser.toast

import android.content.Context

class ToastModule(
    val context: Context
) {

    fun createToastManager(): ToastManager {
        return ToastManagerImpl(
            context.applicationContext
        )
    }
}
