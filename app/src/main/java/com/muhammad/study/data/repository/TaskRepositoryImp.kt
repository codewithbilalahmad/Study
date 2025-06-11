package com.muhammad.study.data.repository

import com.muhammad.study.data.local.dao.*
import com.muhammad.study.domain.model.*
import com.muhammad.study.domain.repository.*
import kotlinx.coroutines.flow.*

class TaskRepositoryImp(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectId: Long): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map {tasks ->
            tasks.filter { it.isComplete.not() }
        }.map { tasks -> sortTasks(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectId: Long): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map {tasks ->
            tasks.filter { it.isComplete }
        }.map { tasks -> sortTasks(tasks) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTask().map {tasks ->
            tasks.filter { it.isComplete.not() }
        }.map {tasks -> sortTasks(tasks) }
    }
    private fun sortTasks(tasks : List<Task>) : List<Task>{
        return tasks.sortedWith(compareBy<Task>{it.dueDate}.thenByDescending { it.priority })
    }
}