package com.group9.jobtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.navigation.JobTrackNavGraph
import com.group9.jobtrack.ui.theme.JobTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobTrackTheme {
                val navController = rememberNavController()

                // Pre-populated applications so all flows can be tested immediately
                val applications = remember {
                    mutableStateListOf(
                        JobApplication(
                            id = "1",
                            company = "Accenture",
                            position = "Associate Software Engineer",
                            platform = "JobStreet",
                            dateApplied = "Oct 15, 2026",
                            status = "Interview",
                            notes = "First round technical interview scheduled."
                        ),
                        JobApplication(
                            id = "2",
                            company = "Globe Telecom",
                            position = "Junior Android Developer",
                            platform = "LinkedIn",
                            dateApplied = "Oct 18, 2026",
                            status = "Applied",
                            notes = "Submitted resume and GitHub portfolio."
                        ),
                        JobApplication(
                            id = "3",
                            company = "Canva",
                            position = "Frontend Intern",
                            platform = "Company Portal",
                            dateApplied = "Oct 10, 2026",
                            status = "Offer",
                            notes = "Received job offer letter, reviewing terms."
                        )
                    )
                }

                JobTrackNavGraph(
                    navController = navController,
                    applications = applications
                )
            }
        }
    }
}