package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID = "kinkeep_expiry_alerts"
    private const val CHANNEL_NAME = "Document Expiry & Renewal Alerts"
    private const val CHANNEL_DESC = "Notifications for family documents reaching 1 month before expiry"

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showRenewalAlert(
        context: Context,
        docName: String,
        expiryDate: String,
        daysRemaining: Int?,
        reminderDaysBefore: Int = 30
    ) {
        ensureNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            docName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val contentText = when {
            daysRemaining != null && daysRemaining <= 30 ->
                "⚠️ EXPIRES IN $daysRemaining DAYS ($expiryDate)! Renewal action recommended."
            else ->
                "🚨 1-Month Early Alert Scheduled: $docName expires on $expiryDate. You will be notified 30 days prior."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("KinKeep Renewal Alert: $docName")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFF00F0D0.toInt())
            .build()

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(docName.hashCode(), notification)
        } catch (_: SecurityException) {
            // Notifications permission not granted on Android 13+
        }
    }
}
