package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.udacity.databinding.ActivityMainBinding


enum class DownloadResults(val description: String){
    PASSED("Successful"),
    FAILED("Fail")
}

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private var selectedURL: String? = null
    private var fileName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.toolbar)
        notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel()

        mainBinding.customButton.setOnClickListener {
            mainBinding.customButton.changeState(ButtonState.Loading)
            download()
        }

        mainBinding.radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.glide_option -> {
                    selectedURL = GLIDE_URL
                    fileName = mainBinding.glideOption.text.toString()
                }
                R.id.udacity_option -> {
                    selectedURL = UDACITY_URL
                    fileName = mainBinding.udacityOption.text.toString()
                }
                R.id.retrofit_option -> {
                    selectedURL = RETROFIT_URL
                    fileName = mainBinding.retrofitOption.text.toString()
                }
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
                        notificationManager.sendNotification(applicationContext, CHANNEL_ID, fileName,DownloadResults.FAILED)
                    } else{
                        notificationManager.sendNotification(applicationContext, CHANNEL_ID, fileName,DownloadResults.PASSED)
                    }
                }
                mainBinding.customButton.changeState(ButtonState.Completed)
                result.close()
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

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}