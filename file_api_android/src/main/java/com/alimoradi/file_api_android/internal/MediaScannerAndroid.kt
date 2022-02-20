package com.alimoradi.file_api_android.internal

import com.alimoradi.file_api.MediaScanner

internal class MediaScannerAndroid(
    private val addOn: AddOn
) : MediaScanner {

    private var refreshListeners = ArrayList<MediaScanner.RefreshListener>()

    override fun refresh(
        path: String
    ) {
        addOn.refreshSystemMediaScanDataBase(path)
        for (listener in refreshListeners) {
            listener.onContentChanged(path)
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

    interface AddOn {

        /**
         * @param docPath : absolute path of file for which broadcast will be send to refresh
         * media database
         */
        fun refreshSystemMediaScanDataBase(
            path: String
        )
    }
}
