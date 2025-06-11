package com.muhammad.study.domain.model

import androidx.room.*
import com.muhammad.study.presentation.theme.*

@Entity
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val subjectId : Long?=null,
    val name : String,
    val goalHours : Float,
    val colors : List<Int>
){
    companion object{
        val subjectCardColors = listOf(gradient1,gradient2,gradient3,gradient4, gradient5)
    }
}
