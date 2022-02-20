package com.alimoradi.file_api_online_android

import android.content.Context
import com.alimoradi.file_api.MediaScanner
import com.alimoradi.file_api.FileManager
import com.alimoradi.file_api.FileCopyCutManager
import com.alimoradi.file_api.FileCreatorManager
import com.alimoradi.file_api.FileDeleteManager
import com.alimoradi.file_api.FileRenameManager
import com.alimoradi.file_api.FileSizeManager
import com.alimoradi.file_api.FileChildrenManager
import com.alimoradi.file_api.FileShareManager
import com.alimoradi.file_api_online.FileOnlineLoginRepository
import com.alimoradi.file_api_online.FileOnlineModule

class FileOnlineAndroidModule(
    private val context: Context,
    private val fileOnlineApiNetwork: FileOnlineApiNetwork,
    private val localMediaScanner: MediaScanner
) {

    private val onlineMediaScanner: MediaScanner by lazy {
        MediaScannerOnlineAndroid()
    }

    private val fileOnlineModule by lazy { FileOnlineModule() }
    private val fileOnlineLoginRepository by lazy { createFileOnlineLoginRepository() }
    private val fileOnlineLoginManagerInternal by lazy { createFileOnlineLoginManager() }

    private val fileOnlineApi by lazy {
        createFileOnlineApi()
    }

    fun createFileOnlineManager(): FileManager {
        val fileManager = FileOnlineManagerAndroid(
            fileOnlineApi
        )
        onlineMediaScanner.registerListener(object : MediaScanner.RefreshListener {
            override fun onContentChanged(path: String) {
                fileManager.refresh(path)
            }
        })
        return fileManager
    }

    fun createFileOnlineChildrenManager(): FileChildrenManager {
        val fileManager = FileOnlineChildrenManagerAndroid(
            fileOnlineApi
        )
        onlineMediaScanner.registerListener(object : MediaScanner.RefreshListener {
            override fun onContentChanged(path: String) {
                fileManager.refresh(path)
            }
        })
        return fileManager
    }

    fun createFileOnlineCopyCutManager(): FileCopyCutManager {
        return FileOnlineCopyCutManagerAndroid(
            fileOnlineApi,
            onlineMediaScanner
        )
    }

    fun createFileOnlineCreatorManager(): FileCreatorManager {
        return FileOnlineCreatorManagerAndroid(
            fileOnlineApi,
            onlineMediaScanner
        )
    }

    fun createFileOnlineDeleteManager(): FileDeleteManager {
        return FileOnlineDeleteManagerAndroid(
            fileOnlineApi,
            onlineMediaScanner
        )
    }

    fun createFileOnlineDownloadManager(): FileOnlineDownloadManager {
        return FileOnlineDownloadManagerAndroid(
            fileOnlineApi,
            localMediaScanner
        )
    }

    fun getFileOnlineLoginManager() = fileOnlineLoginManagerInternal

    fun createFileOnlineRenameManager(): FileRenameManager {
        return FileOnlineRenameManagerAndroid(
            fileOnlineApi,
            onlineMediaScanner
        )
    }

    fun createFileOnlineShareManager(): FileShareManager {
        return FileOnlineShareManagerAndroid()
    }

    fun createFileOnlineSizeManager(): FileSizeManager {
        val fileSizeManager = FileOnlineSizeManagerAndroid(
            fileOnlineApi
        )
        onlineMediaScanner.registerListener(object : MediaScanner.RefreshListener {
            override fun onContentChanged(path: String) {
                fileSizeManager.loadSize(path, true)
            }
        })
        return fileSizeManager
    }

    fun createFileOnlineUploadManager(): FileOnlineUploadManager {
        return FileOnlineUploadManagerAndroid(
            fileOnlineApi,
            onlineMediaScanner
        )
    }

    private fun createFileOnlineLoginManager() = fileOnlineModule.createFileOnlineLoginManager(
        fileOnlineLoginRepository
    )

    private fun createFileOnlineLoginRepository(): FileOnlineLoginRepository {
        val sharedPreferences = context.getSharedPreferences(
            FileOnlineLoginRepositoryImpl.PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return FileOnlineLoginRepositoryImpl(
            sharedPreferences
        )
    }

    private fun createFileOnlineApi(): FileOnlineApi {
        return FileOnlineApiImpl(
            fileOnlineApiNetwork,
            fileOnlineLoginManagerInternal
        )
    }
}
