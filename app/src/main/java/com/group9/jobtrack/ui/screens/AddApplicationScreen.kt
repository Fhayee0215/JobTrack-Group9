package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.data.JourneyEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApplicationScreen(
    onNavigateBack: () -> Unit,
    onSaveApplication: (JobApplication) -> Unit,
    onAddAnother: (JobApplication) -> Unit
) {
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var platformChoice by remember { mutableStateOf("LinkedIn") }
    var customPlatform by remember { mutableStateOf("") }
    var selectedStage by remember { mutableStateOf("Applied") }
    var notes by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var savedAppId by remember { mutableStateOf<JobApplication?>(null) }

    val platforms = listOf("LinkedIn", "JobStreet", "Indeed", "Company Website", "Other")
    val stages = listOf("Applied", "Screening", "Interview", "Offer")
    var expandedPlatform by remember { mutableStateOf(false) }

    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val currentTime = System.currentTimeMillis()
    var selectedDateMillis by remember { mutableStateOf<Long?>(currentTime) }
    var dateAppliedText by remember { mutableStateOf(dateFormatter.format(Date(currentTime))) }
    var showDatePicker by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.background

    if (showDatePicker) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            yearRange = (currentYear - 2)..(currentYear + 1)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDateMillis = millis
                            dateAppliedText = dateFormatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) { Text("OK", color = primaryColor, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = secondaryColor) }
            },
            colors = DatePickerDefaults.colors(containerColor = surfaceColor)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = surfaceColor,
                    selectedDayContainerColor = primaryColor,
                    todayContentColor = primaryColor,
                    todayDateBorderColor = primaryColor
                )
            )
        }
    }

    val actualPlatform = if (platformChoice == "Other") customPlatform.trim() else platformChoice

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("New Application", color = onSurfaceColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showError) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Please complete all required fields (*), including platform.",
                            color = Color(0xFF991B1B),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Company Name
                        OutlinedTextField(
                            value = company,
                            onValueChange = { company = it },
                            label = { Text("Company Name *") },
                            placeholder = { Text("e.g. Google, Accenture") },
                            isError = showError && company.isBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Position Title
                        OutlinedTextField(
                            value = position,
                            onValueChange = { position = it },
                            label = { Text("Position Title *") },
                            placeholder = { Text("e.g. Android Developer, UI/UX Designer") },
                            isError = showError && position.isBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Platform Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedPlatform,
                            onExpandedChange = { expandedPlatform = !expandedPlatform }
                        ) {
                            OutlinedTextField(
                                value = platformChoice,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Platform *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlatform) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedPlatform,
                                onDismissRequest = { expandedPlatform = false }
                            ) {
                                platforms.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p) },
                                        onClick = {
                                            platformChoice = p
                                            expandedPlatform = false
                                        }
                                    )
                                }
                            }
                        }

                        // Custom platform input if "Other" is selected
                        if (platformChoice == "Other") {
                            OutlinedTextField(
                                value = customPlatform,
                                onValueChange = { customPlatform = it },
                                label = { Text("Specify Platform *") },
                                placeholder = { Text("e.g. Referral, Handshake, Glassdoor") },
                                isError = showError && customPlatform.isBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Date Applied
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = dateAppliedText,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Date Applied *") },
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = onSurfaceColor,
                                    disabledBorderColor = outlineColor,
                                    disabledLabelColor = secondaryColor,
                                    disabledTrailingIconColor = secondaryColor,
                                    disabledContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Current Stage Selector (Scrollable sideways buttons)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Current Stage", style = MaterialTheme.typography.bodyMedium, color = secondaryColor)
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(stages) { stage ->
                                    FilterChip(
                                        selected = selectedStage == stage,
                                        onClick = { selectedStage = stage },
                                        label = { Text(stage, fontSize = 12.sp) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = primaryColor,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                        }

                        // Notes
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes") },
                            placeholder = { Text("Add details about salary, referral, or job description...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val isPlatformValid = if (platformChoice == "Other") customPlatform.isNotBlank() else platformChoice.isNotBlank()
                        if (company.isNotBlank() && position.isNotBlank() && isPlatformValid) {
                            val newApp = JobApplication(
                                id = UUID.randomUUID().toString(),
                                company = company.trim(),
                                position = position.trim(),
                                platform = actualPlatform,
                                dateApplied = dateAppliedText,
                                status = selectedStage,
                                notes = notes.trim(),
                                journey = listOf(
                                    JourneyEntry(status = selectedStage, date = dateAppliedText, note = "Application created via $actualPlatform")
                                )
                            )
                            savedAppId = newApp
                            showSuccessDialog = true
                        } else {
                            showError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Application", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Success Dialog / Bottom Sheet
            if (showSuccessDialog && savedAppId != null) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.4f)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            color = surfaceColor
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD1FAE5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF065F46),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Text(
                                    text = "Application Saved!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = onSurfaceColor
                                )
                                Text(
                                    text = "${savedAppId?.company} — ${savedAppId?.position}",
                                    fontSize = 14.sp,
                                    color = secondaryColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        onSaveApplication(savedAppId!!)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("View Applications")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onAddAnother(savedAppId!!)
                                        // Reset form to add another
                                        company = ""
                                        position = ""
                                        platformChoice = "LinkedIn"
                                        customPlatform = ""
                                        selectedStage = "Applied"
                                        notes = ""
                                        showSuccessDialog = false
                                        savedAppId = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Add Another", color = onSurfaceColor)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
