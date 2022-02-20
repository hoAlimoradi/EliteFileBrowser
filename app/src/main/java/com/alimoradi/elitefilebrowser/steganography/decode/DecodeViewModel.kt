package com.alimoradi.elitefilebrowser.steganography.decode

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.service.BitmapService
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.service.FileService
import com.alimoradi.elitefilebrowser.steganography.OperationStatistics
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.*
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.service.PayloadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

class DecodeViewModel : ViewModel() {

    lateinit var bitmapService: BitmapService
    lateinit var fileService: FileService
    lateinit var payloadService: PayloadService
    lateinit var revealService: RevealService


    var outputPayload: Payload? = null
    var temporaryOutputFileUri: Uri? = null
    var statistics: OperationStatistics? = null
    var dataSizeInBytes: Long = 0

    //
    val liveDataTextRes = MutableLiveData<Int>()
    val liveDataVersion = MutableLiveData<String>()
    val liveDataTypeTextRes = MutableLiveData<Int>()
    val liveDataEncryptionTextRes = MutableLiveData<Int>()
    val liveDataInputState = MutableLiveData<Boolean>()
    val loadingMutableLiveData = MutableLiveData<Boolean>()
    val decodeErrorMutableLiveData = MutableLiveData<Boolean>()

    lateinit var steganoInfo: SteganoInfo
    var imageUri: Uri? = null
    var bitmap: Bitmap? = null
    var decryptionPassword: CharArray? = null
    var encryptionAlgorithm = EncryptionAlgorithm.NONE

    fun createService(applicationContext: Context) {
        fileService = FileService(applicationContext)
        //payloadService = PayloadService(fileService)
        bitmapService = BitmapService(applicationContext)
        revealService = RevealService()
    }

    fun imageUriChanged(image: Uri) {
        viewModelScope.launch {
            imageUri = image
            bitmap = bitmapService.decodeBitmap(imageUri!!)
            checkSettings()
        }

    }

    fun decryptionPasswordChanged(decryptionPasswordValue: CharArray) {
        decryptionPassword = decryptionPasswordValue
        checkSettings()
    }

    private fun checkSettings() {

        viewModelScope.launch {
            val inputStream = ByteArrayInputStream(bitmapService.getPixelBytes(bitmap!!))
            val steganoInfo = inputStream.use { revealService.revealInfo(inputStream) }

            if (steganoInfo == null) {
                liveDataInputState.postValue(false)
            } else {
                liveDataInputState.postValue(true)
            }

            val containsHiddenData = steganoInfo != null
            val isEncrypted = steganoInfo?.encryptionAlgorithm == EncryptionAlgorithm.AES_256
            /*liveDataDecryptionPasswordShown.postValue(isEncrypted)
            liveDataStartEnabled.postValue(containsHiddenData && ((isEncrypted && ArrayUtils.isNotEmpty(decryptionPassword)) || !isEncrypted))
    */
            steganoInfo?.let {
                if (it.encryptionAlgorithm == EncryptionAlgorithm.NONE) {
                    liveDataTextRes.value = R.string.reveal_info
                } else {
                    liveDataTextRes.value = R.string.reveal_info_encrypted
                }

                liveDataVersion.value = it.version.toString()

                if (it.payloadType == Type.PLAINTEXT) {
                    liveDataTypeTextRes.value = R.string.plaintext

                } else if (it.payloadType == Type.FILE) {
                    liveDataTypeTextRes.value = R.string.file
                }

                if (it.encryptionAlgorithm == EncryptionAlgorithm.NONE) {
                    liveDataEncryptionTextRes.value = R.string.encryption_none

                } else if (it.encryptionAlgorithm == EncryptionAlgorithm.AES_256) {
                    liveDataEncryptionTextRes.value = R.string.encryption_aes
                }
            }
        }

    }

    //


    //imageUri: Uri, decryptionPassword: CharArray?
    fun decode() {

        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableLiveData.postValue(true)
            try {
                // Get input file. Unfortunately, there is currently no way to get the bitmap pixels as stream
                // hence we have to create it on our own
                val startTime = System.currentTimeMillis()

                val bitmap = bitmapService.decodeBitmap(imageUri!!)
                val inputStream = ByteArrayInputStream(bitmapService.getPixelBytes(bitmap))

                // Reveal the data
                val startTimeReveal = System.currentTimeMillis()
                val data = inputStream.use { revealService.reveal(inputStream, decryptionPassword) }
                val revealTimeInMs = System.currentTimeMillis() - startTimeReveal
                outputPayload = data.payload

                if (outputPayload?.getType() == Type.FILE) {
                    val filePayload = outputPayload as FilePayload
                    temporaryOutputFileUri =
                        fileService.createTemporaryFile(filePayload.mimeType, filePayload.file)
                }

                // Create statistics and finish
                statistics =
                    OperationStatistics(System.currentTimeMillis() - startTime, revealTimeInMs)
                dataSizeInBytes = data.getEstimatedLengthInBytes().toLong()
                loadingMutableLiveData.postValue(false)
                decodeErrorMutableLiveData.postValue(false)

            } catch (e: Exception) {
                Log.e(" ViewModel", e.message, e)
                decodeErrorMutableLiveData.postValue(true)
                loadingMutableLiveData.postValue(false)
            }
        }

    }

    fun finish() {
        if (temporaryOutputFileUri != null) {
            fileService.deleteTemporaryFile(temporaryOutputFileUri!!)
        }
    }

    fun backPressed() {

    }
}