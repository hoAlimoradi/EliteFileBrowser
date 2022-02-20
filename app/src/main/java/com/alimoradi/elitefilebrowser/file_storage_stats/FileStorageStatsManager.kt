@file:Suppress("PackageName")

/* ktlint-disable package-name */
package com.alimoradi.elitefilebrowser.file_storage_stats

interface FileStorageStatsManager {

    fun getTotalMemory(): Long

    fun getFreeMemory(): Long

    fun getBusyMemory(): Long
}
