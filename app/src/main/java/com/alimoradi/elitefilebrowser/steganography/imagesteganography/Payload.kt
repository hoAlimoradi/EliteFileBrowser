package com.alimoradi.elitefilebrowser.steganography.imagesteganography

interface Payload {

    fun getType(): Type

    fun getLengthInBytes(): Int

    fun getBytes(): ByteArray
}

