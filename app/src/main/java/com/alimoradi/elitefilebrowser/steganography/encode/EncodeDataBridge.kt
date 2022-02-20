package com.alimoradi.elitefilebrowser.steganography.encode

import android.graphics.Bitmap
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.SteganoData

/**
 * A singleton holding the information of what to hide. Due to Android's intent limit, we can't pass the bitmap and steganoData in the bundle.
 */
class EncodeDataBridge {

    var bitmap: Bitmap? = null
    var steganoData: SteganoData? = null

    /**
     * Clears the data when it is not needed anymore to free resources
     */
    fun clear() {
        bitmap = null
        steganoData = null
    }
}