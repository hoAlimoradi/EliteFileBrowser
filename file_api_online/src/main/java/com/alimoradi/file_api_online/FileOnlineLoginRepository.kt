package com.alimoradi.file_api_online

interface FileOnlineLoginRepository {

    fun save(key: String, value: String)

    fun load(key: String): String?
}
