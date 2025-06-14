package com.muhammad.study.data.local.converter

import androidx.room.*

class ColorListConverter {
    @TypeConverter
    fun fromColorList(colorList : List<Int>) : String{
        return colorList.joinToString(","){it.toString()}
    }
    @TypeConverter
    fun toColorList(colorListString : String) : List<Int>{
        return colorListString.split(",").map { it.toInt() }
    }
}