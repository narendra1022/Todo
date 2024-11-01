package com.example.todolist.Presentation.States

import androidx.compose.material3.SnackbarDuration
import com.example.todolist.Data.Models.TaskEntity

sealed class TaskEvent {
    data class AddTask(val task: TaskEntity) : TaskEvent()
    data class EditTask(val task: TaskEntity) : TaskEvent()
    data class DeleteTask(val task: TaskEntity) : TaskEvent()
    data class ReorderTasks(val fromIndex: Int, val toIndex: Int) : TaskEvent()
}

sealed class SnackbarEvent {
    data class ShowSnackbar(
        val message: String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : SnackbarEvent()

    data object NavigateUp : SnackbarEvent()
}