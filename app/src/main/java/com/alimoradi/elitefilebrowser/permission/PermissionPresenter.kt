package com.alimoradi.elitefilebrowser.permission

import com.alimoradi.file_api_android.FileScopedStorageManager

class PermissionPresenter(
    private val screen: PermissionContract.Screen,
    private val fileScopedStorageManager: FileScopedStorageManager
) : PermissionContract.UserAction {

    override fun onPermissionAllowClicked() {
        if (fileScopedStorageManager.isScopedStorage()) {
            screen.requestScopedStoragePermission()
        } else {
            screen.requestStoragePermission()
        }
    }
}
