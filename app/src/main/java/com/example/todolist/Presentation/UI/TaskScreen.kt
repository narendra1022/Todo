package com.example.todolist.Presentation.UI

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolist.Data.Models.TaskEntity
import com.example.todolist.Presentation.Components.ConfirmationDialog
import com.example.todolist.Presentation.Components.EmptyStateUI
import com.example.todolist.Presentation.Components.SlideState
import com.example.todolist.Presentation.Components.TaskDetailedDailog
import com.example.todolist.Presentation.Components.TaskDialog
import com.example.todolist.Presentation.Components.TaskItem
import com.example.todolist.Presentation.Components.dragToReorder
import com.example.todolist.Presentation.States.SnackbarEvent
import com.example.todolist.Presentation.States.TaskEvent
import com.example.todolist.Presentation.Viewmodels.TaskViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

private val itemHeightDp = 105.dp
private var itemHeightPx = 0

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel()
) {

    val state by viewModel.taskState.collectAsStateWithLifecycle()

    var isDialogOpen by remember { mutableStateOf(false) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var currentTask by remember { mutableStateOf<TaskEntity?>(null) }
    var isConfirmDeleteDialogOpen by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") } // Add search query state
    val filteredTasks = remember(searchQuery, state.tasks) {
        state.tasks.filter { task ->
            task.title.contains(searchQuery, ignoreCase = true) || task.description.contains(
                searchQuery,
                ignoreCase = true
            )
        }
    } // Filter the tasks based on search query

    val snackbarEvent: SharedFlow<SnackbarEvent> = viewModel.snackbarEventFlow
    val snackBarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()
    val isPlaced = remember { mutableStateOf(false) }
    val currentIndex = remember { mutableIntStateOf(-1) }
    val destinationIndex = remember { mutableIntStateOf(0) }
    val slideStates = remember {
        mutableStateMapOf<TaskEntity, SlideState>()
            .apply {
                state.tasks.associateWith { SlideState.NONE }.also { putAll(it) }
            }
    }

    LaunchedEffect(isPlaced.value) {
        if (isPlaced.value) {
            launch {
                if (currentIndex.intValue != destinationIndex.intValue) {
                    Collections.swap(state.tasks, currentIndex.intValue, destinationIndex.intValue)
                    slideStates.apply {
                        state.tasks.associateWith { SlideState.NONE }.also { putAll(it) }
                    }
                }
                isPlaced.value = false
            }
        }
    }

    with(LocalDensity.current) {
        itemHeightPx = itemHeightDp.toPx().toInt()
    }

    // Collect SharedFlow for Snackbar messages
    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackBarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .background(Color.White)
            .animateContentSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .animateContentSize()
                .padding(paddingValues)
        ) {

            // Search TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Tasks", style = TextStyle(color = Color.Black)) },
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Task List",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 26.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 10.dp, start = 8.dp)
                        .align(Alignment.CenterVertically)
                )

                FloatingActionButton(
                    onClick = {
                        currentTask = null
                        isEditMode = false
                        isAddDialogOpen = true
                    },
                    modifier = Modifier
                        .padding(bottom = 10.dp, end = 5.dp)
                        .height(37.dp)
                        .width(59.dp),
                    containerColor = Color(0xFFB0C5DD)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = Color.Black
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {

                if (state.tasks.isEmpty()) {
                    item { EmptyStateUI() }
                }

                items(state.tasks.size) { idx ->

                    val myItem = state.tasks.getOrNull(idx) ?: return@items

                    val slideState = slideStates[myItem] ?: SlideState.NONE

                    val verticalTranslation by animateIntAsState(
                        targetValue = when (slideState) {
                            SlideState.UP -> -itemHeightPx
                            SlideState.DOWN -> itemHeightPx
                            else -> 0
                        },
                        label = "drag_to_reorder_vertical_translation"
                    )

                    key(myItem) {
                        TaskItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp)
                                .padding(horizontal = 5.dp)
                                .dragToReorder(
                                    item = myItem,
                                    itemList = state.tasks,
                                    itemHeight = itemHeightPx,
                                    updateSlideState = { param: TaskEntity, state: SlideState ->
                                        slideStates[param] = state
                                    },
                                    onStartDrag = { index -> currentIndex.intValue = index },
                                    onStopDrag = { currIndex: Int, destIndex: Int ->
                                        isPlaced.value = true
                                        currentIndex.intValue = currIndex
                                        destinationIndex.intValue = destIndex

                                    }
                                )
                                .offset { IntOffset(0, verticalTranslation) },
                            task = myItem,
                            onEditClick = {
                                currentTask = myItem
                                isEditMode = true
                                isDialogOpen = true
                            },
                            onDeleteClick = {
                                currentTask = myItem
                                isConfirmDeleteDialogOpen = true
                            },
                            onClick = {
                                currentTask = myItem
                                isEditMode = false
                                isDialogOpen = true
                            }
                        )
                    }
                }
            }

            if (isAddDialogOpen || (isDialogOpen && isEditMode)) {
                TaskDialog(
                    isEditMode = isEditMode,
                    task = currentTask,
                    onDismiss = {
                        isAddDialogOpen = false
                        isDialogOpen = false
                    },
                    onSubmit = { task ->
                        if (task.title.length < 3 || task.description.length < 3) {
                            viewModel.triggerSnackbar("Title and description must be at least 3 characters long.")
                        } else {
                            if (isEditMode) {
                                viewModel.onEvent(TaskEvent.EditTask(task))
                            } else {
                                viewModel.onEvent(TaskEvent.AddTask(task))
                            }
                            isAddDialogOpen = false
                            isDialogOpen = false
                        }
                    }
                )
            }

            if (isConfirmDeleteDialogOpen && currentTask != null) {
                ConfirmationDialog(
                    task = currentTask!!,
                    onConfirm = {
                        viewModel.onEvent(TaskEvent.DeleteTask(currentTask!!))
                        isConfirmDeleteDialogOpen = false
                    },
                    onDismiss = { isConfirmDeleteDialogOpen = false }
                )
            }

            if (currentTask != null && !isEditMode && isDialogOpen) {
                TaskDetailedDailog(
                    task = currentTask!!,
                    onDismiss = { isDialogOpen = false }
                )
            }
        }
    }
}
