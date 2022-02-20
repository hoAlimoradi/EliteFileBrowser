package com.alimoradi.elitefilebrowser.file_provider_root

import com.alimoradi.elitefilebrowser.file_provider.FileProvider

interface FileProviderRootManager {

    fun getFileRootPath(
        fileProvider: FileProvider
    ): String
}
