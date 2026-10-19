package com.example.thread

sealed class TaskState {
    object Idle : TaskState()
    object Preparing : TaskState()
    data class Working(val progress: Int) : TaskState()
    object Finished : TaskState()
    object Canceled : TaskState()
}
