package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.FileCopyCutManager
import com.alimoradi.file_api.MediaScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class FileOnlineCopyCutManagerAndroid(
    private val fileOnlineApi: FileOnlineApi,
    private val mediaScanner: MediaScanner
) : FileCopyCutManager {

    private var pathToCopy: String? = null
    private var pathToCut: String? = null
    private val fileToPasteChangedListeners = ArrayList<FileCopyCutManager.FileToPasteChangedListener>()
    private val pasteListeners = ArrayList<FileCopyCutManager.PasteListener>()

    override fun copy(path: String) {
        pathToCopy = path
        pathToCut = null
        for (listener in fileToPasteChangedListeners) {
            listener.onFileToPasteChanged()
        }
    }

    override fun copy(pathInput: String, pathDirectoryOutput: String) {
        GlobalScope.launch(Dispatchers.Default) {
            copySync(pathInput, pathDirectoryOutput)
            val ioFileInput = java.io.File(pathInput)
            if (ioFileInput.isDirectory) {
                mediaScanner.refresh(pathInput)
            } else {
                mediaScanner.refresh(ioFileInput.parentFile.absolutePath)
            }
            mediaScanner.refresh(pathDirectoryOutput)
            GlobalScope.launch(Dispatchers.Main) {
                cancelCopyCut()
                for (listener in pasteListeners) {
                    listener.onPasteEnded(pathInput, pathDirectoryOutput)
                }
            }
        }
    }

    override fun cut(path: String) {
        pathToCopy = null
        pathToCut = path
        for (listener in fileToPasteChangedListeners) {
            listener.onFileToPasteChanged()
        }
    }

    override fun cut(pathInput: String, pathDirectoryOutput: String) {
        GlobalScope.launch(Dispatchers.Default) {
            cutSync(pathInput, pathDirectoryOutput)
            val ioFileInput = java.io.File(pathInput)
            if (ioFileInput.isDirectory) {
                mediaScanner.refresh(pathInput)
            } else {
                mediaScanner.refresh(ioFileInput.parentFile.absolutePath)
            }
            mediaScanner.refresh(pathDirectoryOutput)
            GlobalScope.launch(Dispatchers.Main) {
                cancelCopyCut()
                for (listener in pasteListeners) {
                    listener.onPasteEnded(pathInput, pathDirectoryOutput)
                }
            }
        }
    }

    override fun paste(pathDirectoryOutput: String) {
        if (pathToCopy != null && pathToCut != null) {
            throw IllegalStateException("copy and cut in the same time not supported")
        }
        if (pathToCopy != null) {
            copy(pathToCopy!!, pathDirectoryOutput)
        } else if (pathToCut != null) {
            cut(pathToCut!!, pathDirectoryOutput)
        }
    }

    override fun cancelCopyCut() {
        if (pathToCopy == null && pathToCut == null) {
            return
        }
        pathToCopy = null
        pathToCut = null
        for (listener in fileToPasteChangedListeners) {
            listener.onFileToPasteChanged()
        }
    }

    override fun getFileToPastePath(): String? {
        if (pathToCopy != null && pathToCut != null) {
            throw IllegalStateException("copy and cut in the same time not supported")
        }
        return when {
            pathToCopy != null -> pathToCopy
            pathToCut != null -> pathToCut
            else -> null
        }
    }

    override fun registerFileToPasteChangedListener(listener: FileCopyCutManager.FileToPasteChangedListener) {
        if (fileToPasteChangedListeners.contains(listener)) {
            return
        }
        fileToPasteChangedListeners.add(listener)
    }

    override fun unregisterFileToPasteChangedListener(listener: FileCopyCutManager.FileToPasteChangedListener) {
        fileToPasteChangedListeners.remove(listener)
    }

    override fun registerPasteListener(listener: FileCopyCutManager.PasteListener) {
        if (pasteListeners.contains(listener)) {
            return
        }
        pasteListeners.add(listener)
    }

    override fun unregisterPasteListener(listener: FileCopyCutManager.PasteListener) {
        pasteListeners.remove(listener)
    }

    private fun copySync(pathInput: String, pathDirectoryOutput: String) {
        fileOnlineApi.copy(pathInput, pathDirectoryOutput)
    }

    private fun cutSync(pathInput: String, pathDirectoryOutput: String) {
        fileOnlineApi.cut(pathInput, pathDirectoryOutput)
    }
}
