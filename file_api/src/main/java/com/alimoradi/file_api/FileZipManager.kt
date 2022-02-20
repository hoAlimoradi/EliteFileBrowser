package com.alimoradi.file_api

interface FileZipManager {

    fun isZip(
        path: String
    ): Boolean
/*
    fun unzip(
        path: String,
        outputPath: String,
        filename: String
    )*/
    fun unzip(
        path: String,
        outputPath: String
    )

    /*fun zip(
        path: String,
        outputPath: String
    )*/

    fun zip(
        path: String,
        outputPath: String,
        filename: String
    )

    fun registerFileZipListener(
        listener: FileZipListener
    )

    fun unregisterFileZipListener(
        listener: FileZipListener
    )

    interface FileZipListener {

        fun onUnzipEnded(
            path: String,
            outputPath: String
        )
    }
}
