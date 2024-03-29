package com.alimoradi.file_api_android.internal.file_children

import com.alimoradi.file_api.FileChildrenResult

internal class FileChildrenResultLoaderImpl(
    private val fileChildrenResultLoaderFile: FileChildrenResultLoader,
    private val fileChildrenResultLoaderContentResolver: FileChildrenResultLoader,
    private val addOn: AddOn
) : FileChildrenResultLoader {

    override fun loadFileChildrenSync(
        parentPath: String
    ): FileChildrenResult {
        val fileChildrenResult = if (
            parentPath.startsWith("content://")
        ) {
            fileChildrenResultLoaderContentResolver.loadFileChildrenSync(
                parentPath
            )
        } else {
            fileChildrenResultLoaderFile.loadFileChildrenSync(
                parentPath
            )
        }
        for (file in fileChildrenResult.getFiles()) {
            if (!file.directory) {
                addOn.onFileSizeComputed(
                    file.path,
                    file.length
                )
            }
        }
        return fileChildrenResult
    }

    interface AddOn {

        fun onFileSizeComputed(
            path: String,
            length: Long
        )
    }
}
