package com.muhammad.study.domain.model

import androidx.room.*

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId : Long?=null,
    val title : String,
    val description : String,
    val dueDate : Long,
    val priority : Int,
    val relatedToSubject : String,
    val isComplete : Boolean,
    val taskSubjectId : Long
)