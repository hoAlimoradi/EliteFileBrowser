package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.File
import com.alimoradi.file_api.MediaScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class FileOnlineDownloadManagerAndroid(
    private val fileOnlineApi: FileOnlineApi,
    private val localMediaScanner: MediaScanner
) : FileOnlineDownloadManager {

    private val listeners = ArrayList<FileOnlineDownloadManager.DownloadListener>()
    private val uploadProgressListener = createUploadProgressListener()

    override fun download(
        inputFilePath: String,
        outputJavaFile: java.io.File
    ) {
        notifyDownloadStarted(inputFilePath)
        GlobalScope.launch(Dispatchers.Default) {
            fileOnlineApi.postDownload(
                inputFilePath,
                outputJavaFile,
                uploadProgressListener
            )
            GlobalScope.launch(Dispatchers.Main) {
                val outputFile = File.create(outputJavaFile)
                localMediaScanner.refresh(outputFile.path)
                outputFile.parentPath?.let {
                    localMediaScanner.refresh(it)
                }
                notifyDownloadEnded(inputFilePath, outputJavaFile)
            }
        }
    }

    override fun registerListener(
        listener: FileOnlineDownloadManager.DownloadListener
    ) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun unregisterListener(
        listener: FileOnlineDownloadManager.DownloadListener
    ) {
        listeners.remove(listener)
    }

    private fun notifyDownloadStarted(
        inputFilePath: String
    ) {
        for (listener in listeners) {
            listener.onDownloadStarted(inputFilePath)
        }
    }

    private fun notifyDownloadProgress(
        inputFilePath: String,
        current: Long,
        size: Long
    ) {
        for (listener in listeners) {
            listener.onDownloadProgress(inputFilePath, current, size)
        }
    }

    private fun notifyDownloadEnded(
        inputFilePath: String,
        outputJavaFile: java.io.File
    ) {
        for (listener in listeners) {
            listener.onDownloadEnded(
                inputFilePath,
                outputJavaFile
            )
        }
    }

    private fun createUploadProgressListener() = object : FileOnlineApi.DownloadProgressListener {
        override fun onDownloadProgress(
            inputFilePath: String,
            current: Long,
            size: Long
        ) {
            notifyDownloadProgress(
                inputFilePath,
                current,
                size
            )
        }
    }
}
