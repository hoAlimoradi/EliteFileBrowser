package com.alimoradi.file_api

interface MediaScanner {

    fun refresh(
        path: String
    )

    fun registerListener(
        listener: RefreshListener
    )

    fun unregisterListener(
        listener: RefreshListener
    )

    interface RefreshListener {

        fun onContentChanged(
            path: String
        )
    }
}
