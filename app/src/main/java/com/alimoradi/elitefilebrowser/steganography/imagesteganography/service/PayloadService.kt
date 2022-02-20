package com.alimoradi.elitefilebrowser.steganography.imagesteganography.service

import android.net.Uri
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.FilePayload
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.Payload
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.PlainTextPayload

class PayloadService(private val fileService: FileService) {

    /**
     * Creates a payload based on the data the user entered.
     *
     * @param payloadPlainText the entered plain text
     * @param payloadFile the selected file
     * @return the payload object containing either the plain text or file, depending on what was passed
     */
    fun createPayload(payloadPlainText: String?, payloadFile: Uri?): Payload {
        return if (payloadPlainText != null) {
            PlainTextPayload(payloadPlainText)

        } else {
            val payloadFileBytes = fileService.getByteArray(payloadFile!!)
            val payloadFileMimeType = fileService.getFileType(payloadFile)!!
            FilePayload(payloadFileMimeType, payloadFileBytes)
        }
    }
}