package com.alimoradi.file_api_android.internal.file_children

import com.alimoradi.file_api.FileChildrenResult

internal interface FileChildrenResultLoader {

    fun loadFileChildrenSync(
        parentPath: String
    ): FileChildrenResult
}
