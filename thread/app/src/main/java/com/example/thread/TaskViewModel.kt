package com.example.thread

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {

    private val _taskState = MutableLiveData<TaskState>(TaskState.Idle)
    val taskState: LiveData<TaskState> = _taskState

    private var backgroundThread: Thread? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    fun startTask() {
        cancelTaskInternal()

        _taskState.value = TaskState.Preparing

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
                    }
                }
            } catch (_: InterruptedException) {
                mainHandler.post {
                    _taskState.value = TaskState.Canceled
                }
            }
        }
        backgroundThread?.start()
    }

    fun cancelTask() {
        if (backgroundThread?.isAlive == true) {
            cancelTaskInternal()
            _taskState.value = TaskState.Canceled
        }
    }

    private fun cancelTaskInternal() {
        backgroundThread?.interrupt()
        backgroundThread = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelTaskInternal()
    }
}
