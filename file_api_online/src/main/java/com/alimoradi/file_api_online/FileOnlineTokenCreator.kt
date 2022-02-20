package com.alimoradi.file_api_online

interface FileOnlineTokenCreator {

    fun createToken(login: String, passwordSha1: String): String

    fun createTokens(login: String, passwordSha1: String, minutesRange: Int): List<String>
}
