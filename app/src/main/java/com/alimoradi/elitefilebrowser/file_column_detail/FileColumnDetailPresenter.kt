@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_column_detail

import android.annotation.SuppressLint
import android.util.Log
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocolAsJsonData
import com.alimoradi.elitefilebrowser.audio.AudioManager
import com.alimoradi.elitefilebrowser.audio.AudioQueueManager
import com.alimoradi.elitefilebrowser.data.AppPreferences
import com.alimoradi.elitefilebrowser.theme.ThemeManager
import com.alimoradi.elitefilebrowser.toast.ToastManager
import com.alimoradi.file_api.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class FileColumnDetailPresenter(
    private val screen: FileColumnDetailContract.Screen,
    private val audioManager: AudioManager,
    private val audioQueueManager: AudioQueueManager,
    private val fileOpenManager: FileOpenManager,
    private val fileDeleteManager: FileDeleteManager,
    private val fileCopyCutManager: FileCopyCutManager,
    private val fileZipManager: FileZipManager,
    private val fileRenameManager: FileRenameManager,
    private val fileShareManager: FileShareManager,
    private val themeManager: ThemeManager,
    private val toastManager: ToastManager,
    private val playDrawableRes: Int,
    private val pauseDrawableRes: Int,
    private val deleteFailedTextRes: Int
) : FileColumnDetailContract.UserAction {

    private val playListener = createPlayListener()
    private val themeListener = createThemeListener()
    private val fileDeleteCompletionListener = createFileDeleteCompletionListener()

    private val fileZipListener = createFileZipListener()
    private var file: File? = null

    override fun onAttached() {
        audioManager.registerPlayListener(playListener)
        synchronizePlayButton()
        themeManager.registerThemeListener(themeListener)
        syncWithCurrentTheme()
        fileDeleteManager.registerFileDeleteCompletionListener(fileDeleteCompletionListener)
        fileZipManager.registerFileZipListener(fileZipListener)

    }

    override fun onDetached() {
        audioManager.unregisterPlayListener(playListener)
        themeManager.unregisterThemeListener(themeListener)
        fileDeleteManager.unregisterFileDeleteCompletionListener(fileDeleteCompletionListener)
        fileZipManager.unregisterFileZipListener(fileZipListener)
    }

    override fun onFileChanged(file: File?) {
        this.file = file
        if (file == null) {
            screen.setTitle("")
            screen.setPath("")
            screen.setLastModified("")
            screen.hidePlayPauseButton()
            return
        }
        val (_, path, _, directory, name, length, lastModified) = file
        if (directory) {
            throw IllegalStateException("Directory not supported for now")
        }
        screen.setTitle(name)
        screen.setPath(path)
        screen.setLength(humanReadableByteCount(length))
        screen.setLastModified(Date(lastModified).toString())
        if (audioManager.isSupportedPath(path)) {
            screen.showPlayPauseButton()
            screen.showNextButton()
            screen.showPreviousButton()
        } else {
            screen.hidePlayPauseButton()
            screen.hideNextButton()
            screen.hidePreviousButton()
        }

        if (signalConfigIsSupportedPath(path)) {
            screen.showTrustButton()
        } else {
            screen.hideTrustButton()
        }

        if (zipSupportedPath(path)) {
            screen.showUnZipButton()
            screen.hideZipButton()
        } else {
            screen.showZipButton()
            screen.hideUnZipButton()
        }

        synchronizePlayButton()
    }

    private fun signalConfigIsSupportedPath(path: String): Boolean {
        return try {
            val extension = path.substring(path.lastIndexOf("."))
            extension == ".esc"
        }catch (e: Exception) {
            false
        }

    // return SignalFileExtension.SIGNAL_CONFIG.isCompliant(path)
    }


    private fun zipSupportedPath(path: String): Boolean {
        return fileZipManager.isZip(path)
    }
  /*  private fun zipSupportedPath(path: String): Boolean {
        val extension = path.substring(path.lastIndexOf("."))
        return (extension == ".zip")
    }*/

    override fun onOpenClicked() {
        fileOpenManager.open(file!!.path)
    }

    override fun onOpenAsClicked() {
        screen.showOpenAsSelection()
    }

    override fun onOpenAsConfirmedClicked(typeMime: String) {
        fileOpenManager.open(file!!.path, typeMime)
    }

    override fun onPlayPauseClicked() {
        if (audioManager.getSourcePath() != file!!.path) {
            audioManager.reset()
            audioManager.setSourcePath(file!!.path)
            audioManager.prepareAsync()
            return
        }
        val playing = audioManager.isPlaying()
        if (playing) {
            audioManager.pause()
        } else {
            audioManager.play()
        }
    }

    override fun onNextClicked() {
        val nextPath = audioQueueManager.next(file!!.path)
        audioManager.reset()
        audioManager.setSourcePath(nextPath)
        audioManager.prepareAsync()
    }

    override fun onPreviousClicked() {
        val previousPath = audioQueueManager.previous(file!!.path)
        audioManager.reset()
        audioManager.setSourcePath(previousPath)
        audioManager.prepareAsync()
    }

    override fun onTrustClicked() {
        //todo chech if
        //اکه روی فایل خودش زد نباید اضافه کرد


        val ioFile = java.io.File(file!!.path)
        val usersJson = ioFile.inputStream().readBytes().toString(Charsets.UTF_8)
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<PersonSignalProtocolAsJsonData>>() {}.type
        val list: ArrayList<PersonSignalProtocolAsJsonData>? = gson.fromJson(usersJson, type)
        Log.e("jafar ", usersJson)
        list?.let { newList ->
            val mutableList: MutableList<PersonSignalProtocolAsJsonData> = ArrayList()
            AppPreferences.getUserSignalProtocolDataList()?.let {
                mutableList.addAll(it)

            }
            mutableList.addAll(newList)
            AppPreferences.setUserSignalProtocolDataList(mutableList)
        }

        AppPreferences.getUserSignalProtocolDataList()?.let {
            Log.e("jafar ", it.size.toString())
        }

    }

    override fun onZipClicked() {

        //fileZipManager.copy()
    }

    override fun onUnZipClicked() {
        Log.e("jafar unzip onUnZipClicked", file?.path + "")
        file?.let {
            val parentPath: String = java.io.File(it.path).absoluteFile.parent
            fileZipManager.unzip(it.path, parentPath)
        }
    }


    override fun onDeleteClicked() {
        screen.showDeleteConfirmation(file!!.name)
    }

    override fun onDeleteConfirmedClicked() {
        fileDeleteManager.delete(file!!.path)
    }

    override fun onRenameClicked() {
        screen.showRenamePrompt(file!!.name)
    }

    override fun onEncryptionClicked() {
        file?.let {
            val extension = it.path.substring(it.path.lastIndexOf("."))
            screen.showEncryptionSignalView(it.path, extension)
        }
    }

    override fun onRenameConfirmedClicked(fileName: String) {
        if (fileName.contains("/")) {
            toastManager.toast("File name should not contain /")
            return
        }
        fileRenameManager.rename(file!!.path, fileName)
    }

    override fun onShareClicked() {
        fileShareManager.share(file!!.path)
    }

    override fun onCopyClicked() {
        fileCopyCutManager.copy(file!!.path)
    }

    override fun onCutClicked() {
        fileCopyCutManager.cut(file!!.path)
    }

    private fun synchronizePlayButton() {
        if (file == null) {
            return
        }
        val sourcePath = audioManager.getSourcePath()
        if (file!!.path != sourcePath) {
            screen.setPlayPauseButtonImage(playDrawableRes)
            return
        }
        val playing = audioManager.isPlaying()
        screen.setPlayPauseButtonImage(if (playing) pauseDrawableRes else playDrawableRes)
    }

    private fun createPlayListener() = object : AudioManager.PlayListener {
        override fun onPlayPauseChanged() {
            synchronizePlayButton()
        }
    }

    private fun syncWithCurrentTheme() {
        val theme = themeManager.getTheme()
        screen.setTextPrimaryColorRes(theme.textPrimaryColorRes)
        screen.setFileColumnDetailBackgroundColorRes(theme.fileColumnDetailBackgroundColorRes)
    }

    private fun createThemeListener() = object : ThemeManager.ThemeListener {
        override fun onThemeChanged() {
            syncWithCurrentTheme()
        }
    }

    private fun createFileDeleteCompletionListener() = object : FileDeleteManager.FileDeleteCompletionListener {
        override fun onFileDeletedCompleted(
            path: String,
            succeeded: Boolean
        ) {
            if (!succeeded) {
                screen.showToast(deleteFailedTextRes)
            }
        }
    }

    private fun createFileZipListener() = object : FileZipManager.FileZipListener {
        override fun onUnzipEnded(path: String, outputPath: String) {

        }

    }

    companion object {

        fun humanReadableByteCount(bytes: Long): String {
            return humanReadableByteCount(bytes, true)
        }

        @SuppressLint("DefaultLocale")
        private fun humanReadableByteCount(bytes: Long, si: Boolean): String {
            val unit = if (si) 1000 else 1024
            if (bytes < unit) {
                return "$bytes B"
            }
            val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
            val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
            return String.format("%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
        }
    }
}
