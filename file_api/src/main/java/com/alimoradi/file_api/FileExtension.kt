package com.alimoradi.file_api

enum class FileExtension(
    private val extension: String
) {

    MP3("mp3"),
    OGG("ogg"),
    WAV("wav"),
    WMA("wma"),
    ZIP("zip");

    fun isCompliant(path: String): Boolean {
        return path.toLowerCase().endsWith(".$extension")
    }
}


enum class SignalFileExtension(
    private val extension: String
) {

    SIGNAL_CONFIG(".esc"),
    SIGNAL_ENC("esenc");

    fun isCompliant(path: String): Boolean {
        return path.toLowerCase().endsWith(".$extension")
    }
}