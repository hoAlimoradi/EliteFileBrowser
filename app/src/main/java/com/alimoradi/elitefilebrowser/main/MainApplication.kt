package com.alimoradi.elitefilebrowser.main

import android.app.Application
import android.content.Context
import com.alimoradi.elitefilebrowser.audio.AudioManager
import com.alimoradi.elitefilebrowser.data.AppPreferences


/**
 * The [Application] of this project.
 */


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Setup fabric
        setupCrashlytics()

        ApplicationGraph.init(this)

        initializeAudio()
        initializeNotificationAudio()
        AppPreferences.init(this)
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)

    }

    private fun setupCrashlytics() {
        /*val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
        Fabric.with(this, crashlyticsKit)*/
    }

    companion object {

        private fun initializeAudio() {
            val audioManager = ApplicationGraph.getAudioManager()
            val audioQueueManager = ApplicationGraph.getAudioQueueManager()
            val audioManagerCompletionListener = object : AudioManager.CompletionListener {
                override fun onCompleted() {
                    val sourcePath = audioManager.getSourcePath() ?: return
                    val nextPath = audioQueueManager.next(sourcePath)
                    audioManager.reset()
                    audioManager.setSourcePath(nextPath)
                    audioManager.prepareAsync()
                }
            }
            audioManager.registerCompletionListener(audioManagerCompletionListener)
        }

        private fun initializeNotificationAudio() {
            val notificationAudioManager = ApplicationGraph.getNotificationAudioManager()
            notificationAudioManager.initialize()
        }

    }
}
