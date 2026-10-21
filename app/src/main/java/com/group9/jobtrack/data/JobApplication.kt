package com.group9.jobtrack.data

data class JobApplication(
    val id: String,
    val company: String,
    val position: String,
    val platform: String,
    val dateApplied: String,
    val status: String,
    val notes: String = ""
)