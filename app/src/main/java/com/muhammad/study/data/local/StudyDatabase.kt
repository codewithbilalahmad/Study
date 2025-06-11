package com.muhammad.study.data.local

import androidx.room.*
import com.muhammad.study.data.local.converter.*
import com.muhammad.study.data.local.dao.*
import com.muhammad.study.domain.model.*

@Database(
    entities = [Subject::class, Session::class, Task::class], version = 1
)
@TypeConverters(ColorListConverter::class)
abstract class StudyDatabase : RoomDatabase(){
    abstract fun subjectDao() : SubjectDao
    abstract fun sessionDao() : SessionDao
    abstract fun taskDao() : TaskDao
}