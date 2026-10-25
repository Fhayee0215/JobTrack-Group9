package com.group9.jobtrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.data.JourneyEntry
import com.group9.jobtrack.ui.screens.AddApplicationScreen
import com.group9.jobtrack.ui.screens.ApplicationDetailScreen
import com.group9.jobtrack.ui.screens.MainContainerScreen

@Composable
fun JobTrackNavGraph(
    navController: NavHostController,
    applications: SnapshotStateList<JobApplication>
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.createRoute("Dashboard")
    ) {
        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(navArgument("initialTab") { type = NavType.StringType; defaultValue = "Dashboard" })
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("initialTab") ?: "Dashboard"
            MainContainerScreen(
                initialTab = initialTab,
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
                    navController.navigate(Screen.Dashboard.createRoute("Applications")) {
                        popUpTo(Screen.Dashboard.createRoute("Dashboard")) { inclusive = true }
                    }
                },
                onAddAnother = { newApp ->
                    applications.add(0, newApp)
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
                onStatusChange = { newStatus, date, note ->
                    if (appIndex != -1 && currentApp != null) {
                        val newEntry = JourneyEntry(
                            status = newStatus,
                            date = date,
                            note = note
                        )
                        val updatedJourney = currentApp.journey + newEntry
                        applications[appIndex] = currentApp.copy(
                            status = newStatus,
                            journey = updatedJourney
                        )
                    }
                }
            )
        }
    }
}
