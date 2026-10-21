package com.group9.jobtrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.ui.screens.AddApplicationScreen
import com.group9.jobtrack.ui.screens.ApplicationDetailScreen
import com.group9.jobtrack.ui.screens.DashboardScreen

@Composable
fun JobTrackNavGraph(
    navController: NavHostController,
    applications: SnapshotStateList<JobApplication>
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                applications = applications,
                onAddClick = {
                    navController.navigate(Screen.AddApplication.route)
                },
                onApplicationClick = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                }
            )
        }

        composable(Screen.AddApplication.route) {
            AddApplicationScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveApplication = { newApp ->
                    applications.add(0, newApp)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appId = backStackEntry.arguments?.getString("applicationId")
            val appIndex = applications.indexOfFirst { it.id == appId }
            val currentApp = if (appIndex != -1) applications[appIndex] else null

            ApplicationDetailScreen(
                application = currentApp,
                onNavigateBack = { navController.popBackStack() },
                onStatusChange = { newStatus ->
                    if (appIndex != -1 && currentApp != null) {
                        applications[appIndex] = currentApp.copy(status = newStatus)
                    }
                }
            )
        }
    }
}