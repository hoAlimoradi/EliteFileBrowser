package com.alimoradi.file_api_online

interface FileOnlineLoginManager {

    fun setLogin(login: String)

    fun setPassword(password: String)

    fun getLogin(): String?

    fun isLogged(): Boolean

    fun createToken(): String

    fun registerLoginListener(listener: LoginListener)

    fun unregisterLoginListener(listener: LoginListener)

    interface LoginListener {

        fun onOnlineLogChanged()
    }
}
