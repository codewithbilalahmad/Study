package com.muhammad.study

import android.app.*
import com.muhammad.study.di.*
import org.koin.android.ext.koin.*
import org.koin.core.context.*

class StudyApplication : Application(){
    companion object{
        lateinit var INSTANCE : StudyApplication
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        startKoin{
            androidContext(this@StudyApplication)
            androidLogger()
            modules(appModule)
        }
    }
}