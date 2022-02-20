package com.alimoradi.file_api_online_android

import com.alimoradi.file_api.FileShareManager

internal class FileOnlineShareManagerAndroid : FileShareManager {

    override fun share(
        path: String
    ) {
        // Nothing here
    }

    override fun isShareSupported(
        path: String
    ) = false
}
