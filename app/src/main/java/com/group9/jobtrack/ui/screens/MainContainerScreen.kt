package com.group9.jobtrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group9.jobtrack.data.JobApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainerScreen(
    initialTab: String = "Dashboard",
    applications: List<JobApplication>,
    onAddClick: () -> Unit,
    onApplicationClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (selectedTab == "Dashboard" || selectedTab == "Applications") {
                ExtendedFloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
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
                color = MaterialTheme.colorScheme.surface,
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
                    MainBottomNavItem(
                        icon = Icons.Outlined.GridView,
                        label = "Dashboard",
                        isSelected = selectedTab == "Dashboard",
                        onClick = { selectedTab = "Dashboard" }
                    )
                    MainBottomNavItem(
                        icon = Icons.Outlined.WorkOutline,
                        label = "Applications",
                        isSelected = selectedTab == "Applications",
                        onClick = { selectedTab = "Applications" }
                    )
                    MainBottomNavItem(
                        icon = Icons.Outlined.Notifications,
                        label = "Alerts",
                        isSelected = selectedTab == "Alerts",
                        onClick = { selectedTab = "Alerts" }
                    )
                    MainBottomNavItem(
                        icon = Icons.Outlined.Assessment,
                        label = "Analytics",
                        isSelected = selectedTab == "Analytics",
                        onClick = { selectedTab = "Analytics" }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                "Dashboard" -> DashboardTabScreen(
                    applications = applications,
                    onApplicationClick = onApplicationClick
                )
                "Applications" -> ApplicationsTabScreen(
                    applications = applications,
                    onApplicationClick = onApplicationClick
                )
                "Alerts" -> AlertsScreen(
                    applications = applications
                )
                "Analytics" -> AnalyticsScreen(
                    applications = applications
                )
            }
        }
    }
}

@Composable
private fun RowScope.MainBottomNavItem(
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
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
    }
}
