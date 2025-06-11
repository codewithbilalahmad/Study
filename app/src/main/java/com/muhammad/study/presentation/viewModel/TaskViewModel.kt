package com.muhammad.study.presentation.viewModel

import android.annotation.SuppressLint
import androidx.compose.material3.*
import androidx.lifecycle.*
import com.muhammad.study.domain.model.*
import com.muhammad.study.domain.repository.*
import com.muhammad.study.presentation.screens.task.*
import com.muhammad.study.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.*

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects()
    ) { state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TaskState())
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEvent = _snackbarEventFlow.asSharedFlow()
    fun onEvent(event: TaskEvent) {
        when (event) {
            TaskEvent.DeleteTask -> deleteTask()
            is TaskEvent.OnDateChange -> {
                _state.update { it.copy(dueDate = event.millis) }
            }

            is TaskEvent.OnDescriptionChange -> {
                _state.update { it.copy(description = event.description) }
            }

           is TaskEvent.OnIsCompleteChange -> {
                updateTask(event.taskId)
            }

            is TaskEvent.OnPriorityChange -> {
                _state.update { it.copy(priority = event.priority) }
            }

            is TaskEvent.OnRelatedSubjectSelected -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is TaskEvent.OnTitleChange -> {
                _state.update { it.copy(title = event.title) }
            }

            TaskEvent.SaveTask ->{
                saveTask()
                _state.update { it.copy(title = "", description = "", relatedToSubject = null, dueDate = null) }
            }
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                val currentTaskId = state.value.currentTaskId
                if (currentTaskId != null) {
                    withContext(Dispatchers.IO) {
                        taskRepository.deleteTask(currentTaskId)
                    }
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Task Deleted Successfully")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Could not delete task")
                )
            }
        }
    }

    @SuppressLint("NewApi")
    private fun saveTask() {
        viewModelScope.launch {
            val state = _state.value
            if (state.subjectId == null || state.relatedToSubject == null) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Please select subject related to task")
                )
                return@launch
            }
            try {
                taskRepository.upsertTask(
                    task = Task(
                        title = state.title,
                        description = state.description,
                        taskId = state.currentTaskId,
                        isComplete = state.isTaskComplete,
                        dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                        relatedToSubject = state.relatedToSubject,
                        priority = state.priority.value,
                        taskSubjectId = state.subjectId
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Task Saved Successfully"
                    )
                )
                _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not save task",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    fun fetchTask(id: Long) {
        viewModelScope.launch {
            taskRepository.getTaskById(id)?.let { task ->
                _state.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        dueDate = task.dueDate,
                        isTaskComplete = task.isComplete,
                        relatedToSubject = task.relatedToSubject,
                        priority = Priority.fromInt(task.priority),
                        subjectId = task.taskSubjectId, currentTaskId = task.taskId
                    )
                }
            }
        }
    }

    fun fetchSubject(id: Long) {
        viewModelScope.launch {
            subjectRepository.getSubjectById(id)?.let { subject ->
                _state.update {
                    it.copy(subjectId = subject.subjectId, relatedToSubject = subject.name)
                }
            }
        }
    }

    private fun updateTask(taskId : Long) {
        viewModelScope.launch {
            try {
                taskRepository.getTaskById(taskId = taskId)?.let {task ->
                    taskRepository.upsertTask(task = task.copy(isComplete = !task.isComplete))
                    _state.update { it.copy(isTaskComplete = task.isComplete) }
                    if (task.isComplete) {
                        _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Saved in upcoming tasks!"))
                    } else {
                        _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Saved in completed tasks!"))
                    }
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Could not update Task!"))
            }
        }
    }
}