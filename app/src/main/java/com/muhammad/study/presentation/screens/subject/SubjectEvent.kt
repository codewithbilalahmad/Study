package com.muhammad.study.presentation.screens.subject

import androidx.compose.ui.graphics.*
import com.muhammad.study.domain.model.*

sealed class SubjectEvent {
    data object UpdateSubject : SubjectEvent()
    data object DeleteSubject : SubjectEvent()
    data object DeleteSession : SubjectEvent()
    data object UpdateProgress : SubjectEvent()
    data class OnTaskIsCompleteChange(val task : Task) : SubjectEvent()
    data class OnSubjectCardColorChange(val colors : List<Color>) : SubjectEvent()
    data class OnSubjectNameChange(val name : String) : SubjectEvent()
    data class OnSubjectStudyHoursChange(val hours : String) : SubjectEvent()
    data class OnDeleteSession(val session : Session) : SubjectEvent()
}