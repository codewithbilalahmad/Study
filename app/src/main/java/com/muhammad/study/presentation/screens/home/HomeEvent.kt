package com.muhammad.study.presentation.screens.home

import androidx.compose.ui.graphics.*
import com.muhammad.study.domain.model.*

sealed class HomeEvent {
    data object SaveSubject : HomeEvent()
    data object DeleteSession : HomeEvent()
    data class OnDeleteSession(val session: Session) : HomeEvent()
    data class OnTaskCompleteChange(val task: Task) : HomeEvent()
    data class OnSubjectCardColorChange(val colors: List<Color>) : HomeEvent()
    data class OnSubjectNameChange(val name: String) : HomeEvent()
    data class OnGoalStudyHoursChange(val hours: String) : HomeEvent()
    data object OnToggleShowAddSubjectDialog : HomeEvent()
}