package com.alimoradi.file_api_android.internal

import android.content.Context
import android.os.Build
import com.alimoradi.file_api_android.FileScopedStorageManager
import com.alimoradi.file_api_android.PermissionManager
import com.alimoradi.file_api_android.PermissionRequestAddOn

internal class PermissionModule(
    private val context: Context,
    private val fileScopedStorageManager: FileScopedStorageManager,
    private val permissionRequestAddOn: PermissionRequestAddOn
) {

    fun createPermissionManager(): PermissionManager {
        val permissionManagerNonScoped = PermissionManagerNonScopedImpl(
            context,
            permissionRequestAddOn
        )
        if (
            fileScopedStorageManager.isScopedStorage() &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        ) {
            return PermissionManagerScopedImpl(
                context,
                permissionManagerNonScoped,
                permissionRequestAddOn
            )
        }
        return permissionManagerNonScoped
    }
}
