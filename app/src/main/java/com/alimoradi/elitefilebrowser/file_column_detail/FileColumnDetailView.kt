@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_column_detail

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.addAccount.AddAccountActivity
import com.alimoradi.elitefilebrowser.common.DialogUtils
import com.alimoradi.elitefilebrowser.main.ApplicationGraph
import com.alimoradi.file_api.File


class FileColumnDetailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr),
    FileColumnDetailContract.Screen {

    private val view = View.inflate(context, R.layout.view_file_column_detail, this)
    private val title: TextView = view.findViewById(R.id.view_file_column_detail_title)
    private val path: TextView = view.findViewById(R.id.view_file_column_detail_path)
    private val length: TextView = view.findViewById(R.id.view_file_column_detail_length)
    private val lastModified: TextView = view.findViewById(R.id.view_file_column_detail_last_modified)
    private val playPause: ImageView = view.findViewById(R.id.view_file_column_detail_play_pause)
    private val next: View = view.findViewById(R.id.view_file_column_detail_play_next)
    private val previous: View = view.findViewById(R.id.view_file_column_detail_play_previous)
    private val trustSignalConfigFile: View = view.findViewById(R.id.view_file_colmun_detail_trust_signal_config)
    private val zipFile: View = view.findViewById(R.id.view_file_colmun_detail_zip)
    private val unZipFile: View = view.findViewById(R.id.view_file_colmun_detail_un_zip)

    private val userAction: FileColumnDetailContract.UserAction = createUserAction()

    init {
        playPause.setOnClickListener {
            userAction.onPlayPauseClicked()
        }
        next.setOnClickListener {
            userAction.onNextClicked()
        }
        previous.setOnClickListener {
            userAction.onPreviousClicked()
        }

        trustSignalConfigFile.setOnClickListener {
            userAction.onTrustClicked()
        }

        zipFile.setOnClickListener {
            userAction.onZipClicked()
        }

        unZipFile.setOnClickListener {
            userAction.onUnZipClicked()
        }

        findViewById<View>(R.id.view_file_colmun_detail_open).setOnClickListener {
            userAction.onOpenClicked()
        }
        findViewById<View>(R.id.view_file_column_detail_open_as).setOnClickListener {
            userAction.onOpenAsClicked()
        }
        findViewById<View>(R.id.view_file_colmun_detail_share).setOnClickListener {
            userAction.onShareClicked()
        }
        findViewById<View>(R.id.view_file_colmun_detail_rename).setOnClickListener {
            userAction.onRenameClicked()
        }

        findViewById<View>(R.id.view_file_colmun_detail_encryption).setOnClickListener {
            userAction.onEncryptionClicked()
        }

        findViewById<View>(R.id.view_file_colmun_detail_copy).setOnClickListener {
            userAction.onCopyClicked()
        }
        findViewById<View>(R.id.view_file_colmun_detail_cut).setOnClickListener {
            userAction.onCutClicked()
        }
        findViewById<View>(R.id.view_file_colmun_detail_delete).setOnClickListener {
            userAction.onDeleteClicked()
        }



    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        userAction.onAttached()
    }

    override fun onDetachedFromWindow() {
        userAction.onDetached()
        super.onDetachedFromWindow()
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setPath(path: String) {
        this.path.text = context.getString(R.string.view_file_detail_path, path)
    }

    override fun setLength(length: String) {
        this.length.text = context.getString(R.string.view_file_detail_size, length)
    }

    override fun setLastModified(lastModifiedDateString: String) {
        this.lastModified.text = context.getString(
            R.string.view_file_detail_last_modified, lastModifiedDateString)
    }

    override fun showPlayPauseButton() {
        playPause.visibility = VISIBLE
    }

    override fun hidePlayPauseButton() {
        playPause.visibility = GONE
    }

    override fun showEncryptionSignalView(path: String, extension: String) {
        val intent = Intent(context, AddAccountActivity::class.java)
        val bundle = Bundle()
        bundle.putString("EXTRA_PATH", path)
        bundle.putString("EXTRA_FILE_EXTENTION", extension)
        intent.putExtras(bundle)
        startActivity(context, intent, bundle)
    }

    override fun showNextButton() {
        next.visibility = VISIBLE
    }

    override fun hideNextButton() {
        next.visibility = GONE
    }

    override fun showPreviousButton() {
        previous.visibility = VISIBLE
    }

    override fun hidePreviousButton() {
        previous.visibility = GONE
    }

    override fun setPlayPauseButtonImage(drawableRes: Int) {
        playPause.setImageResource(drawableRes)
    }

    override fun showDeleteConfirmation(fileName: String) {
        DialogUtils.alert(
            context,
            "Delete file?",
            "Do you want to delete: $fileName",
            "Yes",
            object : DialogUtils.OnDialogUtilsListener {
                override fun onDialogUtilsCalledBack() {
                    userAction.onDeleteConfirmedClicked()
                }
            },
            "No",
            null
        )
    }

    override fun showRenamePrompt(fileName: String) {
        DialogUtils.prompt(context, "Rename file?",
            "Enter a new name for: $fileName",
            "Rename",
            object : DialogUtils.OnDialogUtilsStringListener {
                override fun onDialogUtilsStringCalledBack(text: String) {
                    userAction.onRenameConfirmedClicked(text)
                }
            },
            "Dismiss",
            null,
            fileName,
            "File name",
            null
        )
    }

    override fun showOpenAsSelection() {
        val menuAlert = AlertDialog.Builder(context)
        val menuList = arrayOf(
            "Text",
            "Image",
            "Audio",
            "Video",
            "Other")
        menuAlert.setTitle("Open as:")
        menuAlert.setItems(menuList) { _, item ->
            val typeMime: String = when (item) {
                0 -> "text/plain"
                1 -> "image/*"
                2 -> "audio/*"
                3 -> "video/*"
                else -> "*/*"
            }
            userAction.onOpenAsConfirmedClicked(typeMime)
        }
        val menuDrop = menuAlert.create()
        menuDrop.show()
    }

    override fun showToast(deleteFailedTextRes: Int) {
        Toast.makeText(context, deleteFailedTextRes, Toast.LENGTH_LONG).show()
    }

    override fun setTextPrimaryColorRes(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        title.setTextColor(color)
        path.setTextColor(color)
        length.setTextColor(color)
        lastModified.setTextColor(color)
    }

    override fun setFileColumnDetailBackgroundColorRes(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        setBackgroundColor(color)
    }

    override fun showTrustButton() {
        trustSignalConfigFile.visibility = VISIBLE
    }

    override fun hideTrustButton() {
        trustSignalConfigFile.visibility = GONE
    }

    override fun showZipButton() {
        zipFile.visibility = VISIBLE
    }

    override fun hideZipButton() {
        zipFile.visibility = GONE
    }

    override fun showUnZipButton() {
        unZipFile.visibility = VISIBLE
    }

    override fun hideUnZipButton() {
        unZipFile.visibility = GONE
    }

    fun setFile(file: File?) {
        userAction.onFileChanged(file)
    }

    private fun createUserAction() = if (isInEditMode) {
        object : FileColumnDetailContract.UserAction {
            override fun onAttached() {}
            override fun onDetached() {}
            override fun onFileChanged(file: File?) {}
            override fun onOpenClicked() {}
            override fun onOpenAsClicked() {}
            override fun onOpenAsConfirmedClicked(typeMime: String) {}
            override fun onPlayPauseClicked() {}
            override fun onNextClicked() {}
            override fun onPreviousClicked() {}
            override fun onTrustClicked() {}
            override fun onZipClicked() {}
            override fun onUnZipClicked() {}
            override fun onDeleteClicked() {}
            override fun onDeleteConfirmedClicked() {}
            override fun onRenameClicked() {}
            override fun onEncryptionClicked() {}
            override fun onRenameConfirmedClicked(fileName: String) {}
            override fun onShareClicked() {}
            override fun onCopyClicked() {}
            override fun onCutClicked() {}
        }
    } else {
        val audioManager = ApplicationGraph.getAudioManager()
        val audioQueueManager = ApplicationGraph.getAudioQueueManager()
        val fileOpenManager = ApplicationGraph.getFileOpenManager()
        val fileDeleteManager = ApplicationGraph.getFileDeleteManager()
        val fileCopyCutManager = ApplicationGraph.getFileCopyCutManager()

        val fileZipManager = ApplicationGraph.getFileZipManager()

        val fileRenameManager = ApplicationGraph.getFileRenameManager()
        val fileShareManager = ApplicationGraph.getFileShareManager()
        val themeManager = ApplicationGraph.getThemeManager()
        val toastManager = ApplicationGraph.getToastManager()
        FileColumnDetailPresenter(
            this,
            audioManager,
            audioQueueManager,
            fileOpenManager,
            fileDeleteManager,
            fileCopyCutManager,
            fileZipManager,
            fileRenameManager,
            fileShareManager,
            themeManager,
            toastManager,
            R.drawable.ic_play_arrow_black_24dp,
            R.drawable.ic_pause_black_24dp,
            R.string.view_file_detail_delete_failed
        )
    }
}
