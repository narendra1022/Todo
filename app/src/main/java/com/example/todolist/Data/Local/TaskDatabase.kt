package com.example.todolist.Data.Local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.Data.Models.TaskEntity

@Database(entities = [TaskEntity::class], version = 2, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
