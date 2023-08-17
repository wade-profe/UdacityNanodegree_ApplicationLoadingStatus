package com.udacity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // TODO cancel notification when pressing action but calling cancel from details activity
        // TODO Implement design for detail tab
        // Set FAB to return to main activity
        // Implement motion layout animations

        binding.text1.text = intent.getStringExtra("fileName")
        binding.text2.text = intent.getStringExtra("result")

    }
}
