package com.group9.jobtrack.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard/{initialTab}") {
        fun createRoute(initialTab: String = "Dashboard") = "dashboard/$initialTab"
    }
    object AddApplication : Screen("add_application")
    object Detail : Screen("detail/{applicationId}") {
        fun createRoute(applicationId: String) = "detail/$applicationId"
    }
}
