package com.alimoradi.file_api_online_android

import android.content.SharedPreferences
import com.alimoradi.file_api_online.FileOnlineLoginRepository

internal class FileOnlineLoginRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : FileOnlineLoginRepository {

    override fun save(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun load(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    companion object {
        @JvmStatic
        val PREFERENCE_NAME = "FileOnlineLoginRepository"
    }
}
