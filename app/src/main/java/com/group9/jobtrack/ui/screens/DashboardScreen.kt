package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group9.jobtrack.data.JobApplication
import com.group9.jobtrack.ui.components.ApplicationCard
import java.text.SimpleDateFormat
import java.util.*

// Figma Theme Colors
private val PrimaryBlue = Color(0xFF4F46E5)
private val BackgroundLight = Color(0xFFF8FAFC)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BorderGray = Color(0xFFE2E8F0)
private val NavInactive = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    applications: List<JobApplication>,
    onAddClick: () -> Unit,
    onApplicationClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Dashboard") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val stages = listOf("All", "Applied", "Screening", "Interview", "Offer", "Rejected")

    // Real stat counts computed from applications
    val totalCount = applications.size
    val interviewCount = applications.count { it.status.equals("Interview", ignoreCase = true) }
    val offerCount = applications.count { it.status.equals("Offer", ignoreCase = true) }
    val rejectedCount = applications.count { it.status.equals("Rejected", ignoreCase = true) }

    val filteredList = applications.filter {
        val matchesQuery = it.company.contains(searchQuery, ignoreCase = true) ||
                it.position.contains(searchQuery, ignoreCase = true)
        val matchesFilter = if (selectedFilter == "All") true else it.status.equals(selectedFilter, ignoreCase = true)
        matchesQuery && matchesFilter
    }

    Scaffold(
        containerColor = BackgroundLight,
        floatingActionButton = {
            if (selectedTab == "Dashboard" || selectedTab == "Applications") {
                ExtendedFloatingActionButton(
                    onClick = onAddClick,
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Application",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 2.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(64.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FigmaBottomNavItem(
                        icon = Icons.Outlined.GridView,
                        label = "Dashboard",
                        isSelected = selectedTab == "Dashboard",
                        onClick = { selectedTab = "Dashboard" }
                    )
                    FigmaBottomNavItem(
                        icon = Icons.Outlined.WorkOutline,
                        label = "Applications",
                        isSelected = selectedTab == "Applications",
                        onClick = { selectedTab = "Applications" }
                    )
                    FigmaBottomNavItem(
                        icon = Icons.Outlined.Notifications,
                        label = "Alerts",
                        isSelected = selectedTab == "Alerts",
                        onClick = { selectedTab = "Alerts" }
                    )
                    FigmaBottomNavItem(
                        icon = Icons.Outlined.Assessment,
                        label = "Analytics",
                        isSelected = selectedTab == "Analytics",
                        onClick = { selectedTab = "Analytics" }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            "Dashboard", "Applications" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (selectedTab == "Dashboard") {
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
                                    color = TextSecondary
                                )
                                Text(
                                    text = "John Doe",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
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
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        // 2. Metrics Summary (2x2 Grid)
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
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "All Applications",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // 3. Search and Status Filter Chips (ALWAYS VISIBLE AT THE TOP)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by company or position", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderGray
                            ),
                            singleLine = true
                        )

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(stages) { stage ->
                                FilterChip(
                                    selected = selectedFilter == stage,
                                    onClick = { selectedFilter = stage },
                                    label = { Text(stage, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // 4. Section Title & Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedTab == "Dashboard") "Recent Applications" else "Application Records",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${filteredList.size} found",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    // 5. Scrolling Application Cards List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (filteredList.isEmpty()) {
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
                                        color = TextSecondary
                                    )
                                }
                            }
                        } else {
                            items(filteredList, key = { it.id }) { app ->
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

            "Alerts" -> {
                val interviewApps = applications.filter { it.status.equals("Interview", ignoreCase = true) }
                val offerApps = applications.filter { it.status.equals("Offer", ignoreCase = true) }
                val appliedApps = applications.filter { it.status.equals("Applied", ignoreCase = true) || it.status.equals("Screening", ignoreCase = true) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Notifications & Reminders",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

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

            "Analytics" -> {
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
                val avgResponseDays = if (respondedAppsCount > 0) totalResponseDays / respondedAppsCount else 4

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
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // STICKY HEADER (Title & Platform Filter Chips)
                    Spacer(modifier = Modifier.height(8.dp))
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
                                        subtext = "Avg response: ${avgResponseDays}d",
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
                                                verticalArrangement = Arrangement.Bottom,
                                                modifier = Modifier.fillMaxHeight()
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
        }
    }
}

@Composable
private fun NotificationCard(
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

@Composable
private fun PipelineBar(label: String, count: Int, progress: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.width(70.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(BorderGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color)
            )
        }
        Text(text = count.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.width(24.dp), textAlign = TextAlign.End)
    }
}

@Composable
private fun PlatformRow(name: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, fontSize = 13.sp, color = TextPrimary)
        Text(text = count.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun RowScope.FigmaBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PrimaryBlue else NavInactive,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) PrimaryBlue else NavInactive
        )
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.heightIn(min = 105.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun StatCardWithSubtext(title: String, value: String, subtext: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.heightIn(min = 105.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (subtext.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
