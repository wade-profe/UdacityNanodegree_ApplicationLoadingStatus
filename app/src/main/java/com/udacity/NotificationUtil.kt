package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 1

fun NotificationManager.sendNotification(
    context: Context,
    channelId: String,
    fileName: String?,
    result: DownloadResults?
) {

    val detailsIntent = Intent(context, DetailActivity::class.java)
        .putExtra("fileName", fileName)
        .putExtra("result", result?.description)

    val detailsPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        detailsIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action = NotificationCompat.Action.Builder(
        0, context.resources.getString(R.string.notification_button), detailsPendingIntent)
        .build()

    val builder = NotificationCompat.Builder(
        context,
        channelId,
    )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(context.resources.getString(R.string.notification_title))
        .setContentText(context.resources.getString(R.string.notification_description))
        .addAction(action)

    notify(NOTIFICATION_ID, builder.build())

}

fun NotificationManager.cancel(){
    cancel(NOTIFICATION_ID)
}