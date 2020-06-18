package com.motevets.simpleamplifier

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var output: String? = null
    private var audioRecorder : AudioRecord? = null
    private var audioPlayer: AudioTrack? = null
    private var audioManager: AudioManager? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    private var minBuffer: Int = 0;
    private var audioRecorderState = 0
    private var audioPlayerState = 0
    private var isRecording = false

    private val SOURCE: Int = MediaRecorder.AudioSource.MIC
    private val CHANNEL_IN: Int = AudioFormat.CHANNEL_IN_MONO
    private val CHANNEL_OUT: Int = AudioFormat.CHANNEL_OUT_MONO
    private val FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        volumeControlStream = AudioManager.MODE_IN_COMMUNICATION

        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager!!.mode = AudioManager.MODE_IN_COMMUNICATION

        val sampleRate = getSampleRate()
        minBuffer = AudioRecord.getMinBufferSize(sampleRate, CHANNEL_IN, FORMAT)

        audioRecorder = AudioRecord(SOURCE, sampleRate, CHANNEL_IN, FORMAT, minBuffer)
        audioRecorderState = audioRecorder!!.state
        val audioSessionId = audioRecorder!!.audioSessionId

        audioPlayer = AudioTrack(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
            AudioFormat.Builder().setEncoding(FORMAT).setSampleRate(sampleRate).setChannelMask(CHANNEL_OUT).build(),
            minBuffer,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE)

        audioPlayerState = audioPlayer!!.state

        button_start_recording.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
            } else {
                startRecording()
            }
        }

        button_stop_recording.setOnClickListener{
            pauseRecording()
        }

        button_pause_recording.setOnClickListener {
            pauseRecording()
        }
    }

    private fun startRecording() {
        var read = 0
        var write = 0

        if(audioRecorderState == AudioRecord.STATE_INITIALIZED && audioPlayerState == AudioTrack.STATE_INITIALIZED) {
            audioRecorder!!.startRecording();
            audioPlayer!!.play();
            isRecording = true;
            Log.d("Record", "Recording...");
        }

        while(isRecording) {
            var audioData = ShortArray(minBuffer)
            read = audioRecorder!!.read(audioData, 0, minBuffer)
            Log.d("Record", "Read: $read");
            write = audioPlayer!!.write(audioData, 0, read)
            Log.d("Record", "Write: $write")
        }
    }

    private fun pauseRecording() {
        isRecording = false
        if(audioRecorder!!.recordingState == AudioRecord.RECORDSTATE_RECORDING)
            audioRecorder!!.stop()
        Log.d("Record", "Stopping...")
        if(audioPlayer!!.playState == AudioTrack.PLAYSTATE_PLAYING)
            audioPlayer!!.stop()
        Log.d("Player", "Stopping...")

    }

    private fun getSampleRate(): Int {
        //Find a sample rate that works with the device
        for (rate in intArrayOf(8000, 11025, 16000, 22050, 44100, 48000)) {
            val buffer = AudioRecord.getMinBufferSize(rate, CHANNEL_IN, FORMAT)
            if (buffer > 0) return rate
        }
        return -1
    }
}

//package com.motevets.simpleamplifier
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//}