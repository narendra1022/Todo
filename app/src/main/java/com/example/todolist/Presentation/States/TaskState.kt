package com.example.todolist.Presentation.States

import com.example.todolist.Data.Models.TaskEntity

data class TaskState(
    val tasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = false
)

