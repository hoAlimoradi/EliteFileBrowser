@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.settings_theme

import androidx.annotation.ColorRes

interface SettingsThemeContract {

    interface Screen {

        fun setDarkThemeCheckBox(checked: Boolean)

        fun setSectionColor(@ColorRes colorRes: Int)

        fun setTextPrimaryColorRes(@ColorRes colorRes: Int)

        fun setTextSecondaryColorRes(@ColorRes colorRes: Int)
    }

    interface UserAction {

        fun onAttached()

        fun onDetached()

        fun onDarkThemeCheckBoxCheckedChanged(isChecked: Boolean)
    }
}
