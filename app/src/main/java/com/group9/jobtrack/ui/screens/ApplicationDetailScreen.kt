package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    application: JobApplication?,
    onNavigateBack: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    if (application == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Application not found.")
        }
        return
    }

    val stages = listOf("Applied", "Interview", "Offer", "Rejected")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = application.position, style = MaterialTheme.typography.headlineSmall)
                    Text(text = application.company, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(text = "Current Status: ")
                        StatusBadge(status = application.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Platform: ${application.platform}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Date Applied: ${application.dateApplied}", style = MaterialTheme.typography.bodyMedium)
                    if (application.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Notes: ${application.notes}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text(text = "Update Recruitment Stage:", style = MaterialTheme.typography.titleSmall)

            stages.forEach { stage ->
                OutlinedButton(
                    onClick = { onStatusChange(stage) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = application.status != stage
                ) {
                    Text("Move to $stage")
                }
            }
        }
    }
}