package com.alimoradi.elitefilebrowser.authentication

import android.content.Context

class AuthenticationModule(
    private val context: Context
) {

    fun createAuthenticationManager(): AuthenticationManager {
        val sharedPreferences = context.getSharedPreferences(
            AuthenticationManagerImpl.PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return AuthenticationManagerImpl(sharedPreferences)
    }
}