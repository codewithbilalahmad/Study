package com.muhammad.study.presentation.viewModel

import androidx.compose.material3.*
import androidx.compose.ui.graphics.*
import androidx.lifecycle.*
import com.muhammad.study.domain.model.*
import com.muhammad.study.domain.repository.*
import com.muhammad.study.presentation.screens.subject.*
import com.muhammad.study.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SubjectViewModel(
    private val subjectId: Long,
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(subjectId),
        taskRepository.getCompletedTasksForSubject(subjectId),
        sessionRepository.getRecentTenSessionsForSubject(subjectId),
        sessionRepository.getTotalSessionsDurationBySubject(subjectId)
    ) { state, upcomingTasks, completedTasks, recentTenSessions, sessionDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTasks,
            recentSessions = recentTenSessions,
            studiedHours = sessionDuration.toHours()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SubjectState())
    private val _snackbarFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarFlow = _snackbarFlow.asSharedFlow()
    fun onEvent(event: SubjectEvent) {
        when (event) {
            SubjectEvent.DeleteSession -> deleteSession()
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSession -> {
                _state.update { it.copy(session = event.session) }
            }

            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update { it.copy(subjectCardColors = event.colors) }
            }

            is SubjectEvent.OnSubjectNameChange -> {
                _state.update { it.copy(subjectName = event.name) }
            }

            is SubjectEvent.OnSubjectStudyHoursChange -> {
                _state.update { it.copy(goalStudyHours = event.hours) }
            }

            is SubjectEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }

            SubjectEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f, 1f)
                    )
                }
            }

            SubjectEvent.UpdateSubject -> updateSubject()
        }
    }

    init {
        fetchSubject(subjectId)
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() })
                )
                _snackbarFlow.emit(SnackbarEvent.ShowSnackbar(message = "Subject updated successfully"))
            } catch (e: Exception) {
                _snackbarFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not update subject.",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchSubject(id: Long) {
        viewModelScope.launch {
            subjectRepository.getSubjectById(id)?.let { subject ->
                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { colors -> Color(colors) },
                        currentSubjectId = subject.subjectId
                    )
                }
            }
        }
    }

    private fun deleteSubject() {
        viewModelScope.launch {
            try {
                val currentSubjectId = state.value.currentSubjectId
                if (currentSubjectId != null) {
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                    }
                    _snackbarFlow.emit(SnackbarEvent.ShowSnackbar(message = "Subject deleted successfully"))
                    _snackbarFlow.emit(SnackbarEvent.NavigateUp)
                } else {
                    _snackbarFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "No Subject to delete")
                    )
                }
            } catch (e: Exception) {
                _snackbarFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not delete subject",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task = task.copy(isComplete = !task.isComplete))
                if (task.isComplete) {
                    _snackbarFlow.emit(SnackbarEvent.ShowSnackbar(message = "Saved in upcoming tasks."))
                } else {
                    _snackbarFlow.emit(SnackbarEvent.ShowSnackbar(message = "Saved in completed task."))
                }
            } catch (e: Exception) {
                _snackbarFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not update task.", duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let { session ->
                    sessionRepository.deleteSession(session)
                    _snackbarFlow.emit(SnackbarEvent.ShowSnackbar(message = "Session deleted successfully"))
                }
            } catch (e: Exception) {
                _snackbarFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not delete session",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
}
