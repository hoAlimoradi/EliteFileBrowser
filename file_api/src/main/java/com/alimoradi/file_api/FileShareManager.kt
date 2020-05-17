package com.alimoradi.file_api

interface FileShareManager {

    fun share(
        path: String
    )

    fun isShareSupported(
        path: String
    ): Boolean
}
