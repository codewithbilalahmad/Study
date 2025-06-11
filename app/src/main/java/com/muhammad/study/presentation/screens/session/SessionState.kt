package com.muhammad.study.presentation.screens.session

import com.muhammad.study.domain.model.*

data class SessionState(
    val subjects : List<Subject> = emptyList(),
    val sessions : List<Session> = emptyList(),
    val relatedToSubject : String?=null,
    val subjectId : Long?=null,
    val session : Session?=null
)
