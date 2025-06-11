package com.muhammad.study.data.local.dao

import androidx.room.*
import com.muhammad.study.domain.model.*
import kotlinx.coroutines.flow.*

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: Session)
    @Delete
    suspend fun deleteSession(session: Session)
    @Query("SELECT * FROM SESSION")
    fun getAllSessions() : Flow<List<Session>>
    @Query("SELECT * FROM Session WHERE sessionSubjectId =:subjectId")
    fun getRecentSessionsForSubject(subjectId : Long) : Flow<List<Session>>
    @Query("SELECT COALESCE(SUM(duration), 0) FROM SESSION")
    fun getTotalSessionDuration() : Flow<Long>
    @Query("SELECT COALESCE(SUM(duration), 0) FROM SESSION WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionsDurationBySubject(subjectId: Long) : Flow<Long>
    @Query("DELETE FROM Session WHERE sessionSubjectId = :subjectId")
    fun deleteSessionBySubjectId(subjectId: Long)
}