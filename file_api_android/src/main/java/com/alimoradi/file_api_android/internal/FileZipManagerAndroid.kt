package com.alimoradi.file_api_android.internal

import android.os.Environment
import android.util.Log
import com.alimoradi.file_api.FileExtension
import com.alimoradi.file_api.FileZipManager
import com.alimoradi.file_api.MediaScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileZipManagerAndroid(
    private val mediaScanner: MediaScanner
) : FileZipManager {

    private val SDPath = Environment.getExternalStorageDirectory().absolutePath
    private val dataPath = "$SDPath/elitefile/zipunzipFile/data/"
    private val zipPath = "$SDPath/EliteFile/Zip/"
    private val unzipPath = "$SDPath/elitefile/UnZip/"

    private val listeners = ArrayList<FileZipManager.FileZipListener>()

    override fun isZip(path: String) = FileExtension.ZIP.isCompliant(path)

    override fun unzip(
        path: String,
        outputPath: String
    ) {
        GlobalScope.launch(Dispatchers.Default) {
            val zipInput = java.io.File(path)
            val output = java.io.File(unzipPath)
           // unzipSync(zipInput, output)
           //  mediaScanner.refresh(output.parentFile.absolutePath)
            Log.e("jafar unzip ", unzipPath)
            unzipSync(zipInput, output)
           // FileHelper.unzip(sourceFile = path, destinationFolder = unzipPath)
            mediaScanner.refresh(unzipPath)
            GlobalScope.launch(Dispatchers.Main) {
                for (listener in listeners) {
                    listener.onUnzipEnded(path, outputPath)
                }
            }
        }
    }

    override fun zip(path: String, outputPath: String, filename: String) {
        //zipAll(zipFile = path , directory = outputPath)
        GlobalScope.launch(Dispatchers.Default) {

            val output = java.io.File(path)
            Log.e("jafar:" , " زیپ شد" + zipPath )
            if (FileHelper.zip(path, zipPath , filename + ".zip", true)) {
                Log.e("jafar:" , " زیپ شد")
            }
            mediaScanner.refresh(output.parentFile.absolutePath)
            GlobalScope.launch(Dispatchers.Main) {
                for (listener in listeners) {

                }
            }
        }

    }

    override fun registerFileZipListener(
        listener: FileZipManager.FileZipListener
    ) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun unregisterFileZipListener(
        listener: FileZipManager.FileZipListener
    ) {
        listeners.remove(listener)
    }

    companion object {

        @Throws(IOException::class)
        private fun unzipSync(zipFile: java.io.File, targetDirectory: java.io.File) {
            val zipInputStream = ZipInputStream(
                java.io.BufferedInputStream(
                    java.io.FileInputStream(
                        zipFile
                    )
                )
            )
            zipInputStream.use { zis ->
                var count: Int
                val buffer = ByteArray(8192)
                var zipEntry = zis.nextEntry
                while (zipEntry != null) {
                    val file = java.io.File(targetDirectory, zipEntry.name)
                    val dir = if (zipEntry.isDirectory) file else file.parentFile
                    if (!dir.isDirectory && !dir.mkdirs())
                        throw java.io.FileNotFoundException(
                            "Failed to ensure directory: " + dir.absolutePath
                        )
                    if (zipEntry.isDirectory) {
                        zipEntry = zis.nextEntry
                        continue
                    }
                    val fileOutputStream = java.io.FileOutputStream(file)
                    fileOutputStream.use {
                        count = zis.read(buffer)
                        while (count != -1) {
                            it.write(buffer, 0, count)
                            count = zis.read(buffer)
                        }
                    }
                    val time = zipEntry.time
                    if (time > 0) {
                        file.setLastModified(time)
                    }
                    zipEntry = zis.nextEntry
                }
            }
        }


        fun zip1(path: String ) {
            val inputDirectory = File(path)
            val outputZipFile = File.createTempFile("out", ".zip")
            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZipFile))).use { zos ->
                inputDirectory.walkTopDown().forEach { file ->
                    val zipFileName = file.absolutePath.removePrefix(inputDirectory.absolutePath).removePrefix("/")
                    val entry = ZipEntry( "$zipFileName${(if (file.isDirectory) "/" else "" )}")
                    zos.putNextEntry(entry)
                    if (file.isFile) {
                        file.inputStream().copyTo(zos)
                    }
                }
            }
        }


        fun zipAll(zipFile: String , directory: String ) {
            val sourceFile = File(directory)

            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use {
                zipFiles(it, sourceFile, "")
            }
        }

        private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
            val data = ByteArray(2048)
            sourceFile.listFiles()?.forEach { f ->
                if (f.isDirectory) {
                    val path = if (parentDirPath == "") {
                        f.name
                    } else {
                        parentDirPath + File.separator + f.name
                    }
                    val entry = ZipEntry(path + File.separator)
                    entry.time = f.lastModified()
                    entry.isDirectory
                    entry.size = f.length()
                    zipOut.putNextEntry(entry)
                    //Call recursively to add files within this directory
                    zipFiles(zipOut, f, path)
                } else {
                    FileInputStream(f).use { fi ->
                        BufferedInputStream(fi).use { origin ->
                            val path = parentDirPath + File.separator + f.name
                            val entry = ZipEntry(path)
                            entry.time = f.lastModified()
                            entry.isDirectory
                            entry.size = f.length()
                            zipOut.putNextEntry(entry)
                            while (true) {
                                val readBytes = origin.read(data)
                                if (readBytes == -1) {
                                    break
                                }
                                zipOut.write(data, 0, readBytes)
                            }
                        }
                    }
                }
            }
        }


    }
}
