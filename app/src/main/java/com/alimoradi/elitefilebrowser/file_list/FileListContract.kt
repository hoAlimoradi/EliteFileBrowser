@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_list

import androidx.annotation.ColorRes
import com.alimoradi.file_api.File
import com.alimoradi.file_api.FileChildrenManager
import com.alimoradi.file_api.FileOpenManager

interface FileListContract {

    interface UserAction {

        fun onAttached()

        fun onDetached()

        fun onRefresh()

        fun onFileClicked(file: File)

        fun onFabUpArrowClicked()

        fun onSetFileManagers(
            fileChildrenManager: FileChildrenManager,
            fileOpenManager: FileOpenManager,
            rootPath: String
        )

        fun getCurrentPath(): String
    }

    interface Screen {

        fun showEmptyView()

        fun hideEmptyView()

        fun showErrorMessage()

        fun hideErrorMessage()

        fun showFiles(files: List<File>)

        fun hideFiles()

        fun showLoader()

        fun hideLoader()

        fun selectPath(path: String?)

        fun showFabUpArrow()

        fun hideFabUpArrow()

        fun setEmptyTextColorRes(@ColorRes colorRes: Int)

        fun setErrorTextColorRes(@ColorRes colorRes: Int)

        fun notifyListenerCurrentPathChanged(currentPath: String)
    }
}
