package com.alimoradi.file_api_android.internal

import android.os.Environment
import com.alimoradi.file_api_android.FileScopedStorageManager
import com.alimoradi.file_api.FileRootManager

class FileRootManagerImpl(
    private val fileScopedStorageManager: FileScopedStorageManager
) : FileRootManager {

    override fun getFileRootPath(): String {
        if (fileScopedStorageManager.isScopedStorage()) {
            return "content://com.android.externalstorage.documents/tree/primary%3A"
        }
        return Environment.getExternalStorageDirectory().absolutePath
    }
}
