package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

    private var selectedURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.toolbar)
        notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(CHANNEL_ID, CHANNEL_NAME)

        mainBinding.customButton.setOnClickListener {
            mainBinding.customButton.changeState(ButtonState.Loading)
            download()
        }

        mainBinding.radioGroup.setOnCheckedChangeListener { _, i ->
            selectedURL = when (i) {
                R.id.glide_option -> GLIDE_URL
                R.id.udacity_option -> UDACITY_URL
                R.id.retrofit_option -> RETROFIT_URL
                else -> null
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID){
                val result: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                if(result.moveToFirst()){
                    val downloadStatus: Int =
                        result.getInt(result.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    if(downloadStatus != DownloadManager.STATUS_SUCCESSFUL){
                        Toast.makeText(applicationContext, "Download failed", Toast.LENGTH_LONG).show()
                    } else{
                        notificationManager.sendNotification(applicationContext, CHANNEL_ID, "","")
                    }
                }
                mainBinding.customButton.changeState(ButtonState.Completed)
            }
        }
    }

    private fun download() {
        selectedURL?.let {
            val request =
                DownloadManager.Request(Uri.parse(selectedURL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
            ?: run{
                Toast.makeText(
                    applicationContext,
                    "Please select the file to download",
                    Toast.LENGTH_LONG
                ).show()

                mainBinding.customButton.changeState(ButtonState.Completed)

            }

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"

        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"

        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private const val CHANNEL_ID = "download_channel"
        private const val CHANNEL_NAME = "Downloads"

    }
}