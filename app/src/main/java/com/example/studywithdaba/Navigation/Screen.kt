package com.example.studywithdaba.Navigation

sealed  class Screen(val route: String) {
    object Home: Screen("home")
    object Notes: Screen("notes")
    object EditNote: Screen("edit_note")
    object Settings: Screen("settings")
}