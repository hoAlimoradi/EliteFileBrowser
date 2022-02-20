package com.alimoradi.elitefilebrowser.steganography

data class OperationStatistics(
    var totalTimeInMs: Long,
    var operationInMs: Long,
    var imageProcessingInMs: Long = totalTimeInMs - operationInMs,
)
