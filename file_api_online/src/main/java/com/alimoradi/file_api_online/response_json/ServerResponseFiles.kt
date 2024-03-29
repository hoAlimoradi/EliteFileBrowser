@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.file_api_online.response_json

import com.alimoradi.file_api.File
import org.json.JSONObject

data class ServerResponseFiles private constructor(
    private val files: List<File>,
    private val serverResponse: ServerResponse
) {

    fun getFiles() = ArrayList<File>(files)

    fun toJsonString() = toJson(this).toString()

    companion object {

        @JvmStatic
        fun create(
            files: List<File>,
            debugMessage: String,
            succeeded: Boolean
        ): ServerResponseFiles {
            val content = JSONObject()
            content.put("files", File.toJson(files))
            val serverResponse = ServerResponse.create(
                content,
                debugMessage,
                succeeded
            )
            return ServerResponseFiles(
                files,
                serverResponse
            )
        }

        @JvmStatic
        fun toJson(serverResponseFiles: ServerResponseFiles): JSONObject {
            return ServerResponse.toJson(serverResponseFiles.serverResponse)
        }

        @JvmStatic
        fun fromJson(jsonObject: JSONObject): ServerResponseFiles {
            val serverResponse = ServerResponse.fromJson(jsonObject)
            val content = serverResponse.content
            val files = File.fromJson(content.getJSONArray("files"))
            return ServerResponseFiles(
                files,
                serverResponse
            )
        }
    }
}
