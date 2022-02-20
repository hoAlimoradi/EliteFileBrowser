package com.alimoradi.elitefilebrowser.hash

import android.content.Context

class HashModule(
    private val context: Context
) {

    fun createHashManager(): HashManager {
        return HashManagerImpl()
    }
}
