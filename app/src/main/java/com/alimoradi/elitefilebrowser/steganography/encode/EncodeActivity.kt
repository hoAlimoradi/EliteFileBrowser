package com.alimoradi.elitefilebrowser.steganography.encode

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.dialog.DialogActivity
import com.alimoradi.elitefilebrowser.steganography.TextWatcherAdapter
import com.alimoradi.elitefilebrowser.steganography.success.EncodeSuccessActivity
import com.alimoradi.elitefilebrowser.steganography.imagesteganography.EncryptionAlgorithm
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_encode.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils


class EncodeActivity : AppCompatActivity() {

    //TextInput

    private var textInput: String? = null
    private var fileInput: Uri? = null

    private var fileEncodeHolder: Uri? = null

    var imageSteganographyIsTooLarge = false
    var enoughLargeError = false

    private val REQUEST_CODE_INPUT_FILE = 1
    private val REQUEST_CODE_IMAGE_FILE = 2
    lateinit var viewModel: EncodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode)

        viewModel = ViewModelProviders.of(this).get(EncodeViewModel::class.java)
        viewModel.createService(this.applicationContext)

        //input data
        initView()
        observeViewModel()
        backImageView.setOnClickListener {
            this.finish()
        }
        val radioGroupTextInput = findViewById<View>(R.id.radioGroupTextInput) as RadioGroup
        radioGroupTextInput.setOnCheckedChangeListener { group, checkedId ->

            val rb: RadioButton = findViewById<View>(checkedId) as RadioButton
            Toast.makeText(applicationContext, rb.getText(), Toast.LENGTH_SHORT).show()
            if (rb.text == getString(R.string.plaintext)) {
                showTextToEncode()

            } else if (rb.text == getString(R.string.file)) {
                showFileToEncode()
            }
        }
        selectFileInputButton.setOnClickListener {
            selectFileClicked(
                mimeType = getTextInputEncodeMimeType(),
                title = applicationContext.getString(R.string.file_select),
                requestCode = 1
            )
        }

        selectFileImageButton.setOnClickListener {
            selectFileClicked(
                mimeType = getImageInputEncodeMimeType(),
                title = applicationContext.getString(R.string.file_select),
                requestCode = 2
            )
        }

        //setting
        layoutTextPasswordEncryption.visibility = View.GONE
        val radioGroupEncryption = findViewById<View>(R.id.radioGroupEncryption) as RadioGroup
        radioGroupEncryption.setOnCheckedChangeListener { group, checkedId ->

            val rb: RadioButton = findViewById<View>(checkedId) as RadioButton
            if (rb.text == getString(R.string.encryption_none)) {
                showSteganographyPure()
                viewModel.encryptionAlgorithm = EncryptionAlgorithm.NONE
            } else if (rb.text == getString(R.string.encryption_aes)) {
                showSteganographyEncrypted()
                viewModel.encryptionAlgorithm = EncryptionAlgorithm.AES_256
            }
        }

        //inpput
        editTextTextInputEncode.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                //plainTextInput = s.toString()
                viewModel.createPayload(plainText = s.toString())
                textInput = s.toString()
            }
        })

        //pass
        editTextEncryptionPassword.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.encryptionPassword = s.toString().toCharArray()
            }
        })


        encodeStartButton.setOnClickListener {

            if (inValidInput()) {
                //Toast.makeText(applicationContext, "خطا رخ داده", Toast.LENGTH_SHORT).show()
                val input = DialogActivity.DialogInput(
                    dialogId = "encodeinputerror",
                    title = "! encodeinputerror",
                    message = "لطفا همه موارد را تکمیل کنید",
                    positive = "متوجه شدم",
                    negative = "لغو",
                    input = " ",
                    type = DialogActivity.DialogInput.DIALOG_TYPE_ALERT
                )
                DialogActivity.start(applicationContext, input)

                return@setOnClickListener
            }

            GlobalScope.launch(Dispatchers.IO) {
                viewModel.initailSteganographyFile()
            }


        }


    }

    private fun observeViewModel() {
        viewModel.imageSteganographyIsTooLargeMutableLiveData.observe(this) {

            if (it) {
                showTooLargeImageError()
            } else {
                hideTooLargeImageError()
            }

        }

        viewModel.enoughLargeMutableLiveData.observe(this) {

            if (it) {
                hideEnoughLargeImageError()

            } else {
                showEnoughLargeImageError()

            }

        }


        viewModel.loadingMutableLiveData.observe(this) {

            if (it) {
                showLoading()
            } else {
                hideLoading()
            }

        }

        viewModel.encodeErrorMutableLiveData.observe(this) {
            Log.e("", "  encodeErrorMutableLiveData " + it)
            if (!it) {

                val statistics = Gson().toJson(viewModel.statistics)
                val dataSizeInBytes = Gson().toJson(viewModel.dataSizeInBytes)
                val input = EncodeSuccessActivity.EncodeSuccessInput(
                    statistics = Gson().toJson(viewModel.statistics),
                    dataSizeInBytes = Gson().toJson(viewModel.dataSizeInBytes),
                    message = ""
                )

                EncodeSuccessActivity.start(
                    context = this.applicationContext,
                    input = input
                )
                this.finish()
            }
        }



        viewModel.imageSteganographyMutableLiveData.observe(this) {
            textViewFileInfoImageInput.text = it
        }


    }

    private fun showEnoughLargeImageError() {
        imageFileErrorMessageContainer.visibility = View.VISIBLE
        textViewMessageErrorForImageFile.text = getString((R.string.hide_image_too_small))
        enoughLargeError = true
    }

    private fun hideEnoughLargeImageError() {
        imageFileErrorMessageContainer.visibility = View.GONE
        enoughLargeError = false
    }

    private fun hideTooLargeImageError() {
        imageFileErrorMessageContainer.visibility = View.GONE
        imageSteganographyIsTooLarge = false
    }

    private fun showTooLargeImageError() {
        imageFileErrorMessageContainer.visibility = View.VISIBLE
        textViewMessageErrorForImageFile.text = getString((R.string.hide_image_too_large))
        imageSteganographyIsTooLarge = true


    }

    private fun showSteganographyEncrypted() {
        layoutTextPasswordEncryption.visibility = View.VISIBLE
    }

    private fun showSteganographyPure() {
        layoutTextPasswordEncryption.visibility = View.GONE
    }

    private fun showFileToEncode() {
        filePickerInputEncode.visibility = View.VISIBLE
        layoutTextInputEncode.visibility = View.GONE
    }

    private fun showTextToEncode() {
        filePickerInputEncode.visibility = View.GONE
        layoutTextInputEncode.visibility = View.VISIBLE
    }

    private fun initView() {
        hideLoading()
        filePickerInputEncode.visibility = View.GONE
        editTextTextInputEncode.visibility = View.VISIBLE
    }


    fun showLoading() {
        loadingConstraintLayout.visibility = View.VISIBLE
    }

    fun hideLoading() {
        loadingConstraintLayout.visibility = View.GONE
    }


    private fun showEncodeSuccessActivity() {
        startActivity(Intent(applicationContext, EncodeSuccessActivity::class.java))
    }


    fun backPressed() {
        /*if (liveDataStatus.value == OperationStatus.RUNNING || liveDataStatus.value == OperationStatus.SUCCESS) {
            liveEventCancelWarning.send()
        }*/
    }

    private fun getTextInputEncodeMimeType(): String {
        return "*/*"
    }

    private fun getImageInputEncodeMimeType(): String {
        return "image/*"
    }

    private fun selectFileClicked(mimeType: String, title: String, requestCode: Int) {
        val pickIntent =
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = mimeType
        val intentTextInput = Intent.createChooser(pickIntent, title)
        startActivityForResult(intentTextInput, requestCode) // 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //to hide
        if (requestCode == REQUEST_CODE_INPUT_FILE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            data.data?.let {
                viewModel.checkFirstFileInput(it)

            }
        }

        //image
        if (requestCode == REQUEST_CODE_IMAGE_FILE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            data.data?.let {
                viewModel.checkImageSteganographyIsTooLarge(it)

            }
        }

    }


    private fun inValidInput(): Boolean {
        val plainTextEntered = StringUtils.isBlank(textInput)
        //val fileSelected = selectFileShownTextInput && fileTextInput != null
        val nextButtonEnabledTextInput =
            imageSteganographyIsTooLarge || enoughLargeError || plainTextEntered
        return nextButtonEnabledTextInput
    }


    private fun showCancelWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.cancel)
            .setMessage(R.string.cancel_confirmation)
            .setPositiveButton(R.string.yes) { _, _ -> finish() }
            .setNegativeButton(R.string.no) { _, _ -> }
            .show()
    }

    override fun finish() {
        //viewModel.finish()
        super.finish()
    }

    override fun onBackPressed() {
        // viewModel.backPressed()
    }

}