package com.muhammad.study.di

import com.muhammad.study.data.local.*
import com.muhammad.study.data.repository.SessionRepositoryImp
import com.muhammad.study.data.repository.SubjectRespositoryImp
import com.muhammad.study.data.repository.TaskRepositoryImp
import com.muhammad.study.domain.repository.SessionRepository
import com.muhammad.study.domain.repository.SubjectRepository
import com.muhammad.study.domain.repository.TaskRepository
import com.muhammad.study.presentation.viewModel.HomeViewModel
import com.muhammad.study.presentation.viewModel.SessionViewModel
import com.muhammad.study.presentation.viewModel.SubjectViewModel
import com.muhammad.study.presentation.viewModel.TaskViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.*

val appModule = module {
    single { databaseBuilder() }
    single { databaseBuilder().taskDao() }
    single { databaseBuilder().sessionDao() }
    single { databaseBuilder().subjectDao() }
    singleOf(::SubjectRespositoryImp).bind<SubjectRepository>()
    singleOf(::TaskRepositoryImp).bind<TaskRepository>()
    singleOf(::SessionRepositoryImp).bind<SessionRepository>()
    single { HomeViewModel(get(), get(), get()) }
    single { TaskViewModel(get(), get()) }
    single { SessionViewModel(get(),get()) }
    viewModel { (subjectId: Long) ->
        SubjectViewModel(
            subjectRepository = get(),
            taskRepository = get(),
            sessionRepository = get(), subjectId = subjectId
        )
    }
}