package com.alimoradi.file_api

interface FileDeleteManager {

    fun delete(
        path: String
    )

    fun registerFileDeleteCompletionListener(
        listener: FileDeleteCompletionListener
    )

    fun unregisterFileDeleteCompletionListener(
        listener: FileDeleteCompletionListener
    )

    interface FileDeleteCompletionListener {

        fun onFileDeletedCompleted(
            path: String,
            succeeded: Boolean
        )
    }
}
