package com.alimoradi.file_api

interface FileCreatorManager {

    fun create(
        parentPath: String,
        name: String
    )
}
