package com.example.todolist.Data.Repositories

import com.example.todolist.Data.Local.TaskDao
import com.example.todolist.Data.Models.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun updateTaskOrder(taskId: Int, newOrder: Int) {
        taskDao.updateTaskOrder(taskId, newOrder)
    }
}
