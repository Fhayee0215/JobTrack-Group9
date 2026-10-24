package com.group9.jobtrack.data

data class JourneyEntry(
    val status: String,
    val date: String,
    val note: String = ""
)

data class JobApplication(
    val id: String,
    val company: String,
    val position: String,
    val platform: String,
    val dateApplied: String,
    val status: String,
    val notes: String = "",
    val journey: List<JourneyEntry> = listOf(JourneyEntry(status = "Applied", date = dateApplied))
)
