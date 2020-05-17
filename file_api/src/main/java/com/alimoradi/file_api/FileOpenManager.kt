package com.alimoradi.file_api

interface FileOpenManager {

    fun open(
        path: String,
        mime: String? = null
    )
}
