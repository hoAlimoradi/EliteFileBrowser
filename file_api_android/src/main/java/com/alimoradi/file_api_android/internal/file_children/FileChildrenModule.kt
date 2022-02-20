package com.alimoradi.file_api_android.internal.file_children

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.alimoradi.file_api_android.PermissionManager
import com.alimoradi.file_api_android.RecursiveFileObserver
import com.alimoradi.file_api.FileChildrenManager
import com.alimoradi.file_api.FileRootManager
import com.alimoradi.file_api.MediaScanner
import java.io.File

internal class FileChildrenModule(
    private val context: Context,
    private val fileRootManager: FileRootManager,
    private val mediaScanner: MediaScanner,
    private val permissionManager: PermissionManager,
    private val addOn: AddOn
) {

    fun createFileChildrenManager(): FileChildrenManager {
        val fileChildrenResultLoader = createFileChildrenResultLoader()
        val fileChildrenManager = FileChildrenManagerAndroid(
            fileChildrenResultLoader,
            permissionManager
        )
        val fileObserver = RecursiveFileObserver(
            fileRootManager.getFileRootPath()
        ) {
            if (it != null && !it.endsWith("/null")) {
                val path = File(it).parentFile.absolutePath
                fileChildrenManager.refresh(path)
            }
        }
        mediaScanner.registerListener(object : MediaScanner.RefreshListener {
            override fun onContentChanged(path: String) {
                fileChildrenManager.refresh(path)
            }
        })
        fileObserver.startWatching()
        return fileChildrenManager
    }

    @SuppressLint("NewApi")
    private fun createFileChildrenResultLoader(): FileChildrenResultLoader {
        val fileChildrenResultLoaderFile = FileChildrenResultLoaderFile()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return fileChildrenResultLoaderFile
        }
        val fileChildrenResultLoaderContentResolver = FileChildrenResultLoaderContentResolver(
            context.contentResolver
        )
        return FileChildrenResultLoaderImpl(
            fileChildrenResultLoaderFile,
            fileChildrenResultLoaderContentResolver,
            object : FileChildrenResultLoaderImpl.AddOn {
                override fun onFileSizeComputed(
                    path: String,
                    length: Long
                ) {
                    addOn.onFileSizeComputed(
                        path,
                        length
                    )
                }
            }
        )
    }

    interface AddOn {

        fun onFileSizeComputed(
            path: String,
            length: Long
        )
    }
}
