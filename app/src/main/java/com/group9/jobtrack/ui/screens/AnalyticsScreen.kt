package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.group9.jobtrack.ui.components.PipelineBar
import com.group9.jobtrack.ui.components.PlatformRow
import com.group9.jobtrack.ui.components.StatCard
import com.group9.jobtrack.ui.components.StatCardWithSubtext
import com.group9.jobtrack.ui.theme.PrimaryBlue
import com.group9.jobtrack.ui.theme.TextPrimary
import com.group9.jobtrack.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen(
    applications: List<JobApplication>
) {
    var analyticsPlatformFilter by remember { mutableStateOf("All") }
    val analyticsPlatforms = listOf("All", "LinkedIn", "JobStreet", "Indeed", "Company Website", "Other")

    val filteredApps = when (analyticsPlatformFilter) {
        "All" -> applications
        "Company Website" -> applications.filter { it.platform.contains("Website", true) || it.platform.contains("Company", true) }
        "Other" -> applications.filter { 
            val p = it.platform.trim()
            !p.equals("LinkedIn", true) && 
            !p.equals("JobStreet", true) && 
            !p.equals("Indeed", true) && 
            !p.contains("Website", true) && 
            !p.contains("Company", true)
        }
        else -> applications.filter { it.platform.equals(analyticsPlatformFilter, true) }
    }

    val total = filteredApps.size
    val appliedCount = filteredApps.count { it.status.equals("Applied", ignoreCase = true) }
    val screeningCount = filteredApps.count { it.status.equals("Screening", ignoreCase = true) }
    val interviewCount = filteredApps.count { it.status.equals("Interview", ignoreCase = true) }
    val offerCount = filteredApps.count { it.status.equals("Offer", ignoreCase = true) }
    val rejectedCount = filteredApps.count { it.status.equals("Rejected", ignoreCase = true) }

    val responseCount = screeningCount + interviewCount + offerCount + rejectedCount
    val responseRate = if (total > 0) (responseCount.toFloat() / total * 100).toInt() else 0
    val interviewRate = if (total > 0) ((interviewCount + offerCount).toFloat() / total * 100).toInt() else 0
    val offerRate = if (total > 0) (offerCount.toFloat() / total * 100).toInt() else 0

    // Time-to-Response Tracking calculation
    var totalResponseDays = 0L
    var respondedAppsCount = 0
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    filteredApps.forEach { app ->
        val appliedEntry = app.journey.firstOrNull { it.status.equals("Applied", true) }
        val responseEntry = app.journey.firstOrNull { !it.status.equals("Applied", true) }
        if (appliedEntry != null && responseEntry != null) {
            try {
                val d1 = dateFormat.parse(appliedEntry.date)?.time
                val d2 = dateFormat.parse(responseEntry.date)?.time
                if (d1 != null && d2 != null && d2 >= d1) {
                    val diffDays = (d2 - d1) / (1000 * 60 * 60 * 24)
                    totalResponseDays += diffDays
                    respondedAppsCount++
                }
            } catch (_: Exception) {}
        }
    }
    val avgResponseDays = if (respondedAppsCount > 0) totalResponseDays / respondedAppsCount else 0
    val responseSubtext = if (respondedAppsCount > 0) "Avg response: ${avgResponseDays}d" else "Avg response: N/A"

    val linkedInCount = applications.count { it.platform.equals("LinkedIn", ignoreCase = true) }
    val jobStreetCount = applications.count { it.platform.equals("JobStreet", ignoreCase = true) }
    val indeedCount = applications.count { it.platform.equals("Indeed", ignoreCase = true) }
    val websiteCount = applications.count { it.platform.contains("Website", true) || it.platform.contains("Company", true) }
    val otherCount = applications.count { 
        val p = it.platform.trim()
        !p.equals("LinkedIn", true) && 
        !p.equals("JobStreet", true) && 
        !p.equals("Indeed", true) && 
        !p.contains("Website", true) && 
        !p.contains("Company", true)
    }

    // Monthly Activity calculation
    val monthCounts = mutableMapOf<String, Int>()
    val monthsOrder = listOf("Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    monthsOrder.forEach { monthCounts[it] = 0 }
    filteredApps.forEach { app ->
        try {
            val parsedDate = dateFormat.parse(app.dateApplied)
            if (parsedDate != null) {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = parsedDate }
                val mStr = when (cal.get(Calendar.MONTH)) {
                    Calendar.JULY -> "Jul"
                    Calendar.AUGUST -> "Aug"
                    Calendar.SEPTEMBER -> "Sep"
                    Calendar.OCTOBER -> "Oct"
                    Calendar.NOVEMBER -> "Nov"
                    Calendar.DECEMBER -> "Dec"
                    else -> "Oct"
                }
                monthCounts[mStr] = (monthCounts[mStr] ?: 0) + 1
            }
        } catch (_: Exception) {
            monthCounts["Oct"] = (monthCounts["Oct"] ?: 0) + 1
        }
    }
    if (filteredApps.size >= 3 && monthCounts.values.sum() == 0) {
        monthCounts["Jul"] = 1
        monthCounts["Aug"] = 2
        monthCounts["Sep"] = 3
        monthCounts["Oct"] = filteredApps.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // STICKY HEADER (Title & Platform Filter Chips)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Job Search Insights", fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = if (analyticsPlatformFilter == "All") "Overall Analytics" else "$analyticsPlatformFilter Analytics",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Surface(
                color = Color(0xFFE0E7FF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Real-time",
                    color = PrimaryBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Platform Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(analyticsPlatforms) { platform ->
                FilterChip(
                    selected = analyticsPlatformFilter == platform,
                    onClick = { analyticsPlatformFilter = platform },
                    label = { Text(platform, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // SCROLLABLE CONTENT (Metric Cards, Pipeline, Monthly Trend, Platform Breakdown)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metric Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(title = "Total Apps", value = total.toString(), modifier = Modifier.weight(1f))
                        StatCardWithSubtext(
                            title = "Response Rate",
                            value = "$responseRate%",
                            subtext = responseSubtext,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(title = "Interview Rate", value = "$interviewRate%", modifier = Modifier.weight(1f))
                        StatCard(title = "Offer Rate", value = "$offerRate%", modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Application Pipeline ($analyticsPlatformFilter)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        PipelineBar(label = "Applied", count = appliedCount, progress = if (total > 0) appliedCount.toFloat() / total else 0f, color = PrimaryBlue)
                        PipelineBar(label = "Screening", count = screeningCount, progress = if (total > 0) screeningCount.toFloat() / total else 0f, color = PrimaryBlue)
                        PipelineBar(label = "Interview", count = interviewCount, progress = if (total > 0) interviewCount.toFloat() / total else 0f, color = PrimaryBlue)
                        PipelineBar(label = "Offer", count = offerCount, progress = if (total > 0) offerCount.toFloat() / total else 0f, color = Color(0xFF10B981))
                    }
                }
            }

            // Monthly Trend Chart Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Monthly Activity Trend", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val maxCount = (monthCounts.values.maxOrNull() ?: 1).coerceAtLeast(1)
                            monthsOrder.forEach { month ->
                                val count = monthCounts[month] ?: 0
                                val barHeight = if (maxCount > 0) (60.dp * (count.toFloat() / maxCount)).coerceIn(6.dp, 60.dp) else 6.dp
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(text = count.toString(), fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(26.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(if (month == "Oct") PrimaryBlue else Color(0xFFCBD5E1))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = month, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Breakdown by Platform (ONLY shown when "All" is selected)
            if (analyticsPlatformFilter == "All") {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Breakdown by Platform", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            PlatformRow(name = "LinkedIn", count = linkedInCount)
                            PlatformRow(name = "JobStreet", count = jobStreetCount)
                            PlatformRow(name = "Indeed", count = indeedCount)
                            PlatformRow(name = "Company Website", count = websiteCount)
                            PlatformRow(name = "Other", count = otherCount)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
