package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.group9.jobtrack.ui.components.ApplicationCard
import com.group9.jobtrack.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTabScreen(
    applications: List<JobApplication>,
    onApplicationClick: (String) -> Unit
) {
    val totalCount = applications.size
    val interviewCount = applications.count { it.status.equals("Interview", ignoreCase = true) }
    val offerCount = applications.count { it.status.equals("Offer", ignoreCase = true) }
    val rejectedCount = applications.count { it.status.equals("Rejected", ignoreCase = true) }

    var selectedStageFilter by remember { mutableStateOf("All") }
    val stages = listOf("All", "Applied", "Screening", "Interview", "Offer", "Rejected")

    val displayedApps = applications.filter {
        if (selectedStageFilter == "All") true else it.status.equals(selectedStageFilter, ignoreCase = true)
    }.take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning,",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "John Doe",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AM",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Metrics Summary (2x2 Grid)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Apps",
                    value = totalCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Interviews",
                    value = interviewCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Offers",
                    value = offerCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Rejections",
                    value = rejectedCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 1. "Recent Applications" Title first
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Applications",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${displayedApps.size} shown",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // 2. Sideways Stage Navigation Chips below the title
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stages) { stage ->
                FilterChip(
                    selected = selectedStageFilter == stage,
                    onClick = { selectedStageFilter = stage },
                    label = { Text(stage, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        // 3. Scrolling list of applications
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (displayedApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No applications found for $selectedStageFilter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                items(displayedApps, key = { it.id }) { app ->
                    ApplicationCard(
                        application = app,
                        onClick = { onApplicationClick(app.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
