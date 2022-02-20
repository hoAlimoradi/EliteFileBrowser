@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.file_api_android

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.N
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.alimoradi.file_api.*
import com.alimoradi.file_api_android.internal.*
import com.alimoradi.file_api_android.internal.file_children.FileChildrenModule
import java.io.File
import java.net.URLConnection


class FileModule(
    private val context: Context,
    private val permissionRequestAddOn: PermissionRequestAddOn
) {

    private val mediaScannerInternal by lazy { createMediaScanner() }
    private val filePathManagerInternal by lazy { createFilePathManager() }
    private val fileRootManagerInternal by lazy { createFileRootManager() }
    private val fileSizeManagerInternal by lazy { createFileSizeManager() }
    private val fileScopedStorageManagerInternal by lazy { createFileScopedStorageManager() }
    private val fileZipManagerInternal by lazy { createFileZipManager() }
    private val permissionManagerInternal by lazy { createPermissionManager() }

    fun getMediaScanner() = mediaScannerInternal

    fun getPermissionManager() = permissionManagerInternal

    fun createFileManager(): FileManager {
        val fileManager = FileManagerAndroid(permissionManagerInternal)
        val fileObserver = RecursiveFileObserver(
            fileRootManagerInternal.getFileRootPath()
        ) {
            if (it != null && !it.endsWith("/null")) {
                val path = File(it).parentFile.absolutePath
                fileManager.refresh(path)
            }
        }
        mediaScannerInternal.registerListener(object : MediaScanner.RefreshListener {
            override fun onContentChanged(path: String) {
                fileManager.refresh(path)
            }
        })
        fileObserver.startWatching()
        return fileManager
    }

    fun createFileChildrenManager(): FileChildrenManager {
        val addOn = object : FileChildrenModule.AddOn {
            override fun onFileSizeComputed(
                path: String,
                length: Long
            ) {
                fileSizeManagerInternal.setSize(
                    path,
                    length
                )
            }
        }
        return FileChildrenModule(
            context,
            fileRootManagerInternal,
            mediaScannerInternal,
            permissionManagerInternal,
            addOn
        ).createFileChildrenManager()
    }

    fun createFileOpenManager(): FileOpenManager {
        val addOn = object : FileOpenManagerAndroid.AddOn {
            override fun startActivity(path: String, mime: String) {
                val uri = getUriFromFilePath(
                    context,
                    path
                )
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, mime)
                if (context !is Activity) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(context, intent)
            }
        }
        return FileOpenManagerAndroid(
            fileZipManagerInternal,
            addOn
        )
    }

    fun createFileDeleteManager(): FileDeleteManager {
        val addOn = object : FileDeleteManagerAndroid.AddOn {
            override fun deleteFromContentResolver(
                path: String
            ): Boolean {
                val documentFile = DocumentFile.fromSingleUri(
                    context,
                    Uri.parse(path)
                ) ?: return false
                return documentFile.delete()
            }
        }
        return FileDeleteManagerAndroid(
            mediaScannerInternal,
            filePathManagerInternal,
            addOn
        )
    }

    fun createFileCopyCutManager(): FileCopyCutManager = FileCopyCutManagerAndroid(
        mediaScannerInternal
    )

    fun createFileCreatorManager(): FileCreatorManager {

        val addOn = object : FileCreatorManagerAndroid.AddOn {
            override fun createFileFromContentResolver(
                parentPath: String,
                name: String
            ): Boolean {
                val documentFile = DocumentFile.fromSingleUri(
                    context,
                    Uri.parse(parentPath)
                ) ?: return false
                // TODO
                return documentFile.createFile(
                    "image/png",
                    name
                ) != null
            }

            override fun createDirectoryFromContentResolver(
                parentPath: String,
                name: String
            ): Boolean {
                val documentFile = DocumentFile.fromSingleUri(
                    context,
                    Uri.parse(parentPath)
                ) ?: return false
                return documentFile.createDirectory(name) != null
            }
        }
        return FileCreatorManagerAndroid(
            permissionManagerInternal,
            mediaScannerInternal,
            addOn
        )
    }

    fun getFilePathManager(): FilePathManager {
        return filePathManagerInternal
    }

    fun createFileRenameManager(): FileRenameManager = FileRenameManagerAndroid(
        mediaScannerInternal
    )

    fun getFileSizeManager() = fileSizeManagerInternal

    fun getFileRootManager(): FileRootManager {
        return FileRootManagerImpl(
            fileScopedStorageManagerInternal
        )
    }

    fun getFileScopedStorageManager(): FileScopedStorageManager {
        return fileScopedStorageManagerInternal
    }

    fun createFileSearchManager(): FileSearchManager {
        return FileSearchManagerAndroid(
            fileRootManagerInternal
        )
    }

    fun createFileShareManager(): FileShareManager {
        val addOn = object : FileShareManagerAndroid.AddOn {
            override fun startActivity(
                path: String,
                mime: String
            ) {

                when (mime) {
                    "esc", "" -> {
                        val intentShareFile = Intent(Intent.ACTION_SEND)
                        intentShareFile.type = "text/html";
                        //intentShareFile.type = URLConnection.guessContentTypeFromName("json")
                        intentShareFile.putExtra(
                            Intent.EXTRA_STREAM,
                            Uri.parse("file://" + File(path).absolutePath)
                        )
                        val intent = Intent.createChooser(intentShareFile, "Share Signal Config File")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                        startActivity(context,intent )

                    }
                    "esenc" -> {
                        // code to run if condition is true
                        val intentShareFile = Intent(Intent.ACTION_SEND)
                        intentShareFile.type = "text/html";
                        //intentShareFile.type = URLConnection.guessContentTypeFromName("json")
                        intentShareFile.putExtra(
                            Intent.EXTRA_STREAM,
                            Uri.parse("file://" + File(path).absolutePath)
                        )
                        val intent = Intent.createChooser(intentShareFile, "Share Encrypted File")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                        startActivity(context,intent )
                    }
                    else -> {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.setDataAndType(getUriFromIOFile(context, File(path)), mime)
                        if (context !is Activity) {
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(context, intent)
                    }
                }




                /*val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(getUriFromIOFile(context, File(path)), mime)
                if (context !is Activity) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(context, intent)*/

            }
        }
        return FileShareManagerAndroid(addOn)
    }

    private fun shareFile(file: File) {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = URLConnection.guessContentTypeFromName(file.name)
        intentShareFile.putExtra(
            Intent.EXTRA_STREAM,
            Uri.parse("file://" + file.absolutePath)
        )

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");
        startActivity(context, Intent.createChooser(intentShareFile, "Share File"))
    }

    fun createFileSortManager(): FileSortManager = FileSortManagerImpl()

    private fun createFilePathManager(): FilePathManager {
        return FilePathManagerAndroid()
    }

    private fun createFileSizeManager(): FileSizeManager {
        val fileSizeManager = FileSizeManagerAndroid(
            permissionManagerInternal
        )
        mediaScannerInternal.registerListener(object : MediaScanner.RefreshListener {
            override fun onContentChanged(path: String) {
                fileSizeManager.loadSize(path, true)
            }
        })
        return fileSizeManager
    }

    private fun createMediaScanner(): MediaScanner {
        val addOn = object : MediaScannerAndroid.AddOn {
            override fun refreshSystemMediaScanDataBase(path: String) {
                refreshSystemMediaScanDataBase(context, path)
            }
        }
        return MediaScannerAndroid(
            addOn
        )
    }

    private fun createFileRootManager(): FileRootManager {
        return FileRootManagerImpl(
            fileScopedStorageManagerInternal
        )
    }

    private fun createFileScopedStorageManager(): FileScopedStorageManager {
        return FileScopedStorageManagerImpl(context)
    }

    private fun createPermissionManager(): PermissionManager {
        val permissionModule = PermissionModule(
            context,
            fileScopedStorageManagerInternal,
            permissionRequestAddOn
        )
        return permissionModule.createPermissionManager()
    }

    fun createFileZipManager(): FileZipManager {
        return FileZipManagerAndroid(
            mediaScannerInternal
        )
    }

    companion object {

        private fun getUriFromFilePath(
            context: Context,
            filePath: String
        ): Uri {
            return if (filePath.startsWith("content://")) {
                Uri.parse(filePath)
            } else {
                getUriFromIOFile(
                    context,
                    File(filePath)
                )
            }
        }

        private fun getUriFromIOFile(
            context: Context,
            ioFile: File
        ): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getUriFromFileOverN(context, ioFile)
        } else {
            Uri.fromFile(ioFile)
        }

        private fun getUriFromFileOverN(
            context: Context,
            file: File
        ): Uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )

        private fun startActivity(
            context: Context,
            intent: Intent
        ) {
            try {
                if (Build.VERSION.SDK_INT >= N) {
                    startActivityOverN(context, intent)
                } else {
                    context.startActivity(intent)
                }
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context, "Oops, there is an error. Try with \"Open as...\"",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        @RequiresApi(api = N)
        private fun startActivityOverN(
            context: Context,
            intent: Intent
        ) {
            try {
                context.startActivity(intent)
            } catch (e: Exception) { // Catch a FileUriExposedException.
                // Test on KitKat if your replace Exception by FileUriExposedException.
                Toast.makeText(
                    context, "Oops, there is an error.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /**
         * @param context : it is the reference where this method get called
         * @param docPath : absolute path of file for which broadcast will be send to refresh media database
         */
        private fun refreshSystemMediaScanDataBase(context: Context, docPath: String) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(File(docPath))
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
            MediaScannerConnection.scanFile(
                context,
                arrayOf(docPath),
                null
            ) { _, _ ->
            }
        }
    }
}
