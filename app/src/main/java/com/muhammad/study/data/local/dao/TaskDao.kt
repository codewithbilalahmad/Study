package com.muhammad.study.data.local.dao

import androidx.room.*
import com.muhammad.study.domain.model.*
import kotlinx.coroutines.flow.*

@Dao
interface TaskDao{
    @Upsert
    suspend fun upsertTask(task : Task)
    @Query("DELETE FROM Task WHERE taskId = :taskId")
    suspend fun deleteTask(taskId : Long)
    @Query("DELETE FROM Task WHERE taskSubjectId = :subjectId")
    suspend fun deleteTaskBySubjectId(subjectId : Long)
    @Query("SELECT * FROM Task WHERE taskId =:taskId")
    suspend fun getTaskById(taskId: Long) : Task?
    @Query("SELECT * FROM Task WHERE taskSubjectId =:subjectId")
    fun getTasksForSubject(subjectId: Long) : Flow<List<Task>>
    @Query("SELECT * FROM Task")
    fun getAllTask() : Flow<List<Task>>
}