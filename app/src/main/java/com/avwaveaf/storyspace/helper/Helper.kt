package com.avwaveaf.storyspace.helper


import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(dateString: String, locale: Locale = Locale.getDefault()): String {
    return try {
        // Parse the input date string
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        val date = inputFormat.parse(dateString) ?: return "Invalid Date"

        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", locale)
        outputFormat.format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }
}