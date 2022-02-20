@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.settings_storage

import androidx.annotation.ColorRes

interface SettingsStorageContract {

    interface UserAction {

        fun onAttached()

        fun onDetached()

        fun onStorageLocalRowClicked()
    }

    interface Screen {

        fun setTextPrimaryColorRes(@ColorRes colorRes: Int)

        fun setTextSecondaryColorRes(@ColorRes colorRes: Int)

        fun setSectionColor(@ColorRes colorRes: Int)

        fun setLocalSubLabelText(text: String)

        fun setLocalBusy(text: String)

        fun setLocalTotal(text: String)

        fun setProgress(percent: Float)
    }
}
