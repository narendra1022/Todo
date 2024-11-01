package com.example.todolist.Domain.UseCases

import com.example.todolist.Data.Models.TaskEntity
import com.example.todolist.Data.Repositories.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskUseCases(private val taskRepository: TaskRepository) {

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskRepository.getAllTasks()
    }

    suspend fun insertTask(task: TaskEntity) {
        taskRepository.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskRepository.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskRepository.deleteTask(task)
    }

    suspend fun updateTaskOrder(taskId: Int, newOrder: Int) {
        taskRepository.updateTaskOrder(taskId, newOrder)
    }
}
