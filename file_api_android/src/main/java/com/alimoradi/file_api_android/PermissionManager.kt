package com.alimoradi.file_api_android

interface PermissionManager {

    fun hasStoragePermission(): Boolean

    fun requestStoragePermissionIfRequired(): Boolean
}
