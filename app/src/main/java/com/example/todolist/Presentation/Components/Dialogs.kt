package com.example.todolist.Presentation.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.Data.Models.TaskEntity


// Function to apply a gradient background to dialogs
fun Modifier.gradientBackground(): Modifier {
    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF93A5CF),
                Color(0xFFE4EfE9)
            )
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun TaskDetailedDailog(
    task: TaskEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = task.title,
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .gradientBackground()
                    .padding(26.dp)
            ) {
                Text(
                    "Description :\n",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontSize = 20.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontSize = 20.sp, color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.Transparent
    )
}

@Composable
fun TaskDialog(
    isEditMode: Boolean,
    task: TaskEntity?,
    onDismiss: () -> Unit,
    onSubmit: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditMode) "Edit Task" else "Add Task",
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .gradientBackground()
                    .padding(26.dp)
            ) {


                TextField(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            "Title\n", color = Color.DarkGray, fontSize = 15.sp
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray,
                        focusedIndicatorColor = Color.Black,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    textStyle = TextStyle(color = Color.Black, fontSize = 20.sp),
                )
                TextField(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            "Description\n", color = Color.DarkGray, fontSize = 15.sp
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray,
                        focusedIndicatorColor = Color.Black,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    textStyle = TextStyle(color = Color.Black, fontSize = 20.sp),
                )
            }
        },
        confirmButton = {
            TextButton(

                onClick = {
                    val newTask = TaskEntity(
                        id = task?.id ?: 0,
                        title = title,
                        description = description,
                        dateAdded = System.currentTimeMillis()
                    )
                    onSubmit(newTask)
                }
            ) {
                Text(
                    "Save", fontSize = 20.sp, color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Cancel", fontSize = 20.sp, color = Color.White
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.Transparent
    )
}

@Composable
fun ConfirmationDialog(
    task: TaskEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Delete",
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete task : \"${task.title}\" ?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .gradientBackground()
                    .padding(26.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes", fontSize = 20.sp, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No", fontSize = 20.sp, color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.Transparent
    )
}
