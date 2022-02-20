package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.File

interface FileOnlineUploadManager {

    fun upload(
        inputJavaFile: java.io.File,
        outputFile: File
    )

    fun registerListener(listener: UploadListener)

    fun unregisterListener(listener: UploadListener)

    interface UploadListener {

        fun onUploadStarted(
            file: File
        )

        fun onUploadProgress(
            file: File,
            current: Long,
            size: Long
        )

        fun onUploadEnded(
            file: File
        )
    }
}
