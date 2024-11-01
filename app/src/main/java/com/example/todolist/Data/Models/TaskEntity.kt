package com.example.todolist.Data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.Domain.Model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val order: Int = 0,
    val dateAdded: Long = System.currentTimeMillis() // Add date field
)