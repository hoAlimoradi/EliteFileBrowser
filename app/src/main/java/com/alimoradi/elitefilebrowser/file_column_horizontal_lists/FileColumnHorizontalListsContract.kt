@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_column_horizontal_lists

import com.alimoradi.file_api.File

interface FileColumnHorizontalListsContract {

    interface UserAction {

        fun onAttached()

        fun onDetached()

        fun onFileClicked(index: Int, file: File)

        fun onFileLongClicked(index: Int, file: File)

        fun onFabClicked()
    }

    interface Screen {

        fun createList(path: String, index: Int)

        fun setPath(path: String, index: Int)

        fun setListsSize(size: Int)

        fun scrollEnd()

        fun selectPath(path: String?)

        fun showFab()

        fun hideFab()

        fun setFabIcon(drawableRes: Int)

        fun showFileDetailView(file: File)

        fun hideFileDetailView()
    }
}
