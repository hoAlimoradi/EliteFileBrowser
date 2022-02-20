package com.alimoradi.elitefilebrowser.steganography.decode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.steganography.encode.EncodeViewModel
import kotlinx.android.synthetic.main.activity_decode.*
import kotlinx.android.synthetic.main.activity_decode.backImageView
import kotlinx.android.synthetic.main.activity_decode.revealInfoText
import kotlinx.android.synthetic.main.activity_encode.*
import kotlinx.android.synthetic.main.activity_encode.imageFileErrorMessageContainer

class DecodeActivity : AppCompatActivity() {
    private val REQUEST_CODE_IMAGE_FILE = 2
    private lateinit var viewModel: DecodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode)
        viewModel = ViewModelProviders.of(this).get(DecodeViewModel::class.java)
        viewModel.createService(this.applicationContext)

        //input data
        initView()
        observeViewModel()
        backImageView.setOnClickListener {
            this.finish()
        }
        selectFileImageButtonToDecoed.setOnClickListener {
            selectFileClicked(
                mimeType = "image/*",
                title = applicationContext.getString(R.string.file_select),
                requestCode = 2
            )
        }

        decodeStartButton.setOnClickListener {
            viewModel.decode()
        }

    }

    private fun initView() {
        decodeStartButton.visibility = View.GONE
        successInfoLinearLayout.visibility = View.GONE
        imageFileErrorMessageContainer.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.liveDataTextRes.observe(this) {
            revealInfoText.setText(it)
        }

        viewModel.liveDataVersion.observe(this) {
            revealInfoVersion.setText(it)
        }


        viewModel.liveDataTypeTextRes.observe(this) {

            revealInfoType.setText(it)
        }

        viewModel.liveDataEncryptionTextRes.observe(this) {
            revealInfoEncryption.setText(it)
        }

        viewModel.loadingMutableLiveData.observe(this) {

        }

        viewModel.decodeErrorMutableLiveData.observe(this) {

        }

        viewModel.liveDataInputState.observe(this) {
            if (it) {
                decodeStartButton.visibility = View.VISIBLE
                successInfoLinearLayout.visibility = View.VISIBLE
                imageFileErrorMessageContainer.visibility = View.GONE

            } else {
                decodeStartButton.visibility = View.GONE
                successInfoLinearLayout.visibility = View.GONE
                imageFileErrorMessageContainer.visibility = View.VISIBLE
            }
        }

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

        //image
        if (requestCode == REQUEST_CODE_IMAGE_FILE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            data.data?.let {
                viewModel.imageUriChanged(it)
            }
        }

    }

    override fun finish() {
        viewModel.finish()
        super.finish()
    }

    override fun onBackPressed() {
        viewModel.backPressed()
    }

    private fun showCancelWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.cancel)
            .setMessage(R.string.cancel_confirmation)
            .setPositiveButton(R.string.yes) { _, _ -> finish() }
            .setNegativeButton(R.string.no) { _, _ -> }
            .show()
    }



}