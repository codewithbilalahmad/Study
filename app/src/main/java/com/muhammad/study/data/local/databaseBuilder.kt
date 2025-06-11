package com.muhammad.study.data.local

import androidx.room.*
import com.muhammad.study.*

fun databaseBuilder() : StudyDatabase {
    val context = StudyApplication.INSTANCE
    return Room.databaseBuilder(context, StudyDatabase::class.java, "study.db").build()
}