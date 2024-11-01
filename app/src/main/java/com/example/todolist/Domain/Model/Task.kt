package com.example.todolist.Domain.Model

data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val order: Int = 0,
    val dateAdded: Long = System.currentTimeMillis() // Add date field
)

