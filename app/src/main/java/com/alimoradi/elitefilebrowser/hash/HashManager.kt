package com.alimoradi.elitefilebrowser.hash

interface HashManager {

    fun sha256(text: String, time: Int): String?
}
