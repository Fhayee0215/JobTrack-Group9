package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.ui.components.ApplicationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsTabScreen(
    applications: List<JobApplication>,
    onApplicationClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStageFilter by remember { mutableStateOf("All") }
    var selectedPlatformFilter by remember { mutableStateOf("All") }
    var selectedDateRangeFilter by remember { mutableStateOf("All") }

    val stages = listOf("All", "Applied", "Screening", "Interview", "Offer", "Rejected")
    val platforms = listOf("All", "LinkedIn", "JobStreet", "Indeed", "Company Website", "Other")
    val dateRanges = listOf("All", "Today", "This Week", "This Month")

    var showStageDropdown by remember { mutableStateOf(false) }
    var showPlatformDropdown by remember { mutableStateOf(false) }
    var showDateRangeDropdown by remember { mutableStateOf(false) }

    val filteredList = applications.filter {
        val matchesQuery = it.company.contains(searchQuery, ignoreCase = true) ||
                it.position.contains(searchQuery, ignoreCase = true)
        val matchesStage = if (selectedStageFilter == "All") true else it.status.equals(selectedStageFilter, ignoreCase = true)
        val matchesPlatform = if (selectedPlatformFilter == "All") true else {
            if (selectedPlatformFilter == "Company Website") it.platform.contains("Website", true) || it.platform.contains("Company", true)
            else if (selectedPlatformFilter == "Other") {
                val p = it.platform.trim()
                !p.equals("LinkedIn", true) && !p.equals("JobStreet", true) && !p.equals("Indeed", true) && !p.contains("Website", true) && !p.contains("Company", true)
            } else it.platform.equals(selectedPlatformFilter, true)
        }
        matchesQuery && matchesStage && matchesPlatform
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search bar matching prototype ("Search companies or roles...")
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search companies or roles...", fontSize = 14.sp, color = secondaryColor) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = secondaryColor)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = outlineColor
            ),
            singleLine = true
        )

        // Filter Chips Row (Stage, Platform, Date Range matching prototype)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stage Filter Pill
            Box {
                if (selectedStageFilter != "All") {
                    // Active stage filter pill: "Stage: Interview X"
                    Surface(
                        modifier = Modifier.clickable { showStageDropdown = true },
                        shape = RoundedCornerShape(20.dp),
                        color = primaryColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "Stage: $selectedStageFilter", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { selectedStageFilter = "All" }
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { showStageDropdown = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = surfaceColor),
                        border = BorderStroke(1.dp, outlineColor),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Stage", fontSize = 12.sp, color = onSurfaceColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                    }
                }

                DropdownMenu(
                    expanded = showStageDropdown,
                    onDismissRequest = { showStageDropdown = false },
                    modifier = Modifier.background(surfaceColor)
                ) {
                    stages.forEach { stage ->
                        DropdownMenuItem(
                            text = { Text(stage) },
                            onClick = {
                                selectedStageFilter = stage
                                showStageDropdown = false
                            }
                        )
                    }
                }
            }

            // Platform Filter Pill
            Box {
                OutlinedButton(
                    onClick = { showPlatformDropdown = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedPlatformFilter != "All") Color(0xFFEEF2FF) else surfaceColor),
                    border = BorderStroke(1.dp, if (selectedPlatformFilter != "All") primaryColor else outlineColor),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = if (selectedPlatformFilter == "All") "Platform" else selectedPlatformFilter, fontSize = 12.sp, color = if (selectedPlatformFilter != "All") primaryColor else onSurfaceColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                }

                DropdownMenu(
                    expanded = showPlatformDropdown,
                    onDismissRequest = { showPlatformDropdown = false },
                    modifier = Modifier.background(surfaceColor)
                ) {
                    platforms.forEach { platform ->
                        DropdownMenuItem(
                            text = { Text(platform) },
                            onClick = {
                                selectedPlatformFilter = platform
                                showPlatformDropdown = false
                            }
                        )
                    }
                }
            }

            // Date Range Filter Pill
            Box {
                OutlinedButton(
                    onClick = { showDateRangeDropdown = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedDateRangeFilter != "All") Color(0xFFEEF2FF) else surfaceColor),
                    border = BorderStroke(1.dp, if (selectedDateRangeFilter != "All") primaryColor else outlineColor),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = if (selectedDateRangeFilter == "All") "Date Range" else selectedDateRangeFilter, fontSize = 12.sp, color = if (selectedDateRangeFilter != "All") primaryColor else onSurfaceColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                }

                DropdownMenu(
                    expanded = showDateRangeDropdown,
                    onDismissRequest = { showDateRangeDropdown = false },
                    modifier = Modifier.background(surfaceColor)
                ) {
                    dateRanges.forEach { range ->
                        DropdownMenuItem(
                            text = { Text(range) },
                            onClick = {
                                selectedDateRangeFilter = range
                                showDateRangeDropdown = false
                            }
                        )
                    }
                }
            }
        }

        // List & "OTHER " grouping logic matching prototype
        val primaryFiltered = filteredList
        val otherFiltered = if (selectedStageFilter != "All") {
            applications.filter {
                val matchesQuery = it.company.contains(searchQuery, ignoreCase = true) ||
                        it.position.contains(searchQuery, ignoreCase = true)
                val matchesStage = !it.status.equals(selectedStageFilter, ignoreCase = true)
                matchesQuery && matchesStage
            }
        } else emptyList()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (primaryFiltered.isEmpty() && otherFiltered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No applications match your criteria.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryColor
                        )
                    }
                }
            } else {
                // Primary results
                items(primaryFiltered, key = { "prim_${it.id}" }) { app ->
                    ApplicationCard(
                        application = app,
                        onClick = { onApplicationClick(app.id) }
                    )
                }

                // If a stage filter is active and there are other , show "OTHER S" divider
                if (selectedStageFilter != "All" && otherFiltered.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = outlineColor)
                            Text(
                                text = " OTHER ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = secondaryColor
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = outlineColor)
                        }
                    }

                    items(otherFiltered, key = { "other_${it.id}" }) { app ->
                        ApplicationCard(
                            application = app,
                            onClick = { onApplicationClick(app.id) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
