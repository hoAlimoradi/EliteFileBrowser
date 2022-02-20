@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_selection

import android.content.Context
import android.os.Environment
import android.util.AttributeSet
import android.widget.FrameLayout
import com.alimoradi.elitefilebrowser.file_provider.FileProvider
import com.alimoradi.elitefilebrowser.file_list.FileListView
import com.alimoradi.elitefilebrowser.main.ApplicationGraph
import com.alimoradi.file_api.FileCopyCutManager
import com.alimoradi.file_api.FileOpenManager

class FileSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val fileColumnListView = FileListView(context)
    private val fileChildrenManager = ApplicationGraph.getFileChildrenManager()
    private val fileDeleteManager = ApplicationGraph.getFileDeleteManager()
    private val fileRenameManager = ApplicationGraph.getFileRenameManager()
    private val fileSizeManager = ApplicationGraph.getFileSizeManager()

    private var fileOpenManager: FileOpenManager? = null

    init {
        val fileOpenManager = object : FileOpenManager {
            override fun open(path: String, mime: String?) {
                fileOpenManager?.open(path, mime)
            }
        }
        val fileCopyCutManager = object : FileCopyCutManager {
            override fun copy(path: String) {
            }

            override fun copy(pathInput: String, pathDirectoryOutput: String) {
            }

            override fun cut(path: String) {
            }

            override fun cut(pathInput: String, pathDirectoryOutput: String) {
            }

            override fun getFileToPastePath(): String? {
                return null
            }

            override fun paste(pathDirectoryOutput: String) {
            }

            override fun cancelCopyCut() {
            }

            override fun registerFileToPasteChangedListener(listener: FileCopyCutManager.FileToPasteChangedListener) {
            }

            override fun unregisterFileToPasteChangedListener(listener: FileCopyCutManager.FileToPasteChangedListener) {
            }

            override fun registerPasteListener(listener: FileCopyCutManager.PasteListener) {
            }

            override fun unregisterPasteListener(listener: FileCopyCutManager.PasteListener) {
            }
        }
        fileColumnListView.setFileManagers(
            FileProvider.Local,
            fileChildrenManager,
            fileDeleteManager,
            fileCopyCutManager,
            fileOpenManager,
            fileRenameManager,
            fileSizeManager,
            Environment.getExternalStorageDirectory().absolutePath
        )
        addView(fileColumnListView)
    }

    fun setFileOpenManager(fileOpenManager: FileOpenManager) {
        this.fileOpenManager = fileOpenManager
    }
}
