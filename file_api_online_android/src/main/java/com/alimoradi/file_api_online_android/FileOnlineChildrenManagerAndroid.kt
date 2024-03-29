package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.FileChildrenResult
import com.alimoradi.file_api.FileChildrenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class FileOnlineChildrenManagerAndroid(
    private val fileOnlineApi: FileOnlineApi
) : FileChildrenManager {

    private val fileChildrenResultMap = HashMap<String, FileChildrenResult>()
    private val fileChildrenResultListeners = ArrayList<FileChildrenManager.FileChildrenResultListener>()

    override fun loadFileChildren(path: String, forceRefresh: Boolean): FileChildrenResult {
        if (fileChildrenResultMap.contains(path)) {
            val status = fileChildrenResultMap[path]!!.status
            if (status == FileChildrenResult.Status.LOADING) {
                return getFileChildren(path)
            }
            if (status == FileChildrenResult.Status.LOADED_SUCCEEDED && !forceRefresh) {
                return getFileChildren(path)
            }
        }
        fileChildrenResultMap[path] = FileChildrenResult.createLoading(path)
        GlobalScope.launch(Dispatchers.Default) {
            val fileChildrenResult = loadFileChildrenSync(path)
            GlobalScope.launch(Dispatchers.Main) {
                fileChildrenResultMap[path] = fileChildrenResult
                for (listener in fileChildrenResultListeners) {
                    listener.onFileChildrenResultChanged(path)
                }
            }
        }
        return getFileChildren(path)
    }

    override fun getFileChildren(path: String): FileChildrenResult {
        if (fileChildrenResultMap.contains(path)) {
            return fileChildrenResultMap[path]!!
        }
        val fileChildrenResultUnloaded = FileChildrenResult.createUnloaded(path)
        fileChildrenResultMap[path] = fileChildrenResultUnloaded
        return fileChildrenResultUnloaded
    }

    override fun registerFileChildrenResultListener(listener: FileChildrenManager.FileChildrenResultListener) {
        if (fileChildrenResultListeners.contains(listener)) {
            return
        }
        fileChildrenResultListeners.add(listener)
    }

    override fun unregisterFileChildrenResultListener(listener: FileChildrenManager.FileChildrenResultListener) {
        fileChildrenResultListeners.remove(listener)
    }

    fun refresh(path: String) {
        if (!fileChildrenResultMap.containsKey(path)) {
            return
        }
        loadFileChildren(path, true)
    }

    private fun loadFileChildrenSync(path: String): FileChildrenResult {
        val serverResponseFiles = fileOnlineApi.getChildren(path)
            ?: return FileChildrenResult.createErrorNetwork(path)
        return FileChildrenResult.createLoaded(path, serverResponseFiles.getFiles())
    }
}
