package com.alimoradi.file_api

interface FileChildrenManager {

    fun loadFileChildren(
        path: String,
        forceRefresh: Boolean = false
    ): FileChildrenResult

    fun getFileChildren(
        path: String
    ): FileChildrenResult

    fun registerFileChildrenResultListener(
        listener: FileChildrenResultListener
    )

    fun unregisterFileChildrenResultListener(
        listener: FileChildrenResultListener
    )

    interface FileChildrenResultListener {

        fun onFileChildrenResultChanged(
            path: String
        )
    }
}
