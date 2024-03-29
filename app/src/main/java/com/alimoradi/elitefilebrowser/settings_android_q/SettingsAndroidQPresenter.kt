@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.settings_android_q

import com.alimoradi.elitefilebrowser.screen.ScreenManager
import com.alimoradi.elitefilebrowser.theme.Theme
import com.alimoradi.elitefilebrowser.theme.ThemeManager

class SettingsAndroidQPresenter(
    private val screen: SettingsAndroidQContract.Screen,
    private val screenManager: ScreenManager,
    private val themeManager: ThemeManager
) : SettingsAndroidQContract.UserAction {

    private val themeListener = createThemeListener()

    override fun onAttached() {
        themeManager.registerThemeListener(themeListener)
        updateTheme()
        updateScreen()
    }

    override fun onDetached() {
        themeManager.unregisterThemeListener(themeListener)
    }

    override fun onRowClicked() {
        screen.startAndroidQActivity()
    }

    private fun updateScreen() {
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
