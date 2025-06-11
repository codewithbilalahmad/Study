package com.muhammad.study.domain.model

import androidx.room.*

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long? = null,
    val relatedToSubject: String,
    val date: Long,
    val duration: Long,
    val sessionSubjectId: Long
)
