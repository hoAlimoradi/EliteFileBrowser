package com.alimoradi.file_api_android.internal

import android.util.Log
import com.alimoradi.file_api_android.internal.FileHelper
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object FileHelper {
    private const val BUFFER_SIZE = 8192 //2048;
    private val TAG = FileHelper::class.java.name.toString()
    private var parentPath = ""
    fun zip(
        sourcePath: String,
        destinationPath: String,
        destinationFileName: String,
        includeParentFolder: Boolean
    ): Boolean {
        var destinationPath = destinationPath
        File(destinationPath).mkdirs()
        val fileOutputStream: FileOutputStream
        var zipOutputStream: ZipOutputStream? = null
        try {
            if (!destinationPath.endsWith("/")) destinationPath += "/"
            val destination = destinationPath + destinationFileName
            val file = File(destination)
            if (!file.exists()) file.createNewFile()
            fileOutputStream = FileOutputStream(file)
            zipOutputStream = ZipOutputStream(BufferedOutputStream(fileOutputStream))
            if (includeParentFolder) parentPath = File(sourcePath).parent + "/" else parentPath =
                sourcePath
            zipFile(zipOutputStream, sourcePath)
        } catch (ioe: IOException) {
            Log.d(TAG, ioe.message!!)
            return false
        } finally {
            if (zipOutputStream != null) try {
                zipOutputStream.close()
            } catch (e: IOException) {
            }
        }
        return true
    }

    private fun zipFile(zipOutputStream: ZipOutputStream, sourcePath: String) {
        val files = File(sourcePath)
        val fileList = files.listFiles()
        var entryPath = ""
        var input: BufferedInputStream
        if ( !(fileList != null)) {
            println("array is null")
            return
        }
        if (fileList.size == 0) {
            println("array is empty")
            return
        }
        fileList?.let {

        }
        for (file in fileList) {
            if (file.isDirectory) {
                zipFile(zipOutputStream, file.path)
            } else {
                val data = ByteArray(BUFFER_SIZE)
                val fileInputStream = FileInputStream(file.path)
                input = BufferedInputStream(fileInputStream, BUFFER_SIZE)
                entryPath = file.absolutePath.replace(parentPath, "")
                val entry = ZipEntry(entryPath)
                zipOutputStream.putNextEntry(entry)
                var count: Int
                while (input.read(data, 0, BUFFER_SIZE).also { count = it } != -1) {
                    zipOutputStream.write(data, 0, count)
                }
                input.close()
            }
        }
    }

    fun unzip(sourceFile: String?, destinationFolder: String?): Boolean {
        var zis: ZipInputStream? = null
        try {
            zis = ZipInputStream(BufferedInputStream(FileInputStream(sourceFile)))
            var ze: ZipEntry
            var count: Int
            val buffer = ByteArray(BUFFER_SIZE)
            while (zis.nextEntry.also { ze = it } != null) {
                var fileName = ze.name
                fileName = fileName.substring(fileName.indexOf("/") + 1)
                val file = File(destinationFolder, fileName)
                val dir = if (ze.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException("Invalid path: " + dir.absolutePath)
                if (ze.isDirectory) continue
                val fout = FileOutputStream(file)
                try {
                    while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
                } finally {
                    fout.close()
                }
            }
        } catch (ioe: IOException) {
            Log.e(TAG, ioe.message!!)
            return false
        } finally {
            if (zis != null) try {
                zis.close()
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            }
        }
        return true
    }

    fun saveToFile(destinationPath: String, data: String, fileName: String) {
        try {
            File(destinationPath).mkdirs()
            val file = File(destinationPath + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileOutputStream = FileOutputStream(file, true)
            fileOutputStream.write((data + System.getProperty("line.separator")).toByteArray())
        } catch (ex: FileNotFoundException) {
            Log.d(TAG, ex.message!!)
        } catch (ex: IOException) {
            Log.d(TAG, ex.message!!)
        }
    }
}