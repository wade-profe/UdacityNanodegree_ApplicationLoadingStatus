package com.udacity

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 1

fun NotificationManager.sendNotification(context: Context, channelId: String,  filename: String, result: String){

    // TODO add intent and pending intent for action button with filename and result params

    val builder =NotificationCompat.Builder(
        context,
        channelId,
    )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(context.resources.getString(R.string.notification_title))
        .setContentText(context.resources.getString(R.string.notification_description))
    // TODO add action
    notify(NOTIFICATION_ID, builder.build())

}