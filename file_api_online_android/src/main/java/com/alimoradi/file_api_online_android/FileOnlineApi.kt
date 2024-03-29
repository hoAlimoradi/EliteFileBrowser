package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.File
import com.alimoradi.file_api_online.response_json.ServerResponse
import com.alimoradi.file_api_online.response_json.ServerResponseFile
import com.alimoradi.file_api_online.response_json.ServerResponseFiles

internal interface FileOnlineApi {

    fun get(): ServerResponseFiles?

    fun get(path: String): ServerResponseFile?

    fun getChildren(
        parentPath: String
    ): ServerResponseFiles?

    fun getSize(
        path: String
    ): ServerResponse?

    fun post(
        file: File
    )

    fun postDownload(
        inputFilePath: String,
        outputJavaFile: java.io.File,
        listener: DownloadProgressListener
    )

    fun postUpload(
        inputJavaFile: java.io.File,
        outputFile: File,
        listener: UploadProgressListener
    )

    fun delete(
        path: String
    ): Boolean

    fun rename(
        path: String,
        name: String
    ): Boolean

    fun copy(
        pathInput: String,
        pathDirectoryOutput: String
    ): Boolean

    fun cut(
        pathInput: String,
        pathDirectoryOutput: String
    ): Boolean

    interface DownloadProgressListener {

        fun onDownloadProgress(
            inputFilePath: String,
            current: Long,
            size: Long
        )
    }

    interface UploadProgressListener {

        fun onUploadProgress(
            file: File,
            current: Long,
            size: Long
        )
    }
}
