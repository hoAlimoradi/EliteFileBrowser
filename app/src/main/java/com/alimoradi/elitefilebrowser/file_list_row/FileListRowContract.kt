@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_list_row

import androidx.annotation.ColorRes
import com.alimoradi.elitefilebrowser.file_provider.FileProvider
import com.alimoradi.file_api.File
import com.alimoradi.file_api.FileCopyCutManager
import com.alimoradi.file_api.FileDeleteManager
import com.alimoradi.file_api.FileRenameManager
import com.alimoradi.file_api.FileSizeManager

interface FileListRowContract {

    interface UserAction {

        fun onAttached()

        fun onDetached()

        fun onFileChanged(file: File, selectedPath: String?)

        fun onRowClicked()

        fun onRowLongClicked()

        fun onCopyClicked()

        fun onCutClicked()

        fun onDeleteClicked()

        fun onDeleteConfirmedClicked()

        fun onRenameClicked()

        fun onZipClicked()

        fun onUnZipClicked()

        fun onRenameConfirmedClicked(fileName: String)

        fun onDetailsClicked()

        fun onOverflowClicked()

        fun onSetFileManagers(
            fileProvider: FileProvider,
            fileCopyCutManager: FileCopyCutManager,
            fileDeleteManager: FileDeleteManager,
            fileRenameManager: FileRenameManager,
            fileSizeManager: FileSizeManager
        )
    }

    interface Screen {

        fun setTitle(title: String)

        fun setSubtitle(subtitle: String)

        fun setSoundIconVisibility(visible: Boolean)

        fun setIcon(directory: Boolean)

        fun notifyRowClicked(file: File)

        fun notifyRowLongClicked(file: File)

        fun showOverflowPopupMenu()

        fun showDeleteConfirmation(fileName: String)

        fun showRenamePrompt(fileName: String)

        fun setTitleTextColorRes(@ColorRes colorRes: Int)

        fun setSubtitleTextColorRes(@ColorRes colorRes: Int)

        fun setCardBackgroundColorRes(@ColorRes colorRes: Int)
    }
}
