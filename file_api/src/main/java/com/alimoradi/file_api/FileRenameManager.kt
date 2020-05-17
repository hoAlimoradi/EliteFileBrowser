package com.alimoradi.file_api

interface FileRenameManager {

    fun rename(
        path: String,
        fileName: String
    )
}
