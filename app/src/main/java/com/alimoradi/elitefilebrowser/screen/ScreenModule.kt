package com.alimoradi.elitefilebrowser.screen

import android.content.Context

class ScreenModule(
    private val context: Context
) {

    fun createScreenManager(): ScreenManager {
        return ScreenManagerImpl(
            context
        )
    }
}
