package com.alimoradi.file_api_android.internal

import com.alimoradi.file_api_android.PermissionManager
import com.alimoradi.file_api.File
import com.alimoradi.file_api.FileManager
import com.alimoradi.file_api.FileResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class FileManagerAndroid(
    private val permissionManager: PermissionManager
) : FileManager {

    private val fileResultMap = HashMap<String, FileResult>()
    private val fileResultListeners = ArrayList<FileManager.FileResultListener>()

    override fun loadFile(
        path: String,
        forceRefresh: Boolean
    ): FileResult {
        if (fileResultMap.contains(path)) {
            val status = fileResultMap[path]!!.status
            if (status == FileResult.Status.LOADING) {
                return getFile(path)
            }
            if (status == FileResult.Status.LOADED_SUCCEEDED && !forceRefresh) {
                return getFile(path)
            }
        }
        if (permissionManager.requestStoragePermissionIfRequired()) {
            return getFile(path)
        }
        fileResultMap[path] = FileResult.createLoading(path)
        GlobalScope.launch(Dispatchers.Default) {
            val fileResult = loadFileSync(path)
            GlobalScope.launch(Dispatchers.Main) {
                fileResultMap[path] = fileResult
                for (listener in fileResultListeners) {
                    listener.onFileResultChanged(path)
                }
            }
        }
        return getFile(path)
    }

    override fun getFile(path: String): FileResult {
        if (fileResultMap.contains(path)) {
            return fileResultMap[path]!!
        }
        val fileResultUnloaded = FileResult.createUnloaded(path)
        fileResultMap[path] = fileResultUnloaded
        return fileResultUnloaded
    }

    override fun registerFileResultListener(listener: FileManager.FileResultListener) {
        if (fileResultListeners.contains(listener)) {
            return
        }
        fileResultListeners.add(listener)
    }

    override fun unregisterFileResultListener(listener: FileManager.FileResultListener) {
        fileResultListeners.remove(listener)
    }

    fun refresh(path: String) {
        if (!fileResultMap.containsKey(path)) {
            return
        }
        loadFile(path, true)
    }

    companion object {

        @JvmStatic
        private fun loadFileSync(path: String): FileResult {
            val ioFile = java.io.File(path)
            val file = File.create(ioFile)
            return FileResult.createLoaded(path, file)
        }
    }
}
