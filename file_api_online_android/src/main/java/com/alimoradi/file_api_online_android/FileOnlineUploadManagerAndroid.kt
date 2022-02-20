package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.MediaScanner
import com.alimoradi.file_api.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class FileOnlineUploadManagerAndroid(
    private val fileOnlineApi: FileOnlineApi,
    private val mediaScanner: MediaScanner
) : FileOnlineUploadManager {

    private val listeners = ArrayList<FileOnlineUploadManager.UploadListener>()
    private val uploadProgressListener = createUploadProgressListener()

    override fun upload(
        inputJavaFile: java.io.File,
        outputFile: File
    ) {
        notifyUploadStarted(outputFile)
        GlobalScope.launch(Dispatchers.Default) {
            fileOnlineApi.postUpload(
                inputJavaFile,
                outputFile,
                uploadProgressListener
            )
            GlobalScope.launch(Dispatchers.Main) {
                mediaScanner.refresh(outputFile.path)
                outputFile.parentPath?.let {
                    mediaScanner.refresh(it)
                }
                notifyUploadEnded(outputFile)
            }
        }
    }

    override fun registerListener(
        listener: FileOnlineUploadManager.UploadListener
    ) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun unregisterListener(
        listener: FileOnlineUploadManager.UploadListener
    ) {
        listeners.remove(listener)
    }

    private fun notifyUploadStarted(
        file: File
    ) {
        for (listener in listeners) {
            listener.onUploadStarted(file)
        }
    }

    private fun notifyUploadProgress(
        file: File,
        current: Long,
        size: Long
    ) {
        for (listener in listeners) {
            listener.onUploadProgress(file, current, size)
        }
    }

    private fun notifyUploadEnded(
        file: File
    ) {
        for (listener in listeners) {
            listener.onUploadEnded(file)
        }
    }

    private fun createUploadProgressListener() = object : FileOnlineApi.UploadProgressListener {
        override fun onUploadProgress(
            file: File,
            current: Long,
            size: Long
        ) {
            notifyUploadProgress(
                file,
                current,
                size
            )
        }
    }
}
