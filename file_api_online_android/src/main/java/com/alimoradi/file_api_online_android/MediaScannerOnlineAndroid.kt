package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.File
import com.alimoradi.file_api.MediaScanner

internal class MediaScannerOnlineAndroid : MediaScanner {

    private var refreshListeners = ArrayList<MediaScanner.RefreshListener>()

    override fun refresh(
        path: String
    ) {
        val cleanedPath = File.cleanPath(path)
        for (listener in refreshListeners) {
            listener.onContentChanged(cleanedPath)
        }
    }

    override fun registerListener(
        listener: MediaScanner.RefreshListener
    ) {
        if (refreshListeners.contains(listener)) {
            return
        }
        refreshListeners.add(listener)
    }

    override fun unregisterListener(
        listener: MediaScanner.RefreshListener
    ) {
        refreshListeners.remove(listener)
    }
}
