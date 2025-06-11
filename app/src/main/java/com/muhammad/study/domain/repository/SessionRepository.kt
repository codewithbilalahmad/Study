package com.muhammad.study.domain.repository

import com.muhammad.study.domain.model.*
import kotlinx.coroutines.flow.*

interface SessionRepository {
    suspend fun insertSession(session : Session)
    suspend fun deleteSession(session: Session)
    fun getAllSessions() : Flow<List<Session>>
    fun getRecentFiveSessions() : Flow<List<Session>>
    fun getRecentTenSessionsForSubject(subjectId : Long) : Flow<List<Session>>
    fun getTotalSessionsDuration() : Flow<Long>
    fun getTotalSessionsDurationBySubject(subjectId: Long) : Flow<Long>
}