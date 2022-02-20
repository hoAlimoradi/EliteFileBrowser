package com.alimoradi.elitefilebrowser.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.alimoradi.elitefilebrowser.file_provider.FileProvider
import com.alimoradi.elitefilebrowser.file_details.FileDetailsActivity
import com.alimoradi.elitefilebrowser.permission.PermissionActivity

class ScreenManagerImpl(
    private val context: Context
) : ScreenManager {

    override fun startFileDetails(
        path: String,
        fileProvider: FileProvider
    ) {
        FileDetailsActivity.start(
            context,
            path,
            fileProvider
        )
    }

    override fun startPermission() {
        PermissionActivity.start(context)
    }

    override fun startSystemSettingsStorage() {
        val intent = Intent(Settings.ACTION_MEMORY_CARD_SETTINGS)
        if (context !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(intent)
    }

    override fun startSearch() {
        val intent = Intent()
        if (context !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        intent.setClassName(
            "com.alimoradi.elitefilebrowser",
            "com.alimoradi.feature_search_dynamic.activity.SearchActivity"
        ).also { context.startActivity(it) }
    }
}
