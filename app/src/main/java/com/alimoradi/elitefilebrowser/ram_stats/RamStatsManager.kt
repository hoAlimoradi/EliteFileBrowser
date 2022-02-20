package com.alimoradi.elitefilebrowser.ram_stats

interface RamStatsManager {

    fun getTotalMemory(): Long

    fun getFreeMemory(): Long

    fun getBusyMemory(): Long
}
