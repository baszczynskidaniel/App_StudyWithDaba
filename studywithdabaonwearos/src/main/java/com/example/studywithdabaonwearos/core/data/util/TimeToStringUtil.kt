package com.example.studywithdaba.core.data.util

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toTimeDateString(pattern: String): String {
    val dateTime = java.util.Date(this)
    val format = SimpleDateFormat(pattern, Locale.US)
    return format.format(dateTime)
}