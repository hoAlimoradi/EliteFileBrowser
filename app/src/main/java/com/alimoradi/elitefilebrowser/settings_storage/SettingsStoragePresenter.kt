@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.settings_storage

import com.alimoradi.elitefilebrowser.file_storage_stats.FileStorageStatsManager
import com.alimoradi.elitefilebrowser.screen.ScreenManager
import com.alimoradi.elitefilebrowser.theme.Theme
import com.alimoradi.elitefilebrowser.theme.ThemeManager
import com.alimoradi.file_api.FileSizeUtils

class SettingsStoragePresenter(
    private val screen: SettingsStorageContract.Screen,
    private val fileStorageStatsManager: FileStorageStatsManager,
    private val screenManager: ScreenManager,
    private val themeManager: ThemeManager
) : SettingsStorageContract.UserAction {

    private val themeListener = createThemeListener()

    override fun onAttached() {
        themeManager.registerThemeListener(themeListener)
        updateTheme()
        updateStorage()
    }

    override fun onDetached() {
        themeManager.unregisterThemeListener(themeListener)
    }

    override fun onStorageLocalRowClicked() {
        screenManager.startSystemSettingsStorage()
    }

    private fun updateStorage() {
        val freeMemory = fileStorageStatsManager.getFreeMemory()
        val busyMemory = fileStorageStatsManager.getBusyMemory()
        val totalMemory = fileStorageStatsManager.getTotalMemory()
        val busyPercent = ((busyMemory.toFloat() * 100f) / totalMemory).toInt()
        val freeMemoryString = FileSizeUtils.humanReadableByteCount(freeMemory)
        val busyMemoryString = FileSizeUtils.humanReadableByteCount(busyMemory)
        val totalMemoryString = FileSizeUtils.humanReadableByteCount(totalMemory)
        screen.setLocalSubLabelText("$busyPercent% used - $freeMemoryString free")
        screen.setLocalBusy("$busyMemoryString busy")
        screen.setLocalTotal("$totalMemoryString total")
        screen.setProgress(busyMemory.toFloat() / totalMemory.toFloat())
    }

    private fun updateTheme(theme: Theme = themeManager.getTheme()) {
        screen.setTextPrimaryColorRes(theme.textPrimaryColorRes)
        screen.setTextSecondaryColorRes(theme.textSecondaryColorRes)
        screen.setSectionColor(theme.cardBackgroundColorRes)
    }

    private fun createThemeListener() = object : ThemeManager.ThemeListener {
        override fun onThemeChanged() {
            updateTheme()
        }
    }
}
