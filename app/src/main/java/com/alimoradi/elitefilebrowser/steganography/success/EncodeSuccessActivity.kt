package com.alimoradi.elitefilebrowser.steganography.success

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.dialog.DialogActivity
import com.alimoradi.elitefilebrowser.steganography.OperationStatistics
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_encode.*
import kotlinx.android.synthetic.main.activity_encode_success.*
import org.json.JSONObject

class EncodeSuccessActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSION = 1
    private val REQUEST_CODE_SAVE_FILE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode_success)
        backImageViewInResult.setOnClickListener {
            this.finish()
        }
        intent?.extras?.getString(EXTRA_INPUT)?.let {
            val input = EncodeSuccessInput.fromJson(it)

            val statistics = Gson().fromJson(input.statistics, OperationStatistics::class.java)
            val dataSize = Gson().fromJson(input.dataSizeInBytes, Long::class.java)
            textViewStatisticsBytes.text = "$dataSize k of data were successfully hidden"
            textViewStatisticsHideTime.text = statistics.operationInMs.toString()
            text_view_statistics_image_processing.text = statistics.imageProcessingInMs.toString()
            textViewStatisticsTotalTime.text = statistics.totalTimeInMs.toString()
        }

        encodeSuccessNextButton.setOnClickListener {
            /*if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
            } else {
                saveFileClicked()
            }*/
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SAVE_FILE && data != null && data.data != null) {
            //onFileUriChanged(data.data!!)
            //onFileSaved(data.data!!)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //saveFileClicked()
        }
    }

    data class EncodeSuccessInput(
        var statistics: String,
        var dataSizeInBytes: String,
        val message: String?
    ) {
        companion object {

            fun toJson(input: EncodeSuccessInput): String {
                val json = JSONObject()
                json.put("statistics", input.statistics)
                json.put("dataSizeInBytes", input.dataSizeInBytes)
                json.put("message", input.message)
                return json.toString()
            }

            fun fromJson(jsonString: String): EncodeSuccessInput {
                val json = JSONObject(jsonString)
                val statistics = json.getString("statistics")
                val dataSizeInBytes = json.getString("dataSizeInBytes")
                val message = if (json.has("message")) {
                    json.getString("message")
                } else {
                    null
                }

                return EncodeSuccessInput(
                    statistics,
                    dataSizeInBytes,
                    message
                )
            }
        }
    }

    companion object {

        private const val EXTRA_INPUT = "EXTRA_INPUT"

        @JvmStatic
        fun start(context: Context, input: EncodeSuccessInput) {
            val intent = Intent(context, EncodeSuccessActivity::class.java)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            intent.putExtra(EXTRA_INPUT, EncodeSuccessInput.toJson(input))
            context.startActivity(intent)
        }
    }
}