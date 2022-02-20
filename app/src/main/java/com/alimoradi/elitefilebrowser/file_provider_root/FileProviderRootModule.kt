package com.alimoradi.elitefilebrowser.file_provider_root

import com.alimoradi.elitefilebrowser.main.ApplicationGraph

class FileProviderRootModule {

    fun createFileProviderRootManager(): FileProviderRootManager {
        val fileRootManager = ApplicationGraph.getFileRootManager()
        return FileProviderRootManagerImpl(
            fileRootManager
        )
    }
}
