package com.alimoradi.elitefilebrowser.authentication
  
import android.content.SharedPreferences

class AuthenticationManagerImpl(
    private val sharedPreferences: SharedPreferences
) : AuthenticationManager {

    private var currentAuthenticationIndex = 0
    private var listeners = ArrayList<AuthenticationManager.AuthenticationListener>()

    init {
        currentAuthenticationIndex = sharedPreferences.getInt("Authentication", 0)
    }

    override fun registerAuthenticationListener(listener: AuthenticationManager.AuthenticationListener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun unregisterAuthenticationListener(listener: AuthenticationManager.AuthenticationListener) {
        listeners.remove(listener)
    }

    companion object {
        @JvmStatic
        val PREFERENCE_NAME = "AuthenticationManager"
    }

    override fun setAuthenticationEnable(enable: Boolean) {
        currentAuthenticationIndex = if (enable) 1 else 0
        sharedPreferences.edit().putInt("Authentication", currentAuthenticationIndex).apply()
        for (listener in listeners) {
            listener.onAuthenticationChanged()
        }
    }

    override fun isAuthenticationEnable(): Boolean = if (currentAuthenticationIndex == 0) false else true


}