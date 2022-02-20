package com.alimoradi.elitefilebrowser.authentication

import com.alimoradi.elitefilebrowser.theme.Theme

interface AuthenticationManager {

    fun setAuthenticationEnable(enable: Boolean)

    fun isAuthenticationEnable(): Boolean

    fun registerAuthenticationListener(listener: AuthenticationListener)

    fun unregisterAuthenticationListener(listener: AuthenticationListener)

    interface AuthenticationListener {
        fun onAuthenticationChanged()
    }
}