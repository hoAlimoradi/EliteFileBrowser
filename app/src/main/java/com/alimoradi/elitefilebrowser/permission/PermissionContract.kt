package com.alimoradi.elitefilebrowser.permission

interface PermissionContract {

    interface UserAction {

        fun onPermissionAllowClicked()
    }

    interface Screen {

        fun requestStoragePermission()

        fun requestScopedStoragePermission()
    }
}
