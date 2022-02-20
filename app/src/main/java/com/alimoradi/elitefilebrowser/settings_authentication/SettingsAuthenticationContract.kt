package com.alimoradi.elitefilebrowser.settings_authentication

import androidx.annotation.ColorRes

interface SettingsAuthenticationContract {

    interface Screen {
        fun setAuthenticationCheckBox(checked: Boolean)

        fun setSectionColor(@ColorRes colorRes: Int)

        fun setTextPrimaryColorRes(@ColorRes colorRes: Int)

        fun setTextSecondaryColorRes(@ColorRes colorRes: Int)
    }

    interface UserAction {

        fun onAttached()

        fun onDetached()

        fun onAuthenticationCheckBoxCheckedChanged(isChecked: Boolean)
    }
}