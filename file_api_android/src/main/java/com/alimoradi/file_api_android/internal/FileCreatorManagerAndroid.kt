package com.alimoradi.file_api_android.internal

import android.util.Log
import com.alimoradi.file_api_android.PermissionManager
import com.alimoradi.file_api.FileCreatorManager
import com.alimoradi.file_api.MediaScanner
import java.io.IOException

internal class FileCreatorManagerAndroid(
    private val permissionManager: PermissionManager,
    private val mediaScanner: MediaScanner,
    private val addOn: AddOn
) : FileCreatorManager {

    override fun create(
        parentPath: String,
        name: String
    ) {
        if (permissionManager.requestStoragePermissionIfRequired()) {
            return
        }
        if (
            parentPath.startsWith("content://")
        ) {
            val succeeded = if (name.contains(".")) {
                addOn.createFileFromContentResolver(
                    parentPath,
                    name
                )
            } else {
                addOn.createDirectoryFromContentResolver(
                    parentPath,
                    name
                )
            }
            if (succeeded) {
                mediaScanner.refresh(parentPath)
            }
            return
        }
        var pathToUse = parentPath
        val len = parentPath.length
        if (len < 1 || name.isEmpty()) {
            return
        }
        if (pathToUse[len - 1] != '/') {
            pathToUse += "/"
        }
        if (!name.contains(".")) {
            val ioFile = java.io.File(pathToUse + name)
            if (!ioFile.mkdir()) {
                return
            }
            mediaScanner.refresh(ioFile.absolutePath)
            mediaScanner.refresh(ioFile.parentFile.absolutePath)
        } else {
            try {
                val ioFile = java.io.File(pathToUse + name)
                if (!ioFile.createNewFile()) {
                    return
                }
                mediaScanner.refresh(ioFile.absolutePath)
                mediaScanner.refresh(ioFile.parentFile.absolutePath)
                return
            } catch (e: IOException) {
                return
            }
        }
        return
    }


    override fun createWithStringContent(
        parentPath: String,
        name: String,
        fileContent: String
    ) {
        if (permissionManager.requestStoragePermissionIfRequired()) {
            return
        }
        if (
            parentPath.startsWith("content://")
        ) {
            val succeeded = if (name.contains(".")) {
                addOn.createFileFromContentResolver(
                    parentPath,
                    name
                )
            } else {
                addOn.createDirectoryFromContentResolver(
                    parentPath,
                    name
                )
            }
            if (succeeded) {
                mediaScanner.refresh(parentPath)
            }
            return
        }
        var pathToUse = parentPath
        val len = parentPath.length
        if (len < 1 || name.isEmpty()) {
            return
        }
        if (pathToUse[len - 1] != '/') {
            pathToUse += "/"
        }
        if (!name.contains(".")) {
            val ioFile = java.io.File(pathToUse + name)
            ioFile.writeText(fileContent)
            Log.e("jafar", " !name.contains .  fileContent   " + fileContent)
            /*if (!ioFile.mkdir()) {
                return
            }*/
            mediaScanner.refresh(ioFile.absolutePath)
            mediaScanner.refresh(ioFile.parentFile.absolutePath)
        } else {
            try {
                val ioFile = java.io.File(pathToUse + name)
                ioFile.writeText(fileContent)
                Log.e("jafar", " name.contains .  fileContent   " + fileContent)
                /*if (!ioFile.createNewFile()) {
                    return
                }*/
                mediaScanner.refresh(ioFile.absolutePath)
                mediaScanner.refresh(ioFile.parentFile.absolutePath)
                return
            } catch (e: IOException) {
                Log.e("jafar", " IOException " + e)
                return
            }
        }
        return
    }


    override fun createWithByteArrayContent(
        parentPath: String,
        name: String,
        fileContent: ByteArray
    ) {
        if (permissionManager.requestStoragePermissionIfRequired()) {
            return
        }
        if (
            parentPath.startsWith("content://")
        ) {
            val succeeded = if (name.contains(".")) {
                addOn.createFileFromContentResolver(
                    parentPath,
                    name
                )
            } else {
                addOn.createDirectoryFromContentResolver(
                    parentPath,
                    name
                )
            }
            if (succeeded) {
                mediaScanner.refresh(parentPath)
            }
            return
        }
        var pathToUse = parentPath
        val len = parentPath.length
        if (len < 1 || name.isEmpty()) {
            return
        }
        if (pathToUse[len - 1] != '/') {
            pathToUse += "/"
        }
        if (!name.contains(".")) {
            val ioFile = java.io.File(pathToUse + name)
            ioFile.writeBytes(fileContent)
            Log.e("jafar", " !name.contains .  fileContent   " + fileContent)
            /*if (!ioFile.mkdir()) {
                return
            }*/
            mediaScanner.refresh(ioFile.absolutePath)
            mediaScanner.refresh(ioFile.parentFile.absolutePath)
        } else {
            try {
                val ioFile = java.io.File(pathToUse + name)
                ioFile.writeBytes(fileContent)
                Log.e("jafar", " name.contains .  fileContent   " + fileContent)
                /*if (!ioFile.createNewFile()) {
                    return
                }*/
                mediaScanner.refresh(ioFile.absolutePath)
                mediaScanner.refresh(ioFile.parentFile.absolutePath)
                return
            } catch (e: IOException) {
                Log.e("jafar", " IOException " + e)
                return
            }
        }
        return
    }
    interface AddOn {

        fun createFileFromContentResolver(
            parentPath: String,
            name: String
        ): Boolean

        fun createDirectoryFromContentResolver(
            parentPath: String,
            name: String
        ): Boolean
    }
}
