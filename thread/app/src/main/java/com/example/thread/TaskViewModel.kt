package com.example.thread

import android.app.Application
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val _taskState = MutableLiveData<TaskState>(TaskState.Idle)
    val taskState: LiveData<TaskState> = _taskState

    private var backgroundThread: Thread? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var mediaPlayer: MediaPlayer? = null

    fun startTask() {
        cancelTaskInternal()

        _taskState.value = TaskState.Preparing
        startMusic()

        backgroundThread = Thread {
            try {
                // Phase 1: 準備階段 (Preparing...)
                Thread.sleep(1500)

                if (Thread.currentThread().isInterrupted) {
                    return@Thread
                }

                // Phase 2: 開始背景工作 (Working... 0% ~ 100%)
                for (p in 0..100) {
                    if (Thread.currentThread().isInterrupted) {
                        return@Thread
                    }

                    mainHandler.post {
                        _taskState.value = TaskState.Working(p)
                    }

                    Thread.sleep(60)
                }

                if (!Thread.currentThread().isInterrupted) {
                    mainHandler.post {
                        _taskState.value = TaskState.Finished
                        stopMusic()
                    }
                }
            } catch (_: InterruptedException) {
                mainHandler.post {
                    _taskState.value = TaskState.Canceled
                    stopMusic()
                }
            }
        }
        backgroundThread?.start()
    }

    fun cancelTask() {
        if (backgroundThread?.isAlive == true) {
            cancelTaskInternal()
            _taskState.value = TaskState.Canceled
            stopMusic()
        }
    }

    private fun startMusic() {
        try {
            stopMusic()

            // Load background_music.mp3 from res/raw/background_music.mp3
            mediaPlayer = MediaPlayer.create(getApplication(), R.raw.background_music)

            // Fallback to system ringtone/notification if raw audio fails to initialize
            if (mediaPlayer == null) {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

                if (uri != null) {
                    mediaPlayer = MediaPlayer.create(getApplication(), uri)
                }
            }

            mediaPlayer?.apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMusic() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }

    private fun cancelTaskInternal() {
        backgroundThread?.interrupt()
        backgroundThread = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelTaskInternal()
        stopMusic()
    }
}
