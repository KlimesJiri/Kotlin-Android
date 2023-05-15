package com.example.audiorecorder

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class DetailActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvFilename: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvTimestamp: TextView

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var delay = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val filename = intent.getStringExtra("filename")
        val duration = intent.getStringExtra("duration")
        val timeStamp = intent.getStringExtra("timeStamp")

        toolbar = findViewById(R.id.toolbar)
        tvFilename = findViewById(R.id.tvFilename)
        tvDuration = findViewById(R.id.tvDuration)
        tvTimestamp = findViewById(R.id.tvTimestamp)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvFilename.text = "The name of recording: $filename"
        tvDuration.text = "Recording length: $duration"
        tvTimestamp.text = "The Date of recording is: $timeStamp"

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            handler.postDelayed(runnable, delay)
        }

    }

}