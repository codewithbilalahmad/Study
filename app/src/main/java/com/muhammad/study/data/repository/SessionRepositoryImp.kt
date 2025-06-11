package com.muhammad.study.data.repository

import com.muhammad.study.data.local.dao.*
import com.muhammad.study.domain.model.*
import com.muhammad.study.domain.repository.*
import kotlinx.coroutines.flow.*

class SessionRepositoryImp(private val sessionDao: SessionDao) : SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { sessions ->
            sessions.sortedByDescending { it.date }
        }
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map {sessions ->
            sessions.sortedByDescending { it.date }
        }.take(5)
    }

    override fun getRecentTenSessionsForSubject(subjectId: Long): Flow<List<Session>> {
        return sessionDao.getAllSessions().map {sessions ->
            sessions.sortedByDescending { it.date }
        }.take(10)
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration().map { it ?: 0L }
    }

    override fun getTotalSessionsDurationBySubject(subjectId: Long): Flow<Long> {
        return sessionDao.getTotalSessionsDurationBySubject(subjectId)
    }
}