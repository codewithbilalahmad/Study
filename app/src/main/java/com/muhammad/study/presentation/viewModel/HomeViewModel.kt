package com.muhammad.study.presentation.viewModel

import androidx.compose.material3.*
import androidx.compose.ui.graphics.*
import androidx.lifecycle.*
import com.muhammad.study.domain.model.*
import com.muhammad.study.domain.repository.*
import com.muhammad.study.presentation.screens.home.*
import com.muhammad.study.utils.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

class HomeViewModel(
    private val subjectRespository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = combine(
        _state,
        subjectRespository.getTotalSubjectCount(),
        subjectRespository.getTotalGoalHours(),
        subjectRespository.getAllSubjects(),
        sessionRepository.getTotalSessionsDuration()
    ) { state, subjectCount, goalHours, subjects, totalSessionDuration ->
        state.copy(
            totalGoalStudyHours = goalHours,
            totalSubjectCount = subjectCount,
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toHours()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeState())
    val tasks = taskRepository.getAllUpcomingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    val recentSessions = sessionRepository.getRecentFiveSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackEventFlow = _snackbarEventFlow.asSharedFlow()
    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.DeleteSession -> {
                deleteSession()
            }
            is HomeEvent.OnDeleteSession -> {
                _state.update { it.copy(session = event.session) }
            }
            is HomeEvent.OnGoalStudyHoursChange -> {
                _state.update { it.copy(goalStudyHours = event.hours) }
            }
            is HomeEvent.OnSubjectCardColorChange ->{
                _state.update { it.copy(subjectCardColors = event.colors) }
            }
            is HomeEvent.OnSubjectNameChange -> {
                _state.update { it.copy(subjectName = event.name) }
            }
            is HomeEvent.OnTaskCompleteChange -> {
                    updateTask(event.task)
            }
            HomeEvent.SaveSubject ->{
                saveSubject()
            }

            HomeEvent.OnToggleShowAddSubjectDialog -> {
                _state.update { it.copy(showAddSubjectDialog = !state.value.showAddSubjectDialog) }
            }
        }
    }
    private fun updateTask(task : Task){
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task =  task.copy(isComplete = !task.isComplete)
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Saved in completed tasks.")
                )
            } catch (e : Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not update task", duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
    private fun saveSubject() {
        viewModelScope.launch {
            try {
                when{
                    state.value.subjectName.isEmpty() ->{
                        _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Please enter subject name."))
                    }
                    state.value.goalStudyHours.isEmpty() ->{
                        _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Please enter subject goal study hours."))
                    }
                    else ->{
                        subjectRespository.upsertSubject(
                            subject = Subject(
                                name = state.value.subjectName,
                                goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                                colors = state.value.subjectCardColors.map { it.toArgb() }
                            )
                        )
                        _state.update {
                            it.copy(
                                subjectName = "",
                                goalStudyHours = "",
                                subjectCardColors = Subject.subjectCardColors.random()
                            )
                        }
                        _snackbarEventFlow.emit(
                            SnackbarEvent.ShowSnackbar(message = "Subject saved successfully.")
                        )
                        onEvent(HomeEvent.OnToggleShowAddSubjectDialog)
                    }
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not save subject!",
                        duration = SnackbarDuration.Long
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
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Session deleted successfully")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Could not delete session",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
}