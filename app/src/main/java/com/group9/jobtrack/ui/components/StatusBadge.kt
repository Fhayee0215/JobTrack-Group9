package com.group9.jobtrack.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "Applied" -> Color(0xFFE0E7FF) to Color(0xFF3730A3)
        "Screening" -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
        "Interview" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        "Offer" -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        "Rejected" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
