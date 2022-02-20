package com.alimoradi.elitefilebrowser.audio

interface AudioQueueManager {

    fun next(path: String): String

    fun previous(path: String): String
}
