package com.muhammad.study.presentation.screens.home

import androidx.compose.ui.graphics.*
import com.muhammad.study.domain.model.*

data class HomeState(
    val totalSubjectCount : Int = 0,
    val totalStudiedHours : Float = 0f,
    val totalGoalStudyHours : Float = 0f,
    val subjects : List<Subject> = emptyList(),
    val subjectName : String = "",
    val goalStudyHours : String = "",
    val showAddSubjectDialog : Boolean = false,
    val subjectCardColors : List<Color> = Subject.subjectCardColors.random(),
    val session : Session?=null
)