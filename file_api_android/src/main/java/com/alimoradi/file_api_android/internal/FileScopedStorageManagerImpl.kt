package com.alimoradi.file_api_android.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.alimoradi.file_api_android.FileScopedStorageManager

class FileScopedStorageManagerImpl(
    private val context: Context
) : FileScopedStorageManager {

    override fun isScopedStorage(): Boolean {
        return false
        /*
        if (checkStoragePermission()) {
            return false
        }
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
            BuildCompat.isAtLeastQ()
            */
    }

    /**
     * Check a permission.
     *
     * @param context The current [Context].
     * @return True if all the permissions are [PackageManager.PERMISSION_GRANTED].
     */
    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
