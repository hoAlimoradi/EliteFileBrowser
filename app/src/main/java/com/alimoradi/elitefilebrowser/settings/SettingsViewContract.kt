package com.alimoradi.elitefilebrowser.settings

interface SettingsViewContract {

    interface UserAction {

        fun onAttached()

        fun onDetached()
    }

    interface Screen {

        fun populate(list: List<Any>)
    }
}
