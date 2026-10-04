package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ExpirationUtils {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun parseDate(dateStr: String): Long? {
        return try {
            dateFormat.parse(dateStr)?.time
        } catch (_: Exception) {
            null
        }
    }

    fun calculateDaysRemaining(expiryTimestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = expiryTimestamp - now
        return (diff / (1000L * 60 * 60 * 24)).toInt()
    }

    fun getTimestampAfterDays(days: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.timeInMillis
    }

    fun isExpiringWithinDays(daysRemaining: Int?, thresholdDays: Int = 30): Boolean {
        if (daysRemaining == null) return false
        return daysRemaining in 0..thresholdDays
    }

    fun isExpired(daysRemaining: Int?): Boolean {
        if (daysRemaining == null) return false
        return daysRemaining < 0
    }
}
