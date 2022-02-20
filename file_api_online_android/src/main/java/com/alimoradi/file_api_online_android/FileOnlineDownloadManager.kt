package com.alimoradi.file_api_online_android

interface FileOnlineDownloadManager {

    fun download(
        inputFilePath: String,
        outputJavaFile: java.io.File
    )

    fun registerListener(listener: DownloadListener)

    fun unregisterListener(listener: DownloadListener)

    interface DownloadListener {

        fun onDownloadStarted(
            inputFilePath: String
        )

        fun onDownloadProgress(
            inputFilePath: String,
            current: Long,
            size: Long
        )

        fun onDownloadEnded(
            inputFilePath: String,
            outputJavaFile: java.io.File
        )
    }
}
