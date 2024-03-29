@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.settings_ram

import com.alimoradi.elitefilebrowser.ram_stats.RamStatsManager
import com.alimoradi.elitefilebrowser.screen.ScreenManager
import com.alimoradi.elitefilebrowser.theme.Theme
import com.alimoradi.elitefilebrowser.theme.ThemeManager
import com.alimoradi.file_api.FileSizeUtils

class SettingsRamPresenter(
    private val screen: SettingsRamContract.Screen,
    private val ramStatsManager: RamStatsManager,
    private val screenManager: ScreenManager,
    private val themeManager: ThemeManager
) : SettingsRamContract.UserAction {

    private val themeListener = createThemeListener()

    override fun onAttached() {
        themeManager.registerThemeListener(themeListener)
        updateTheme()
        updateScreen()
    }

    override fun onDetached() {
        themeManager.unregisterThemeListener(themeListener)
    }

    override fun onStorageLocalRowClicked() {
        updateScreen()
    }

    private fun updateScreen() {
        val freeMemory = ramStatsManager.getFreeMemory()
        val busyMemory = ramStatsManager.getBusyMemory()
        val totalMemory = ramStatsManager.getTotalMemory()
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
