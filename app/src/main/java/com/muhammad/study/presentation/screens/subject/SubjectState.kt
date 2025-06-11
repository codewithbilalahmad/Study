package com.muhammad.study.presentation.screens.subject

import androidx.compose.ui.graphics.*
import com.muhammad.study.domain.model.*

data class SubjectState(
    val currentSubjectId : Long?=null,
    val subjectName : String = "",
    val goalStudyHours : String = "",
    val subjectCardColors : List<Color> = Subject.subjectCardColors.random(),
    val studiedHours : Float = 0f,
    val progress : Float = 0f,
    val recentSessions : List<Session> = emptyList(),
    val upcomingTasks : List<Task> = emptyList(),
    val completedTasks : List<Task> = emptyList(),
    val session : Session?=null
)
