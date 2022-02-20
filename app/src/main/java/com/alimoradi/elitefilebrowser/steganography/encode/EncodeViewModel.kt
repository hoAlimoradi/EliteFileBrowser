package com.alimoradi.elitefilebrowser.steganography.encode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alimoradi.elitefilebrowser.main.ApplicationGraph
import com.alimoradi.elitefilebrowser.steganography.OperationStatistics
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.EncryptionAlgorithm
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.HideService
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.Payload
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.SteganoData
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.service.BitmapService
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.service.FileService
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.service.PayloadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class EncodeViewModel : ViewModel() {

    private var plainTextShownTextInput = false
    private var selectFileShownTextInput = false
    private var nextButtonEnabledTextInput = false

    lateinit var intentTextInput: Intent

    private var fileImagetInput: Uri? = null

    //image


    private var fileImagetEncodeHolder: Uri? = null
    var bitmap: Bitmap? = null
    var payload: Payload? = null
    var encryptionAlgorithm = EncryptionAlgorithm.NONE
    var encryptionPassword: CharArray? = null

    val MAX_FILE_SIZE = 25 * 1000 * 1000


    var encodeDataBridge = EncodeDataBridge()

    var imageSteganographyIsLargeEnough = false
    var imageSteganographyIsTooLarge = false

    //settings
    var temporaryOutputFileUri: Uri? = null
    var statistics: OperationStatistics? = null
    var dataSizeInBytes: Long = 0


    lateinit var bitmapService: BitmapService
    lateinit var fileService: FileService
    lateinit var payloadService: PayloadService
    lateinit var hideService: HideService

    val imageSteganographyIsTooLargeMutableLiveData = MutableLiveData<Boolean>()
    val enoughLargeMutableLiveData = MutableLiveData<Boolean>()
    val loadingMutableLiveData = MutableLiveData<Boolean>()
    val encodeErrorMutableLiveData = MutableLiveData<Boolean>()

    val imageSteganographyMutableLiveData = MutableLiveData<String>()

    lateinit var context: Context

    val invalidInputsErrorMutableLiveData = MutableLiveData<Boolean>()

    fun createService(applicationContext: Context) {
        context = applicationContext
        fileService = FileService(applicationContext)
        //payloadService = PayloadService(fileService)
        bitmapService = BitmapService(applicationContext)
        hideService = HideService()
    }

    fun checkImageSteganographyIsTooLarge(uri: Uri) {

        viewModelScope.launch {
            var isTooLarge = false
            val fileSizeInBytes = fileService.getFileSize(uri)
            imageSteganographyMutableLiveData.postValue(fileService.formatFileInfo(uri))
            fileImagetEncodeHolder = uri
            Log.e("jafar", "  fileSizeInBytes : " + fileSizeInBytes)
            isTooLarge = fileSizeInBytes > MAX_FILE_SIZE
            if (!isTooLarge) {
                bitmap = bitmapService.decodeBitmap(uri)
                checkIsImageLargeEnough()
            }

            imageSteganographyIsTooLargeMutableLiveData.postValue(isTooLarge)
        }
    }

    fun checkIsImageLargeEnough() {
        viewModelScope.launch {
            var isLargeEnough = false

            bitmap?.let { bitmap ->
                encodeDataBridge.bitmap = bitmap
                payload?.let {
                    isLargeEnough =
                        hideService.isImageLargeEnough(bitmap.allocationByteCount, SteganoData(it))
                }

            }
            enoughLargeMutableLiveData.postValue(isLargeEnough)
        }

    }

    fun createPayload(plainText: String) {
        payloadService = PayloadService(fileService)
        payload = payloadService.createPayload(plainText, null)
    }

    fun createPayload(uri: Uri) {
        payloadService = PayloadService(fileService)
        payload = payloadService.createPayload(null, null)
    }

    fun initailSteganographyFile() {

        viewModelScope.launch(Dispatchers.IO) {
            var encodeHasError = false
            loadingMutableLiveData.postValue( true )
            payload?.let { payload ->

                encodeDataBridge.steganoData = SteganoData(
                    payload = payload,
                    encryptionAlgorithm = encryptionAlgorithm,
                    encryptionPassword = encryptionPassword
                )


                val bitmap = encodeDataBridge.bitmap
                require(bitmap != null) { "Bitmap must not be null" }

                val steganoData = encodeDataBridge.steganoData
                require(steganoData != null) { "SteganoData must not be null" }

                // Release all resources from the bridge
                encodeDataBridge.clear()
                //
                try {
                    // Get input file. Unfortunately, there is currently no way to get the bitmap pixels as stream
                    // hence we have to create it on our own
                    val startTime = System.currentTimeMillis()
                    val inputStream = ByteArrayInputStream(bitmapService.getPixelBytes(bitmap))
                    val outputStream = ByteArrayOutputStream()

                    // Hide the data
                    val startTimeHide = System.currentTimeMillis()
                    inputStream.use {
                        outputStream.use {
                            hideService.hide(
                                inputStream,
                                outputStream,
                                steganoData
                            )
                        }
                    }
                    val hideTimeInMs = System.currentTimeMillis() - startTimeHide

                    // Create a PNG bitmap of the pixels containing the data
                    val outputBitmap = bitmapService.createBitmapFromPixelBytes(
                        outputStream.toByteArray(),
                        bitmap.width,
                        bitmap.height
                    )
                    val output = bitmapService.compressToPNG(outputBitmap)

                    // Write the PNG bitmap to the internal tmp dir
                    temporaryOutputFileUri = fileService.createTemporaryFile("image/png", output)
                    FileUtils.saveFileFromUri(context, temporaryOutputFileUri,  Environment.getExternalStorageDirectory().toString() + "/Documents" )


                    //val fileCreatorManager = ApplicationGraph.getFileCreatorManager()
                    /*SavedBitmap().fileFromContentUri(context, temporaryOutputFileUri!!)

                    //SavedBitmap().saveImage()
                    val docsFolder =
                        java.io.File(Environment.getExternalStorageDirectory().toString() + "/Documents")
                    var isPresent = true
                    if (!docsFolder.exists()) {
                        isPresent = docsFolder.mkdir()
                    }
                    if (isPresent) {
                        val file = java.io.File(docsFolder.absolutePath, "image/png")

                    } else {
                        // Failure
                    }
*/


                    // Create statistics and finish
                    statistics =
                        OperationStatistics(System.currentTimeMillis() - startTime, hideTimeInMs)
                    dataSizeInBytes = steganoData.getEstimatedLengthInBytes().toLong()
                    encodeHasError = false
                    val x = statistics

                    Log.e(" "," statistics" + x!!.totalTimeInMs)

                } catch (e: Exception) {
                    Log.e("HideViewModel", e.message, e)
                    encodeHasError = true
                }
            }

            encodeErrorMutableLiveData.postValue(encodeHasError)
            loadingMutableLiveData.postValue( false )
        }


    }




   /* private fun createFileFromInputStream(inputStream: java.io.InputStream, fileName: String): java.io.File? {
        try {
            private val FILE_NAM = "video"
            var outputfile: String = getFilesDir() + java.io.File.separator.toString() + FILE_NAM + "_tmp.mp4"

            var `in`: java.io.InputStream = getContentResolver().openInputStream(videoFileUri)
            val f = java.io.File(fileName)
            f.setWritable(true, false)
            val outputStream: java.io.OutputStream = java.io.FileOutputStream(f)
            val buffer = ByteArray(1024)
            var length = 0
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()
            return f
        } catch (e: IOException) {
            println("error in creating a file")
            e.printStackTrace()
        }
        return null
    }*/

    override fun onCleared() {
        super.onCleared()
       // finish()
    }
    fun finish() {
        if (temporaryOutputFileUri != null) {
            fileService.deleteTemporaryFile(temporaryOutputFileUri!!)
        }
        if (fileImagetEncodeHolder != null) {
            fileService.deleteTemporaryFile(fileImagetEncodeHolder!!)
        }

    }

    fun backPressed() {
        /*if (liveDataStatus.value == OperationStatus.RUNNING || liveDataStatus.value == OperationStatus.SUCCESS) {
            liveEventCancelWarning.send()
        }*/
    }

    fun checkFirstFileInput(uri: Uri) {

    }
}