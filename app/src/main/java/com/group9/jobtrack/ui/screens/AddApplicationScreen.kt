package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group9.jobtrack.data.JobApplication
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApplicationScreen(
    onNavigateBack: () -> Unit,
    onSaveApplication: (JobApplication) -> Unit
) {
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("LinkedIn") }
    var notes by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Application") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = company,
                onValueChange = {
                    company = it
                    showError = false
                },
                label = { Text("Company Name *") },
                isError = showError && company.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = position,
                onValueChange = {
                    position = it
                    showError = false
                },
                label = { Text("Job Position *") },
                isError = showError && position.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = platform,
                onValueChange = { platform = it },
                label = { Text("Platform (e.g. LinkedIn, JobStreet)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (showError) {
                Text(
                    text = "Please complete all mandatory fields (*).",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (company.isNotBlank() && position.isNotBlank()) {
                        val newApp = JobApplication(
                            id = UUID.randomUUID().toString(),
                            company = company.trim(),
                            position = position.trim(),
                            platform = platform.trim(),
                            dateApplied = "Oct 21, 2026",
                            status = "Applied",
                            notes = notes.trim()
                        )
                        onSaveApplication(newApp)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Application")
            }
        }
    }
}