@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_details

import com.alimoradi.file_api.FileChildrenManager
import com.alimoradi.file_api.FileManager
import com.alimoradi.file_api.FileShareManager
import com.alimoradi.file_api.FileSizeManager

interface FileDetailsContract {

    interface UserAction {

        fun onCreate(path: String)

        fun onDestroy()

        fun onSetFileManagers(
            fileManager: FileManager,
            fileChildrenManager: FileChildrenManager,
            fileShareManager: FileShareManager,
            fileSizeManager: FileSizeManager
        )

        fun onSharedClicked()
    }

    interface Screen {

        fun setPathText(text: String)

        fun setNameText(text: String)

        fun setSizeText(text: String)

        fun showShareButton()

        fun hideShareButton()
    }
}
