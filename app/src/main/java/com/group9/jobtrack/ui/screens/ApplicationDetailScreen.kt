package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Language
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
import com.group9.jobtrack.ui.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    application: JobApplication?,
    onNavigateBack: () -> Unit,
    onStatusChange: (String, String, String) -> Unit // newStatus, date, note
) {
    if (application == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Application not found.")
        }
        return
    }

    var showUpdateModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val stages = listOf("Applied", "Screening", "Interview", "Offer", "Rejected")
    var selectedNewStage by remember { mutableStateOf(application.status) }

    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val currentTime = System.currentTimeMillis()
    var selectedDateMillis by remember { mutableStateOf<Long?>(currentTime) }
    var stageDateText by remember { mutableStateOf(dateFormatter.format(Date(currentTime))) }
    var showDatePicker by remember { mutableStateOf(false) }
    var stageNote by remember { mutableStateOf("") }
    var showValidationError by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

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
                            stageDateText = dateFormatter.format(Date(millis))
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card matching prototype
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = application.position,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = onSurfaceColor,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(status = application.status)
                    }

                    Text(
                        text = application.company,
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Language, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                            Text(text = application.platform, style = MaterialTheme.typography.bodyMedium, color = secondaryColor)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                            Text(text = "Applied ${application.dateApplied}", style = MaterialTheme.typography.bodyMedium, color = secondaryColor)
                        }
                    }
                }
            }

            // Application Journey Timeline
            Text(text = "APPLICATION JOURNEY", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = secondaryColor)

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
                    if (application.journey.isEmpty()) {
                        Text(text = "No history recorded yet.", style = MaterialTheme.typography.bodyMedium, color = secondaryColor)
                    } else {
                        application.journey.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor)
                                    )
                                    if (index < application.journey.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(40.dp)
                                                .background(outlineColor)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = entry.status, fontWeight = FontWeight.Bold, color = onSurfaceColor, fontSize = 15.sp)
                                        Text(
                                            text = entry.date,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = secondaryColor
                                        )
                                    }
                                    if (entry.note.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = entry.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = secondaryColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notes Section
            if (application.notes.isNotBlank()) {
                Text(text = "NOTES", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = secondaryColor)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = application.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceColor,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Update Stage Button
            Button(
                onClick = { showUpdateModal = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Update Stage")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ModalBottomSheet popping up from the bottom of the screen
        if (showUpdateModal) {
            ModalBottomSheet(
                onDismissRequest = { showUpdateModal = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Update Current Stage",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor
                    )
                    Text(
                        text = "Select current phase of the process",
                        fontSize = 13.sp,
                        color = secondaryColor
                    )

                    stages.forEach { stage ->
                        OutlinedCard(
                            onClick = {
                                selectedNewStage = stage
                                showValidationError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (selectedNewStage == stage) Color(0xFFEEF2FF) else surfaceColor
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(if (selectedNewStage == stage) primaryColor else outlineColor)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = stage, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                                RadioButton(
                                    selected = selectedNewStage == stage,
                                    onClick = {
                                        selectedNewStage = stage
                                        showValidationError = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                                )
                            }
                        }
                    }

                    if (selectedNewStage == "Interview" && showValidationError) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Interview date required — Please add scheduled date for reminders.",
                                    color = Color(0xFF92400E),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Text(text = "Stage Date", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        OutlinedTextField(
                            value = stageDateText,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
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

                    OutlinedTextField(
                        value = stageNote,
                        onValueChange = { stageNote = it },
                        label = { Text("Note (Optional)") },
                        placeholder = { Text("e.g. Technical panel interview, 15 min with recruiter") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (selectedNewStage == "Interview" && stageDateText.isBlank()) {
                                showValidationError = true
                            } else {
                                onStatusChange(selectedNewStage, stageDateText, stageNote.ifBlank { "Moved to $selectedNewStage" })
                                showUpdateModal = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Confirm Update")
                    }

                    TextButton(
                        onClick = { showUpdateModal = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = secondaryColor)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
