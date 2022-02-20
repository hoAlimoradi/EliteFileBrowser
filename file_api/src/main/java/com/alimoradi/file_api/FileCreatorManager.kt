package com.alimoradi.file_api

interface FileCreatorManager {

    fun create(
        parentPath: String,
        name: String
    )

    fun createWithStringContent(
        parentPath: String,
        name: String,
        fileContent: String
    )

    fun createWithByteArrayContent(
        parentPath: String,
        name: String,
        fileContent: ByteArray
    )

  /*  fun createWithByteArrayContent(
        parentPath: String,
        name: String,
        fileContent: ByteArray
    )*/

}
