package com.alimoradi.elitefilebrowser.file_provider_root

import com.alimoradi.elitefilebrowser.file_provider.FileProvider
import com.alimoradi.file_api.FileRootManager

class FileProviderRootManagerImpl(
    private val fileRootManager: FileRootManager
) : FileProviderRootManager {

    override fun getFileRootPath(
        fileProvider: FileProvider
    ): String {
        @Suppress("UNUSED_EXPRESSION")
        return when (fileProvider) {
            FileProvider.Local -> fileRootManager.getFileRootPath()
        }
    }
}
