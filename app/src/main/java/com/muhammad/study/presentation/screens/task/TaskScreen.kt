package com.muhammad.study.presentation.screens.task

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammad.study.presentation.components.DeleteDialog
import com.muhammad.study.presentation.components.SubjectListBottomSheet
import com.muhammad.study.presentation.components.TaskCheckBox
import com.muhammad.study.presentation.components.TaskDatePicker
import com.muhammad.study.presentation.viewModel.TaskViewModel
import com.muhammad.study.utils.Priority
import com.muhammad.study.utils.SnackbarEvent
import com.muhammad.study.utils.changeMillisToDateString
import com.muhammad.study.utils.rippleClickable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.time.Instant

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(onBackClick: () -> Unit, taskId: Long, subjectId: Long) {
    val viewModel = koinInject<TaskViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarEvent = viewModel.snackbarEvent
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var taskTitleError by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.title) {
        taskTitleError = when {
            state.title.isBlank() -> "Please enter task title."
            state.title.length < 4 -> "Task title too short"
            state.title.length > 25 -> "Task title too long"
            else -> null
        }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchTask(taskId)
        viewModel.fetchSubject(subjectId)
    }
    LaunchedEffect(true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                SnackbarEvent.NavigateUp -> {
                    onBackClick()
                }

                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }
    DeleteDialog(
        showDialog = showDeleteDialog,
        title = "Delete Task?",
        bodyText = "Are you sure, you want to delete this task? " +
                "This action can not be undone.", onDismiss = {
            showDeleteDialog = false
        }, onConfirmClick = {
            viewModel.onEvent(TaskEvent.DeleteTask)
            showDeleteDialog = false
        }
    )
    TaskDatePicker(state = datePickerState, showPicker = showDatePickerDialog, onDismiss = {
        showDatePickerDialog = false
    }, onConfirmClicked = { millis ->
        viewModel.onEvent(TaskEvent.OnDateChange(millis))
        showDatePickerDialog = false
    })
    SubjectListBottomSheet(showBottomSheet = showBottomSheet, sheetState = sheetState, onDismiss = {
        showBottomSheet = false
    }, onSubjectClick = { subject ->
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) showBottomSheet = false
        }
        viewModel.onEvent(TaskEvent.OnRelatedSubjectSelected(subject))
    }, subjects = state.subjects)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
            TaskTopBar(
                isTaskExist = state.currentTaskId != null,
                isComplete = state.isTaskComplete,
                checkBoxBorderColor = state.priority.color,
                onCheckboxClick = {
                    viewModel.onEvent(TaskEvent.OnIsCompleteChange(taskId))
                }, onBackClick = onBackClick, onDeleteClick = {
                    showDeleteDialog = true
                })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(value = state.title, onValueChange = { newValue ->
                viewModel.onEvent(TaskEvent.OnTitleChange(newValue))
            }, modifier = Modifier.fillMaxWidth(), label = {
                Text(text = "Title", style = MaterialTheme.typography.bodyLarge)
            }, supportingText = {
                Text(text = taskTitleError.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }, singleLine = true, isError = taskTitleError != null && state.title.isNotBlank())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = state.description, onValueChange = { newValue ->
                viewModel.onEvent(TaskEvent.OnDescriptionChange(newValue))
            }, label = {
                Text(text = "Description", style = MaterialTheme.typography.bodyLarge)
            }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Text(text = "Due Date", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {
                    showDatePickerDialog = true
                }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(text = "Priority", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Priority.entries.forEach { priority ->
                    val isSelected = priority == state.priority
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        background = priority.color,
                        borderColor = if (isSelected) Color.White else Color.Transparent,
                        onClick = {
                            viewModel.onEvent(TaskEvent.OnPriorityChange(priority))
                        },
                        labelColor = if (priority == state.priority) Color.White else Color.White.copy(
                            0.7f
                        ), showCheck = isSelected
                    )
                }
            }
            Spacer(Modifier.height(30.dp))
            Text(text = "Related to subject", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstSubject = state.subjects.firstOrNull()?.name ?: ""
                Text(
                    text = state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {
                    showBottomSheet = true
                }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            Button(
                onClick = {
                    viewModel.onEvent(TaskEvent.SaveTask)
                },
                enabled = taskTitleError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(text = "Save", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTopBar(
    isTaskExist: Boolean,
    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit, onCheckboxClick: () -> Unit,
) {
    TopAppBar(title = {
        Text(text = "Task", style = MaterialTheme.typography.headlineSmall)
    }, navigationIcon = {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
        }
    }, actions = {
        if (isTaskExist) {
            TaskCheckBox(
                isComplete = isComplete,
                border = checkBoxBorderColor,
                onCheckBoxClick = onCheckboxClick
            )
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    })
}

@Composable
private fun PriorityButton(
    modifier: Modifier = Modifier,
    label: String,
    background: Color,
    borderColor: Color,
    labelColor: Color,
    onClick: () -> Unit,showCheck : Boolean
) {
    Row(
        modifier = modifier
            .background(background, RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .rippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if(showCheck){
            Icon(imageVector = Icons.Default.Check,contentDescription = null, tint = labelColor)
        }
        Text(text = label, style = MaterialTheme.typography.bodyLarge.copy(color = labelColor))
    }
}