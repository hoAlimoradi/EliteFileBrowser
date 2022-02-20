@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_storage_stats

class FileStorageStatsModule {

    fun createFileStorageStatsManager(): FileStorageStatsManager {
        return FileStorageStatsManagerImpl()
    }
}
