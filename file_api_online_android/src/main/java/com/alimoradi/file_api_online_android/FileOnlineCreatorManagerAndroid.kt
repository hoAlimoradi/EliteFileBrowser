package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.File
import com.alimoradi.file_api.FileCreatorManager
import com.alimoradi.file_api.MediaScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class FileOnlineCreatorManagerAndroid(
    private val fileOnlineApi: FileOnlineApi,
    private val mediaScanner: MediaScanner
) : FileCreatorManager {

    override fun create(
        parentPath: String,
        name: String
    ) {
        val file = File.create(
            parentPath,
            name
        )
        GlobalScope.launch(Dispatchers.Default) {
            fileOnlineApi.post(file)
            GlobalScope.launch(Dispatchers.Main) {
                mediaScanner.refresh(file.path)
                file.parentPath?.let {
                    mediaScanner.refresh(it)
                }
            }
        }
    }

    override fun createWithStringContent(parentPath: String, name: String, fileContent: String) {

    }

    override fun createWithByteArrayContent(
        parentPath: String,
        name: String,
        fileContent: ByteArray
    ) {

    }
}
