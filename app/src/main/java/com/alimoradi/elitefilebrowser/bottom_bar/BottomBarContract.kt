@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.bottom_bar

import android.os.Bundle
import androidx.annotation.ColorRes

interface BottomBarContract {

    interface UserAction {

        fun onAttached()

        fun onDetached()

        fun onSaveInstanceState(saveState: Bundle)

        fun onRestoreInstanceState(state: Bundle)

        fun onFileClicked()

        fun onNoteClicked()

        fun onSettingsClicked()

        fun onSelectFile()

        fun onSelectNote()

        fun onSelectSettings()
    }

    interface Screen {

        fun notifyListenerFileClicked()

        fun notifyListenerNoteClicked()

        fun notifyListenerSettingsClicked()

        fun setFileIconColorRes(@ColorRes colorRes: Int)

        fun setNoteIconColorRes(@ColorRes colorRes: Int)

        fun setSettingsIconColorRes(@ColorRes colorRes: Int)

        fun setSectionFileTextColorRes(@ColorRes colorRes: Int)

        fun setSectionNoteTextColorRes(@ColorRes colorRes: Int)

        fun setSectionSettingsTextColorRes(@ColorRes colorRes: Int)
    }
}
