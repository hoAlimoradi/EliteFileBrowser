package com.alimoradi.elitefilebrowser.settings

import com.alimoradi.file_api_android.FileScopedStorageManager

class SettingsViewPresenter(
    private val screen: SettingsViewContract.Screen,
    private val fileScopedStorageManager: FileScopedStorageManager,
) : SettingsViewContract.UserAction {

    override fun onAttached() {
        populate()
    }

    override fun onDetached() {

    }

    private fun populate() {
        val list = ArrayList<Any>()
        list.add(SettingsAdapter.SettingsTheme())
        list.add(SettingsAdapter.SettingsStorage())
        list.add(SettingsAdapter.SettingsAuthentication())
        screen.populate(list)
    }

}
