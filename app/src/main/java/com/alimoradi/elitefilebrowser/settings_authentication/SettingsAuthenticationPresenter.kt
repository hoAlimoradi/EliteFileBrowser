package com.alimoradi.elitefilebrowser.settings_authentication

import android.util.Log
import com.alimoradi.elitefilebrowser.authentication.AuthenticationManager
import com.alimoradi.elitefilebrowser.theme.Theme
import com.alimoradi.elitefilebrowser.theme.ThemeManager

class SettingsAuthenticationPresenter(
    private val screen: SettingsAuthenticationContract.Screen,
    private val authenticationManager: AuthenticationManager,
    private val themeManager: ThemeManager
) : SettingsAuthenticationContract.UserAction {

    private val authenticationListener = createAuthenticationListener()
    private val themeListener = createThemeListener()
    override fun onAttached() {
        authenticationManager.registerAuthenticationListener(authenticationListener)
        themeManager.registerThemeListener(themeListener)
        updateTheme()
        update()
    }

    override fun onDetached() {
        authenticationManager.unregisterAuthenticationListener(authenticationListener)
        themeManager.unregisterThemeListener(themeListener)
    }

    override fun onAuthenticationCheckBoxCheckedChanged(isChecked: Boolean) {
        authenticationManager.setAuthenticationEnable(isChecked)
    }

    private fun update(authenticationMode: Boolean = authenticationManager.isAuthenticationEnable()) {
        //screen.showDialog(authenticationMode)
        Log.e("", " should be show toast$authenticationMode")
    }

    private fun createAuthenticationListener() = object : AuthenticationManager.AuthenticationListener {

        override fun onAuthenticationChanged() {
            update()
        }
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
