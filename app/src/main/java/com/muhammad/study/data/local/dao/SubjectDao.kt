package com.muhammad.study.data.local.dao

import androidx.room.*
import com.muhammad.study.domain.model.*
import kotlinx.coroutines.flow.*

@Dao
interface SubjectDao {
    @Upsert
    suspend fun upsertSubject(subject : Subject)
    @Query("SELECT COUNT(*) FROM subject")
    fun getTotalSubjectCount() : Flow<Int>
    @Query("SELECT SUM(goalHours) FROM subject")
    fun getTotalGoalHours() : Flow<Float>
    @Query("SELECT * FROM subject WHERE subjectId= :subjectId")
    suspend fun getSubjectById(subjectId : Long) : Subject?
    @Query("DELETE FROM Subject WHERE subjectId=:subjectId")
    suspend fun deleteSubject(subjectId: Long)
    @Query("SELECT * FROM Subject")
    fun getAllSubjects() : Flow<List<Subject>>
}