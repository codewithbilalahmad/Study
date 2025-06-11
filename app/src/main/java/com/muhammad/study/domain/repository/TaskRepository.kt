package com.muhammad.study.domain.repository

import com.muhammad.study.domain.model.*
import kotlinx.coroutines.flow.*

interface TaskRepository {
    suspend fun upsertTask(task : Task)
    suspend fun deleteTask(taskId : Long)
    suspend fun getTaskById(taskId : Long) : Task?
    fun getUpcomingTasksForSubject(subjectId : Long) : Flow<List<Task>>
    fun getCompletedTasksForSubject(subjectId : Long) : Flow<List<Task>>
    fun getAllUpcomingTasks() : Flow<List<Task>>
}