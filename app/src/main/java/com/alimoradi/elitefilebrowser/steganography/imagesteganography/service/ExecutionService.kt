package com.alimoradi.elitefilebrowser.steganography.imagesteganography.service

import java.util.concurrent.Executors

open class ExecutionService {

    /**
     * Executes the given runnable asynchronous in a background thread
     *
     * @param runnable the code to execute in background
     */
    open fun executeInBackground(runnable: Runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable)
    }

    companion object {
        private val THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(2)
    }
}