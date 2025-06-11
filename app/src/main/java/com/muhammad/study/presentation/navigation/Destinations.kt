package com.muhammad.study.presentation.navigation

sealed class Destinations(val route : String){
    data object HomeScreen : Destinations("HomeScreen")
    data object SessionScreen : Destinations("SessionScreen")
    data object SubjectScreen : Destinations("SubjectScreen")
    data object TaskScreen : Destinations("TaskScreen")
}