package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintAttribute
import com.udacity.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var notificationManager: NotificationManager

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Download Result Details"
        notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager

        binding.fileNameResult.text = intent.getStringExtra("fileName")

        if(intent.getStringExtra("result").equals(DownloadResults.PASSED.description)){
            binding.statusResult.text = DownloadResults.PASSED.description
        } else {
            binding.statusResult.text = DownloadResults.FAILED.description
            binding.statusResult.setTextColor(Color.RED)

        }

        notificationManager.cancel()

        binding.fab.setOnClickListener {
            Intent(this, MainActivity::class.java).apply{
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
            }
        }


    }
}
