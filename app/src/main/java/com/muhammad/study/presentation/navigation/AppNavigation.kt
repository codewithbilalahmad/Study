package com.muhammad.study.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.muhammad.study.presentation.screens.home.*
import com.muhammad.study.presentation.screens.session.SessionScreen
import com.muhammad.study.presentation.screens.session.StudySessionTimerService
import com.muhammad.study.presentation.screens.subject.*
import com.muhammad.study.presentation.screens.task.*
import com.muhammad.study.presentation.viewModel.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*

@Composable
fun AppNavigation(navHostController: NavHostController, timerService: StudySessionTimerService) {
    NavHost(
        navController = navHostController,
        startDestination = Destinations.HomeScreen.route,
        enterTransition = { slideInHorizontally{it} },
        popEnterTransition ={slideInHorizontally{-it}},
        exitTransition ={slideOutHorizontally{-it}} ,
        popExitTransition = {slideOutHorizontally{it}}
    ) {
        composable(Destinations.HomeScreen.route) {
            HomeScreen(navHostController)
        }
        composable(Destinations.SessionScreen.route) {
            SessionScreen(onBackClick = {
                navHostController.navigateUp()
            }, timerService = timerService)
        }
        composable("${Destinations.SubjectScreen.route}/{subjectId}", arguments = listOf(
            navArgument("subjectId"){
                type = NavType.LongType
            }
        )) {
            val subjectId = it.arguments?.getLong("subjectId") ?: 1L
            val viewModel = koinViewModel<SubjectViewModel>{ parametersOf(subjectId) }
            SubjectScreen(viewModel = viewModel, onBack = {
                navHostController.navigateUp()
            }, onEvent = viewModel::onEvent, navHostController = navHostController)
        }
        composable("${Destinations.TaskScreen.route}/{taskId}/{subjectId}", arguments = listOf(
            navArgument("taskId"){
                type = NavType.LongType
            },
            navArgument("subjectId"){
                type = NavType.LongType
            }
        )) {
            val taskId = it.arguments?.getLong("taskId") ?: 0L
            val subjectId = it.arguments?.getLong("subjectId") ?: 0L
            TaskScreen(taskId = taskId, onBackClick = {
                navHostController.navigateUp()
            },subjectId = subjectId)
        }
    }
}