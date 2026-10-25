package com.group9.jobtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.data.JourneyEntry
import com.group9.jobtrack.navigation.JobTrackNavGraph
import com.group9.jobtrack.ui.theme.JobTrackTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobTrackTheme {
                val navController = rememberNavController()

                // Pre-populated applications sorted chronologically (newest first)
                val applications = remember {
                    val rawApplications = listOf(
                        JobApplication(
                            id = "1",
                            company = "Accenture",
                            position = "Associate Software Engineer",
                            platform = "JobStreet",
                            dateApplied = "Oct 15, 2026",
                            status = "Interview",
                            notes = "First round technical interview scheduled.",
                            journey = listOf(
                                JourneyEntry(status = "Applied", date = "Oct 15, 2026"),
                                JourneyEntry(status = "Interview", date = "Oct 18, 2026", note = "Advanced to technical interview")
                            )
                        ),
                        JobApplication(
                            id = "2",
                            company = "Globe Telecom",
                            position = "Junior Android Developer",
                            platform = "LinkedIn",
                            dateApplied = "Oct 18, 2026",
                            status = "Applied",
                            notes = "Submitted resume and GitHub portfolio.",
                            journey = listOf(
                                JourneyEntry(status = "Applied", date = "Oct 18, 2026")
                            )
                        ),
                        JobApplication(
                            id = "3",
                            company = "Canva",
                            position = "Frontend Intern",
                            platform = "Company Portal",
                            dateApplied = "Oct 10, 2026",
                            status = "Offer",
                            notes = "Received job offer letter, reviewing terms.",
                            journey = listOf(
                                JourneyEntry(status = "Applied", date = "Oct 10, 2026"),
                                JourneyEntry(status = "Interview", date = "Oct 14, 2026", note = "Passed design and coding rounds"),
                                JourneyEntry(status = "Offer", date = "Oct 20, 2026", note = "Received formal offer letter")
                            )
                        )
                    )

                    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }

                    val sorted = rawApplications.sortedByDescending { app ->
                        try {
                            dateFormatter.parse(app.dateApplied)?.time ?: 0L
                        } catch (_: Exception) {
                            0L
                        }
                    }

                    mutableStateListOf<JobApplication>().apply {
                        addAll(sorted)
                    }
                }

                JobTrackNavGraph(
                    navController = navController,
                    applications = applications
                )
            }
        }
    }
}
