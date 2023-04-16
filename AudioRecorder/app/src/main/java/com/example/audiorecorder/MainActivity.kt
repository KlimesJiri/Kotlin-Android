package com.example.audiorecorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.github.squti.androidwaverecorder.WaveRecorder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CODE = 200

class MainActivity : AppCompatActivity(), Timer.OnTimerTickListener {

    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    private lateinit var waveRecorder: WaveRecorder

    private var dirPath = ""
    private var fileName = ""
    private var isRecording = false
    private var isPaused = false

    private lateinit var timer: Timer

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)


        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        timer = Timer(this)

        btnRecord.setOnClickListener {
            when{
                isPaused -> resumeRecorder()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }
        }

        btnList.setOnClickListener {
            //  TODO
            Toast.makeText(this, "List button", Toast.LENGTH_SHORT).show()

        }

        btnDone.setOnClickListener {
            stopRecorder()
            //Toast.makeText(this, "Record saved", Toast.LENGTH_SHORT).show()

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBG.visibility = View.VISIBLE
            filenameInput.setText(fileName)
        }

        btnCancel.setOnClickListener{
            File("$dirPath$fileName.wav").delete()
            dismiss()
        }

        btnOk.setOnClickListener{
            dismiss()
            save()
        }

        bottomSheetBG.setOnClickListener {
            File("$dirPath$fileName.wav").delete()
            dismiss ()
        }

        btnDelete.setOnClickListener {
            stopRecorder()
            File("$dirPath$fileName.wav").delete()
            Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show()
        }

        btnDelete.isClickable = false
    }

    private fun save(){
        val newFileName = filenameInput.text.toString()
        if(newFileName != fileName){
            var newFile = File("$dirPath$newFileName.wav")
            File("$dirPath$fileName.wav").renameTo(newFile)
        }
    }

    private fun dismiss(){
        bottomSheetBG.visibility = View.GONE
        hideKeyboard(filenameInput)

        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 100)
    }

    private fun hideKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun pauseRecorder(){
        waveRecorder.pauseRecording()
        isPaused = true
        btnRecord.setImageResource(R.drawable.ic_record)

        timer.pause()
    }

    private fun resumeRecorder(){
        waveRecorder.resumeRecording()
        isPaused = false
        btnRecord.setImageResource(R.drawable.ic_pause)

        timer.start()
    }

    @Suppress("DEPRECATION")
    private fun startRecording(){
        if (!permissionGranted){
             ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
             return
        }

        dirPath = "${externalCacheDir?.absolutePath}/"

        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        fileName = "audio_record_$date"

        waveRecorder = WaveRecorder("$dirPath$fileName.wav")
        waveRecorder.waveConfig.sampleRate = 44100
        waveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_STEREO
        waveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        waveRecorder.startRecording()

        btnRecord.setImageResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false

        timer.start()

        btnDelete.isClickable = true
        btnDelete.setImageResource(R.drawable.ic_delete)

        btnList.visibility = View.GONE
        btnDone.visibility = View.VISIBLE
    }

    private fun stopRecorder(){
        timer.stop()

        waveRecorder.stopRecording()

        isPaused = false
        isRecording = false

        btnList.visibility = View.VISIBLE
        btnDone.visibility = View.GONE

        btnDelete.isClickable = false
        btnDelete.setImageResource(R.drawable.ic_delete_disabled)

        btnRecord.setImageResource(R.drawable.ic_record)

        tvTimer.text = "00:00.00"
    }

    override fun onTimerTick(duration: String) {
        tvTimer.text = duration
    }

}