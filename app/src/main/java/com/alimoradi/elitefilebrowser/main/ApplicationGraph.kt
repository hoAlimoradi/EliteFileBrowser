package com.alimoradi.elitefilebrowser.main

import android.annotation.SuppressLint
import android.content.Context
import com.alimoradi.elitefilebrowser.audio.AudioModule
import com.alimoradi.elitefilebrowser.authentication.AuthenticationModule
import com.alimoradi.elitefilebrowser.dialog.DialogModule
import com.alimoradi.elitefilebrowser.network.NetworkModule
import com.alimoradi.elitefilebrowser.note.NoteModule
import com.alimoradi.elitefilebrowser.notification.NotificationModule
import com.alimoradi.elitefilebrowser.file_provider_root.FileProviderRootModule
import com.alimoradi.elitefilebrowser.file_storage_stats.FileStorageStatsModule
import com.alimoradi.elitefilebrowser.theme.ThemeModule
import com.alimoradi.elitefilebrowser.version.VersionModule
import com.alimoradi.elitefilebrowser.hash.HashModule
import com.alimoradi.elitefilebrowser.main_thread.MainThreadModule
import com.alimoradi.elitefilebrowser.ram_stats.RamStatsModule
import com.alimoradi.elitefilebrowser.screen.ScreenModule
import com.alimoradi.elitefilebrowser.toast.ToastModule
import com.alimoradi.file_api_android.FileModule
import com.alimoradi.file_api_android.PermissionRequestAddOn

class ApplicationGraph(
    private val context: Context
) {

    private val fileModule by lazy { FileModule(context, createPermissionRequestAddOn()) }
    private val networkModule by lazy { NetworkModule() }
    private val noteModule by lazy { NoteModule(context) }
    private val screenModule by lazy { ScreenModule(context) }

    private val audioManager by lazy { audioModule.createAudioManager() }
    private val audioQueueManager by lazy { audioModule.createAudioQueueManager(audioManager) }
    private val audioModule by lazy { AudioModule(fileSortManager) }
    private val dialogManager by lazy { DialogModule(context).createDialogManager() }
    private val fileManager by lazy { fileModule.createFileManager() }
    private val fileChildrenManager by lazy { fileModule.createFileChildrenManager() }
    private val fileCopyCutManager by lazy { fileModule.createFileCopyCutManager() }

    private val fileZipManager by lazy { fileModule.createFileZipManager() }

    private val fileCreatorManager by lazy { fileModule.createFileCreatorManager() }
    private val fileDeleteManager by lazy { fileModule.createFileDeleteManager() }
    private val fileMediaScanner by lazy { fileModule.getMediaScanner() }

    private val fileOpenManager by lazy { fileModule.createFileOpenManager() }
    private val filePathManager by lazy { fileModule.getFilePathManager() }
    private val fileProviderRootManager by lazy { FileProviderRootModule().createFileProviderRootManager() }
    private val fileRootManager by lazy { fileModule.getFileRootManager() }
    private val fileScopedStorageManager by lazy { fileModule.getFileScopedStorageManager() }
    private val fileShareManager by lazy { fileModule.createFileShareManager() }
    private val fileStorageStatsManager by lazy { FileStorageStatsModule().createFileStorageStatsManager() }
    private val fileRenameManager by lazy { fileModule.createFileRenameManager() }
    private val fileSizeManager by lazy { fileModule.getFileSizeManager() }
    private val fileSortManager by lazy { fileModule.createFileSortManager() }
    private val hashManager by lazy { HashModule(context).createHashManager() }
    private val mainThreadPost by lazy { MainThreadModule().createMainThreadPost() }
    private val networkManager by lazy { networkModule.createNetworkManager() }
    private val noteManager by lazy { noteModule.createNoteManager() }
    private val notificationModule by lazy { NotificationModule(context, audioManager) }
    private val notificationAudioManager by lazy { notificationModule.createNotificationAudioManager() }
    private val okHttpClientLazy by lazy { networkModule.createOkHttpClientLazy() }
    private val permissionManager by lazy { fileModule.getPermissionManager() }
    private val ramStatsManager by lazy { RamStatsModule(context).createRamStatsManager() }
    private val screenManager by lazy { screenModule.createScreenManager() }

    private val themeManager by lazy { ThemeModule(context).createThemeManager() }

    private val authenticationManager by lazy { AuthenticationModule(context).createAuthenticationManager() }

    private val toastManager by lazy { ToastModule(context).createToastManager() }
    private val versionManager by lazy { VersionModule(context).createVersionManager() }

    private fun createPermissionRequestAddOn() = object : PermissionRequestAddOn {
        override fun requestStoragePermission() {
            screenManager.startPermission()
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var graph: ApplicationGraph? = null

        fun getAudioManager() = graph!!.audioManager
        fun getAudioQueueManager() = graph!!.audioQueueManager
        fun getDialogManager() = graph!!.dialogManager
        fun getFileManager() = graph!!.fileManager
        fun getFileModule() = graph!!.fileModule
        fun getFileChildrenManager() = graph!!.fileChildrenManager
        fun getFileOpenManager() = graph!!.fileOpenManager
        fun getFileDeleteManager() = graph!!.fileDeleteManager
        fun getFileCopyCutManager() = graph!!.fileCopyCutManager

        fun getFileZipManager() = graph!!.fileZipManager

        fun getFileCreatorManager() = graph!!.fileCreatorManager

        fun getFilePathRootManager() = graph!!.filePathManager
        fun getFileProviderRootManager() = graph!!.fileProviderRootManager
        fun getFileScopedStorageManager() = graph!!.fileScopedStorageManager
        fun getFileShareManager() = graph!!.fileShareManager
        fun getFileStorageStatsManager() = graph!!.fileStorageStatsManager
        fun getFileRenameManager() = graph!!.fileRenameManager
        fun getFileRootManager() = graph!!.fileRootManager
        fun getFileSizeManager() = graph!!.fileSizeManager
        fun getFileSortManager() = graph!!.fileSortManager
        fun getMainThreadPost() = graph!!.mainThreadPost
        fun getNetworkManager() = graph!!.networkManager
        fun getPermissionManager() = graph!!.permissionManager
        fun getHashManager() = graph!!.hashManager
        fun getNoteManager() = graph!!.noteManager
        fun getNotificationAudioManager() = graph!!.notificationAudioManager

        fun getRamStatsManager() = graph!!.ramStatsManager
        fun getScreenManager() = graph!!.screenManager
        fun getThemeManager() = graph!!.themeManager
        fun getToastManager() = graph!!.toastManager
        fun getAuthenticationManager() = graph!!.authenticationManager
        fun getVersionManager() = graph!!.versionManager

        fun init(context: Context) {
            if (graph == null) {
                val applicationContext = context.applicationContext
                graph = ApplicationGraph(
                    applicationContext
                )
            }
        }

    }
}
