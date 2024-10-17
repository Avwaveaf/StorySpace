package com.avwaveaf.storyspace.helper


import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

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

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}