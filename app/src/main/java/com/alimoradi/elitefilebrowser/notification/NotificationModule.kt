package com.alimoradi.elitefilebrowser.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.audio.AudioManager
import com.alimoradi.elitefilebrowser.main.MainActivity
import com.alimoradi.elitefilebrowser.notification.NotificationAudioReceiver.Companion.getNotificationIntentActivity
import com.alimoradi.elitefilebrowser.notification.NotificationAudioReceiver.Companion.getNotificationIntentClose
import com.alimoradi.elitefilebrowser.notification.NotificationAudioReceiver.Companion.getNotificationIntentNext
import com.alimoradi.elitefilebrowser.notification.NotificationAudioReceiver.Companion.getNotificationIntentPause
import com.alimoradi.elitefilebrowser.notification.NotificationAudioReceiver.Companion.getNotificationIntentPlayPause
import com.alimoradi.elitefilebrowser.notification.NotificationAudioReceiver.Companion.getNotificationIntentPrevious

class NotificationModule(
    private val context: Context,
    private val audioManager: AudioManager
) {

    fun createNotificationAudioManager(): NotificationAudioManager {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val addOn = object : NotificationAudioManagerImpl.AddOn {

            override fun createNotification(
                notificationId: Int,
                fileName: String
            ) {
                val channelId = "elitefilebrowser_notification_channel_audio"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "Elite File Browser audio channel"
                    val description = "Elite File Browser audio notification channel"
                    val importance = NotificationManager.IMPORTANCE_LOW
                    val channel = NotificationChannel(channelId, name, importance)
                    channel.description = description
                    notificationManager.createNotificationChannel(channel)
                }

                val remoteViews = RemoteViews(context.packageName, R.layout.view_notification_audio)

                remoteViews.setTextViewText(R.id.view_notification_audio_title, fileName)
                remoteViews.setOnClickPendingIntent(R.id.view_notification_audio_title,
                    getNotificationIntentActivity(context))
                remoteViews.setOnClickPendingIntent(R.id.view_notification_audio_close,
                    getNotificationIntentClose(context))
                remoteViews.setOnClickPendingIntent(R.id.view_notification_audio_play_pause,
                    getNotificationIntentPlayPause(context))
                remoteViews.setOnClickPendingIntent(R.id.view_notification_audio_next,
                    getNotificationIntentNext(context))
                remoteViews.setOnClickPendingIntent(R.id.view_notification_audio_previous,
                    getNotificationIntentPrevious(context))

                NotificationManagerCompat.from(context).notify(notificationId,
                    NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_sd_storage_black_24dp)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setContent(remoteViews)
                        .setDeleteIntent(getNotificationIntentPause(context))
                        .setContentIntent(PendingIntent.getActivity(context, 0,
                            Intent(context, MainActivity::class.java),
                            PendingIntent.FLAG_UPDATE_CURRENT))

                        .build())
            }

            override fun cancelNotification(notificationId: Int) {
                NotificationManagerCompat.from(context).cancel(notificationId)
            }

            override fun pathToFileName(path: String): String {
                val ioFile = java.io.File(path)
                return ioFile.name
            }
        }
        return NotificationAudioManagerImpl(
            audioManager,
            addOn
        )
    }
}
