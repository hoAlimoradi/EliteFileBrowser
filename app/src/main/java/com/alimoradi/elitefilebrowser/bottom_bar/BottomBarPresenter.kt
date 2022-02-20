@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.bottom_bar

import android.os.Bundle
import androidx.annotation.ColorRes
import com.alimoradi.elitefilebrowser.theme.ThemeManager
import com.alimoradi.file_api_online.FileOnlineLoginManager
import java.lang.IllegalStateException

class BottomBarPresenter(
    private val screen: BottomBarContract.Screen,
    private val themeManager: ThemeManager,
    @ColorRes private val selectedColorRes: Int,
    @ColorRes private val notSelectedColorRes: Int
) : BottomBarContract.UserAction {

    private var selectedSection = Section.UNDEFINED
    private val themeListener = createThemeListener()

    init {
        selectFile()
    }

    override fun onAttached() {
        themeManager.registerThemeListener(themeListener)
        syncWithCurrentTheme()
    }

    override fun onDetached() {
        themeManager.unregisterThemeListener(themeListener)

    }

    override fun onSaveInstanceState(saveState: Bundle) {
        saveState.putInt("BUNDLE_KEY_SECTION", selectedSection.sectionId)
    }

    override fun onRestoreInstanceState(state: Bundle) {
        val sectionId = state.getInt("BUNDLE_KEY_SECTION")
        val section = getSection(sectionId)
        when (section) {
            Section.FILE -> selectFile()
            Section.NOTE -> selectNote()
            Section.SETTINGS -> selectSettings()
            else -> selectFile()
        }
    }

    override fun onFileClicked() {
        selectFile()
        screen.notifyListenerFileClicked()
    }

    override fun onNoteClicked() {
        selectNote()
        screen.notifyListenerNoteClicked()
    }

    override fun onSettingsClicked() {
        selectSettings()
        screen.notifyListenerSettingsClicked()
    }

    override fun onSelectFile() {
        selectFile()
    }

    override fun onSelectNote() {
        selectNote()
    }

    override fun onSelectSettings() {
        selectSettings()
    }

    private fun selectFile() {
        selectedSection = Section.FILE
        syncTexts()
        syncIconsWithSelectedSection()
    }

    private fun selectNote() {
        selectedSection = Section.NOTE
        syncTexts()
        syncIconsWithSelectedSection()
    }

    private fun selectSettings() {
        selectedSection = Section.SETTINGS
        syncTexts()
        syncIconsWithSelectedSection()
    }

    private fun syncWithCurrentTheme() {
        syncTexts()
    }

    private fun syncTexts() {
        val theme = themeManager.getTheme()
        when (selectedSection) {
            Section.UNDEFINED -> throw IllegalStateException("Section should be affected")
            Section.FILE -> {
                screen.setSectionFileTextColorRes(selectedColorRes)
                screen.setSectionNoteTextColorRes(theme.textPrimaryColorRes)
                screen.setSectionSettingsTextColorRes(theme.textPrimaryColorRes)
            }

            Section.NOTE -> {
                screen.setSectionFileTextColorRes(theme.textPrimaryColorRes)
                screen.setSectionNoteTextColorRes(selectedColorRes)
                screen.setSectionSettingsTextColorRes(theme.textPrimaryColorRes)
            }
            Section.SETTINGS -> {
                screen.setSectionFileTextColorRes(theme.textPrimaryColorRes)
                screen.setSectionNoteTextColorRes(theme.textPrimaryColorRes)
                screen.setSectionSettingsTextColorRes(selectedColorRes)
            }
        }
    }

    private fun syncIconsWithSelectedSection() {
        when (selectedSection) {
            Section.UNDEFINED -> throw IllegalStateException("Section should be affected")
            Section.FILE -> {
                screen.setFileIconColorRes(selectedColorRes)
                screen.setNoteIconColorRes(notSelectedColorRes)
                screen.setSettingsIconColorRes(notSelectedColorRes)
            }

            Section.NOTE -> {
                screen.setFileIconColorRes(notSelectedColorRes)
                screen.setNoteIconColorRes(selectedColorRes)
                screen.setSettingsIconColorRes(notSelectedColorRes)
            }
            Section.SETTINGS -> {
                screen.setFileIconColorRes(notSelectedColorRes)
                screen.setNoteIconColorRes(notSelectedColorRes)
                screen.setSettingsIconColorRes(selectedColorRes)
            }
        }
    }

    private fun createThemeListener() = object : ThemeManager.ThemeListener {
        override fun onThemeChanged() {
            syncWithCurrentTheme()
        }
    }

    private enum class Section(val sectionId: Int) {
        UNDEFINED(0),
        FILE(1),
        NOTE(2),
        SETTINGS(3)
    }

    companion object {

        private fun getSection(sectionId: Int) = when (sectionId) {
            0 -> Section.UNDEFINED
            1 -> Section.FILE
            2 -> Section.NOTE
            3 -> Section.SETTINGS
            else -> throw IllegalStateException("Wrong sectionId: $sectionId")
        }
    }
}
