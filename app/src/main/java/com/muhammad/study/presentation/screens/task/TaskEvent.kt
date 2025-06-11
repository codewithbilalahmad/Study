package com.muhammad.study.presentation.screens.task

import com.muhammad.study.domain.model.*
import com.muhammad.study.utils.*

sealed class TaskEvent{
    data class OnTitleChange(val title : String) : TaskEvent()
    data class OnDescriptionChange(val description : String) : TaskEvent()
    data class OnDateChange(val millis : Long?) : TaskEvent()
    data class OnPriorityChange(val priority: Priority) : TaskEvent()
    data class OnRelatedSubjectSelected(val subject : Subject) : TaskEvent()
    data class OnIsCompleteChange(val taskId : Long) : TaskEvent()
    data object SaveTask : TaskEvent()
    data object DeleteTask : TaskEvent()
}