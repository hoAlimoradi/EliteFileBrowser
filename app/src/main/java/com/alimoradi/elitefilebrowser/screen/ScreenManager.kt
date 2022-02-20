package com.alimoradi.elitefilebrowser.screen

import com.alimoradi.elitefilebrowser.file_provider.FileProvider

interface ScreenManager {

    fun startFileDetails(
        path: String,
        fileProvider: FileProvider
    )

    fun startPermission()

    fun startSystemSettingsStorage()

    fun startSearch()
}
