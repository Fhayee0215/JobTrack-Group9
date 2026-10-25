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
import com.group9.jobtrack.ui.theme.PrimaryBlue
import com.group9.jobtrack.ui.theme.TextPrimary
import com.group9.jobtrack.ui.theme.TextSecondary

@Composable
fun AlertsScreen(
    applications: List<JobApplication>
) {
    val interviewApps = applications.filter { it.status.equals("Interview", ignoreCase = true) }
    val offerApps = applications.filter { it.status.equals("Offer", ignoreCase = true) }
    val appliedApps = applications.filter { it.status.equals("Applied", ignoreCase = true) || it.status.equals("Screening", ignoreCase = true) }

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
            color = TextPrimary
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (interviewApps.isEmpty() && offerApps.isEmpty() && appliedApps.isEmpty()) {
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
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(interviewApps, key = { "int_${it.id}" }) { app ->
                    NotificationCard(
                        title = "Upcoming Interview",
                        time = "Scheduled — ${app.dateApplied}",
                        description = "Interview scheduled for ${app.position} at ${app.company}. Prepare your case study and portfolio.",
                        actionText = "Prepare Portfolio",
                        iconTint = Color(0xFFF59E0B),
                        iconBg = Color(0xFFFEF3C7)
                    )
                }

                items(offerApps, key = { "off_${it.id}" }) { app ->
                    NotificationCard(
                        title = "Offer Received",
                        time = "Review Period",
                        description = "Offer deadline from ${app.company} (${app.position}). Send acceptance or renegotiate terms.",
                        actionText = "Review Offer Package",
                        iconTint = Color(0xFF10B981),
                        iconBg = Color(0xFFD1FAE5)
                    )
                }

                items(appliedApps, key = { "app_${it.id}" }) { app ->
                    NotificationCard(
                        title = "Follow up Needed",
                        time = "Application Tracking",
                        description = "Consider sending a polite follow-up for your application to ${app.company} as ${app.position}.",
                        actionText = "Send Follow-up Email",
                        iconTint = PrimaryBlue,
                        iconBg = Color(0xFFE0E7FF)
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
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = time, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
            Text(text = description, fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue
            )
        }
    }
}
