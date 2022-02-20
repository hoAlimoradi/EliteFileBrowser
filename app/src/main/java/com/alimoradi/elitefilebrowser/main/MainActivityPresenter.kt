package com.alimoradi.elitefilebrowser.main

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.alimoradi.elitefilebrowser.addAccount.Convertor
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocol
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocolAsJsonData
import com.alimoradi.elitefilebrowser.data.AppPreferences
import com.alimoradi.elitefilebrowser.file_provider.FileProvider
import com.alimoradi.elitefilebrowser.file_provider_root.FileProviderRootManager
import com.alimoradi.elitefilebrowser.screen.ScreenManager
import com.alimoradi.elitefilebrowser.theme.ThemeManager
import com.alimoradi.elitefilebrowser.toast.ToastManager
import com.alimoradi.file_api.FileCopyCutManager
import com.alimoradi.file_api.FileCreatorManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

internal class MainActivityPresenter(
    private val screen: MainActivityContract.Screen,
    private val fileCreatorManager: FileCreatorManager,
    private val fileCopyCutManager: FileCopyCutManager,
    private val mainActivityFileUiStorage: MainActivityFileUiStorage,
    private val mainActivitySectionStorage: MainActivitySectionStorage,
    private val screenManager: ScreenManager,
    private val themeManager: ThemeManager,
    private val toastManager: ToastManager,
    private val fileProviderRootManager: FileProviderRootManager
) : MainActivityContract.UserAction {

    private var currentPath: String? = null
    private var selectedSection: Section = Section.UNDEFINED
    private val themeListener = createThemeListener()
    private val fileToPasteChangedListener = createFileToPasteChangedListener()

    override fun onCreate() {
        themeManager.registerThemeListener(themeListener)
        fileCopyCutManager.registerFileToPasteChangedListener(fileToPasteChangedListener)
        syncWithCurrentTheme()
        syncToolbarPasteIconVisibility()
    }

    override fun onDestroy() {
        themeManager.unregisterThemeListener(themeListener)
        fileCopyCutManager.unregisterFileToPasteChangedListener(fileToPasteChangedListener)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            when (mainActivitySectionStorage.getSection()) {
                MainActivitySectionStorage.Companion.Section.UNDEFINED -> selectFile()
                MainActivitySectionStorage.Companion.Section.FILE -> selectFile()
                MainActivitySectionStorage.Companion.Section.NOTE -> selectNote()
                MainActivitySectionStorage.Companion.Section.SETTINGS -> selectSettings()
            }
            return
        }
        val section = savedInstanceState.getInt("section", Section.UNDEFINED.value)
        when (section) {
            Section.FILE_LIST.value -> selectFileList()
            Section.FILE_COLUMN.value -> selectFileColumn()
            Section.NOTE.value -> selectNote()
            Section.SETTINGS.value -> selectSettings()
            else -> selectFileColumn()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState == null) {
            return
        }
        outState.putInt("section", selectedSection.value)
    }

    override fun onFileSectionClicked() {
        selectFile()
    }

    override fun onNoteSectionClicked() {
        selectNote()
    }

    override fun onSettingsSectionClicked() {
        selectSettings()
    }

    override fun onToolbarDeleteClicked() {
        screen.deleteNote()
    }

    override fun onToolbarShareClicked() {
        screen.shareNote()
    }

    override fun onToolbarAddClicked() {
        screen.showFileCreationSelection()
    }

    override fun onToolbarExportMyAccountClicked() {
        exportMyAccountSignalFile()
    }

    override fun onToolbarExportListOfTrustUserClicked() {
        exportListOfTrustUserSignalFile()

    }

    override fun onToolbarSearchClicked() {

        //todo
    }

    override fun onToolbarFileColumnClicked() {
        selectFileColumn()
    }

    override fun onToolbarFileListClicked() {
        selectFileList()
    }

    private fun exportMyAccountSignalFile() {

        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
        var isPresent = true
        if (!docsFolder.exists()) {
            isPresent = docsFolder.mkdir()
        }
        if (isPresent) {
            val file = File(docsFolder.absolutePath, "test.txt")
        } else {
            // Failure
        }

        var destinationPath = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
            destinationPath = docsFolder.absolutePath
        } else {
            destinationPath = getRootPath()
        }
        val myAccountSignal = readMySignalData()
        var fileName: String = ""
        myAccountSignal.name?.let { //esc -> elite signal config
            fileName = it+ "signal"+ ".esc"
        }


       /* val parentPath = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }*/

        AppPreferences.getMySignalProtocolData()?.let {
            val gson = Gson()
            val json = gson.toJson(it)
            Log.e("jafar", " myAccountSignalExportFile   " + json)
            fileCreatorManager.createWithStringContent(destinationPath, fileName, json)
            Log.e("jafar", " myAccountSignalExportFile currentPath " + getRootPath())
            toastManager.toast(" فایل خروجی اماده شد ")
        }

    }

    private fun exportListOfTrustUserSignalFile() {
        val uuid = UUID.randomUUID().toString()
        val fileName =  uuid + "listOfTrustUsersSignalFile"+ ".esc"

        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
        var isPresent = true
        if (!docsFolder.exists()) {
            isPresent = docsFolder.mkdir()
        }
        if (isPresent) {
            val file = File(docsFolder.absolutePath, "test.txt")
        } else {
            // Failure
        }

        var destinationPath = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
            destinationPath = docsFolder.absolutePath
        } else {
            destinationPath = getRootPath()
        }

        /*val parentPath = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }*/

        AppPreferences.getUserSignalProtocolDataList()?.let {
            val gson = Gson()
            val json = gson.toJson(it)
            Log.e("jafar", " exportListOfTrustUserSignalFile   " + json)
            fileCreatorManager.createWithStringContent(destinationPath, fileName, json)
            Log.e("jafar", " exportListOfTrustUserSignalFile currentPath " + getRootPath())
            toastManager.toast(" فایل خروجی اماده شد ")
        }

    }



    private fun readMySignalData(): PersonSignalProtocol {
        var mySignalDataProtocolAsListWithOnlyOneElement = ArrayList<PersonSignalProtocolAsJsonData>()

        AppPreferences.getMySignalProtocolData()?.let {
            mySignalDataProtocolAsListWithOnlyOneElement = it

        }
        val result = PersonSignalProtocol().createNewPerson()
        val gson = Gson()
        val myPersonSignalProtocolAsJsonData = mySignalDataProtocolAsListWithOnlyOneElement.first()

        /*val myPersonSignalProtocolAsJsonData =
            gson.fromJson(mySignalDataJson, PersonSignalProtocolAsJsonData::class.java)*/

        result.name = myPersonSignalProtocolAsJsonData.name
        result.deviceId = myPersonSignalProtocolAsJsonData.deviceId
        result.signedPreKeyId = myPersonSignalProtocolAsJsonData.signedPreKeyId
        result.registrationId = myPersonSignalProtocolAsJsonData.registrationId

        val identityKeyPairByteArrayFromString: ByteArray =
            Convertor().stringToByteArray(myPersonSignalProtocolAsJsonData.identityKeyPair)
        val identityKeyPairFromByteArray = IdentityKeyPair(identityKeyPairByteArrayFromString)

        result.identityKeyPair = identityKeyPairFromByteArray

        val preKeysListOfPreKeyRecord: ArrayList<PreKeyRecord> = ArrayList()
        val preKeysListOfString: ArrayList<String> = gson.fromJson(
            myPersonSignalProtocolAsJsonData.preKeys,
            object : TypeToken<List<String>>() {}.getType()
        )

        for (counter in 0 until 100) {
            preKeysListOfPreKeyRecord.add(
                PreKeyRecord(
                    Convertor().stringToByteArray(
                        preKeysListOfString[counter]
                    )
                )
            )
        }

        result.preKeys = preKeysListOfPreKeyRecord

        val signedPreKeyFromString =
            SignedPreKeyRecord(Convertor().stringToByteArray(myPersonSignalProtocolAsJsonData.signedPreKey))
        result.signedPreKey = signedPreKeyFromString

        val signalProtocolAddress: SignalProtocolAddress = gson.fromJson(
            myPersonSignalProtocolAsJsonData.address,
            SignalProtocolAddress::class.java
        )
        result.address = signalProtocolAddress

        return result
    }


    override fun onToolbarFilePasteClicked() {
        val path = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }
        fileCopyCutManager.paste(path!!)
    }

    override fun onFileCreationConfirmed(fileName: String) {
        val parentPath = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }
        fileCreatorManager.create(parentPath!!, fileName)
        Log.e("jafar", " currentPath " + currentPath)
    }

    override fun onSelectedFilePathChanged(path: String?) {
        currentPath = path
    }

    private fun selectFile() {
        val currentFileUi = mainActivityFileUiStorage.getCurrentFileUi()
        when (currentFileUi) {
            MainActivityFileUiStorage.SECTION_FILE_LIST -> selectFileList()
            MainActivityFileUiStorage.SECTION_FILE_COLUMN -> selectFileColumn()
            else -> throw IllegalStateException("Unsupported currentFileUi: $currentFileUi")
        }
    }

    private fun selectFileList() {
        fileCopyCutManager.cancelCopyCut()
        mainActivityFileUiStorage.setCurrentFileUi(MainActivityFileUiStorage.SECTION_FILE_LIST)
        selectedSection = Section.FILE_LIST
        currentPath = screen.getFileListCurrentPath()
        screen.showFileListView()
        screen.hideFileColumnView()
        screen.hideNoteView()
        screen.hideSettingsView()
        screen.hideToolbarDelete()
        screen.hideToolbarShare()
        screen.showToolbarAdd()
        syncToolbarSearchVisibility()
        screen.showToolbarFileColumn()
        screen.hideToolbarFileList()
        screen.selectBottomBarFile()
        syncToolbarPasteIconVisibility()
        mainActivitySectionStorage.putSection(MainActivitySectionStorage.Companion.Section.FILE)
    }

    private fun selectFileColumn() {
        fileCopyCutManager.cancelCopyCut()
        mainActivityFileUiStorage.setCurrentFileUi(MainActivityFileUiStorage.SECTION_FILE_COLUMN)
        selectedSection = Section.FILE_COLUMN
        currentPath = screen.getFileListCurrentPath()
        screen.hideFileListView()
        screen.showFileColumnView()
        screen.hideNoteView()
        screen.hideSettingsView()
        screen.hideToolbarDelete()
        screen.hideToolbarShare()
        screen.showToolbarAdd()
        syncToolbarSearchVisibility()
        screen.hideToolbarFileColumn()
        screen.showToolbarFileList()
        screen.selectBottomBarFile()
        syncToolbarPasteIconVisibility()
        mainActivitySectionStorage.putSection(MainActivitySectionStorage.Companion.Section.FILE)
    }


    private fun selectNote() {
        selectedSection = Section.NOTE
        screen.hideFileListView()
        screen.hideFileColumnView()
        screen.showNoteView()
        screen.hideSettingsView()
        screen.showToolbarDelete()
        screen.showToolbarShare()
        screen.hideToolbarAdd()
        syncToolbarSearchVisibility()
        screen.hideToolbarFileColumn()
        screen.hideToolbarFileList()
        screen.selectBottomBarNote()
        syncToolbarPasteIconVisibility()
        mainActivitySectionStorage.putSection(MainActivitySectionStorage.Companion.Section.NOTE)
    }

    private fun selectSettings() {
        selectedSection = Section.SETTINGS
        screen.hideFileListView()
        screen.hideFileColumnView()
        screen.hideNoteView()
        screen.showSettingsView()
        screen.hideToolbarDelete()
        screen.hideToolbarShare()
        screen.hideToolbarAdd()
        syncToolbarSearchVisibility()
        screen.hideToolbarFileColumn()
        screen.hideToolbarFileList()
        screen.selectBottomBarSettings()
        syncToolbarPasteIconVisibility()
        mainActivitySectionStorage.putSection(MainActivitySectionStorage.Companion.Section.SETTINGS)
    }

    private fun syncWithCurrentTheme() {
        val theme = themeManager.getTheme()
        screen.setWindowBackgroundColorRes(theme.windowBackgroundColorRes)
        screen.setBottomBarBlurOverlayColorRes(theme.bottomBarBlurOverlay)
    }

    private fun syncToolbarPasteIconVisibility() {
        if (selectedSection != Section.FILE_LIST &&
            selectedSection != Section.FILE_COLUMN
        ) {
            screen.setPasteIconVisibility(false)
            return
        }
        val fileToPastePath = fileCopyCutManager.getFileToPastePath()
        screen.setPasteIconVisibility(fileToPastePath != null)
    }

    private fun getRootPath(): String {
        return fileProviderRootManager.getFileRootPath(FileProvider.Local)
    }

    private fun getDocumentsFolderAbsolutePath(): String {
        return  Environment.getExternalStorageDirectory().toString() + "/Documents"
    }

    private fun syncToolbarSearchVisibility() {

    }

    private fun createThemeListener() = object : ThemeManager.ThemeListener {
        override fun onThemeChanged() {
            syncWithCurrentTheme()
        }
    }

    private fun createFileToPasteChangedListener() = object : FileCopyCutManager.FileToPasteChangedListener {
        override fun onFileToPasteChanged() {
            syncToolbarPasteIconVisibility()
        }
    }

    enum class Section(val value: Int) {
        UNDEFINED(1),
        FILE_LIST(2),
        FILE_COLUMN(3),
        NOTE(4),
        SETTINGS(5)
    }
}
