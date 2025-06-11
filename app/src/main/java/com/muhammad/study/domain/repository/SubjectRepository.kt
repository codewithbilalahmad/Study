package com.muhammad.study.domain.repository

import com.muhammad.study.domain.model.*
import kotlinx.coroutines.flow.*

interface SubjectRepository{
    suspend fun upsertSubject(subject: Subject)
    fun getTotalSubjectCount() : Flow<Int>
    fun getTotalGoalHours() : Flow<Float>
    suspend fun deleteSubject(subjectId : Long)
    suspend fun getSubjectById(subjectId : Long) : Subject?
    fun getAllSubjects() : Flow<List<Subject>>
}