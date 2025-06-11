package com.muhammad.study.presentation.screens.task

import com.muhammad.study.domain.model.*
import com.muhammad.study.utils.*

data class TaskState(
    val title : String = "",
    val description : String = "",
    val dueDate : Long?=null,
    val isTaskComplete : Boolean = false,
    val priority: Priority = Priority.LOW,
    val relatedToSubject : String?=null,
    val subjects : List<Subject> = emptyList(),
    val subjectId : Long?=null,
    val currentTaskId : Long?=null
)
