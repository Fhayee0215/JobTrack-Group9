package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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
import java.text.SimpleDateFormat
import java.util.*

data class NotificationItem(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    val actionText: String,
    val iconTint: Color,
    val iconBg: Color,
    val timestamp: Long
)

@Composable
fun AlertsScreen(
    applications: List<JobApplication>
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    // Collect and sort all notifications from latest (top) to oldest (bottom)
    val notifications = remember(applications) {
        val list = mutableListOf<NotificationItem>()

        applications.forEach { app ->
            val journeyEntry = app.journey.lastOrNull { it.status.equals(app.status, true) } ?: app.journey.lastOrNull()
            val dateText = journeyEntry?.date ?: app.dateApplied
            val parsedMillis = try {
                dateFormatter.parse(dateText)?.time ?: 0L
            } catch (_: Exception) {
                0L
            }

            val customNote = journeyEntry?.note?.takeIf { it.isNotBlank() && !it.startsWith("Moved to", true) && !it.startsWith("Application created", true) }
                ?: app.notes.takeIf { it.isNotBlank() }

            when {
                app.status.equals("Interview", true) -> {
                    val noteText = customNote ?: "Interview scheduled for ${app.position} at ${app.company}. Prepare your case study and portfolio."
                    list.add(
                        NotificationItem(
                            id = "int_${app.id}",
                            title = "Upcoming Interview",
                            time = "Scheduled — $dateText",
                            description = noteText,
                            actionText = "Prepare Portfolio",
                            iconTint = Color(0xFFF59E0B),
                            iconBg = Color(0xFFFEF3C7),
                            timestamp = parsedMillis
                        )
                    )
                }
                app.status.equals("Offer", true) -> {
                    val noteText = customNote ?: "Offer deadline from ${app.company} (${app.position}). Send acceptance or renegotiate terms."
                    list.add(
                        NotificationItem(
                            id = "off_${app.id}",
                            title = "Offer Received",
                            time = "Review Period — $dateText",
                            description = noteText,
                            actionText = "Review Offer Package",
                            iconTint = Color(0xFF10B981),
                            iconBg = Color(0xFFD1FAE5),
                            timestamp = parsedMillis
                        )
                    )
                }
                app.status.equals("Applied", true) || app.status.equals("Screening", true) -> {
                    val noteText = customNote ?: "Consider sending a polite follow-up for your application to ${app.company} as ${app.position}."
                    list.add(
                        NotificationItem(
                            id = "app_${app.id}",
                            title = "Follow up Needed",
                            time = "Application Tracking — $dateText",
                            description = noteText,
                            actionText = "Send Follow-up Email",
                            iconTint = primaryColor,
                            iconBg = Color(0xFFE0E7FF),
                            timestamp = parsedMillis
                        )
                    )
                }
            }
        }

        // Sort latest top to bottom (descending by timestamp)
        list.sortedByDescending { it.timestamp }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Notifications & Reminders",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = onSurfaceColor
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No upcoming reminders. All caught up!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryColor
                        )
                    }
                }
            } else {
                items(notifications, key = { it.id }) { item ->
                    NotificationCard(
                        title = item.title,
                        time = item.time,
                        description = item.description,
                        actionText = item.actionText,
                        iconTint = item.iconTint,
                        iconBg = item.iconBg
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun NotificationCard(
    title: String,
    time: String,
    description: String,
    actionText: String,
    iconTint: Color,
    iconBg: Color
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface

    Card(
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
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                        Text(text = time, fontSize = 11.sp, color = secondaryColor)
                    }
                }
            }
            Text(text = description, fontSize = 13.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryColor
            )
        }
    }
}
