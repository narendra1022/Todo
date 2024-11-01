package com.example.todolist.Presentation.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.todolist.Domain.Model.Task
import com.example.todolist.Domain.UseCases.TaskUseCases
import com.example.todolist.Presentation.States.SnackbarEvent
import com.example.todolist.Presentation.States.TaskEvent
import com.example.todolist.Presentation.States.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val _taskState = MutableStateFlow(TaskState())
    val taskState: StateFlow<TaskState> = _taskState.asStateFlow()

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskUseCases.getAllTasks().collect { tasks ->
                _taskState.update { it.copy(tasks = tasks) }
            }
        }
    }

    fun onEvent(event: TaskEvent) {
        viewModelScope.launch {
            when (event) {
                is TaskEvent.AddTask -> {
                    taskUseCases.insertTask(event.task)
                    showSnackbar("Task added successfully")
                }

                is TaskEvent.EditTask -> {
                    taskUseCases.updateTask(event.task)
                    showSnackbar("Task updated successfully")
                }

                is TaskEvent.DeleteTask -> {
                    taskUseCases.deleteTask(event.task)
                    showSnackbar("Task deleted successfully")
                }

                is TaskEvent.ReorderTasks -> {
                    reorderTasks(event.fromIndex, event.toIndex)
                    showSnackbar("Reordered")
                }
            }
        }
    }

    private fun reorderTasks(fromIndex: Int, toIndex: Int) {
        val tasks = _taskState.value.tasks.toMutableList()
        val movedTask = tasks.removeAt(fromIndex)
        tasks.add(toIndex, movedTask)

        viewModelScope.launch {
            tasks.forEachIndexed { index, task ->
                taskUseCases.updateTaskOrder(task.id, index)
            }
            _taskState.update { it.copy(tasks = tasks) }
        }
    }

    private suspend fun showSnackbar(message: String) {
        _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message))
    }

    fun triggerSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message))
        }
    }
}
