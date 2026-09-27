package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

/**
 * WLNotificationHelper manages Android System Status Bar & Lock Screen Notifications.
 * Ensures notifications sent from the WonderLight Cloud Admin Panel appear directly
 * in the phone's system notification tray with sound, vibration, and tap-to-open support.
 */
object WLNotificationHelper {

    private const val TAG = "WLNotificationHelper"
    const val CHANNEL_ID = "wonderlight_travel_alerts"
    private const val CHANNEL_NAME = "WonderLight Travel Alerts"
    private const val CHANNEL_DESC = "Real-time flight status, exclusive travel discounts, booking receipts, and wallet bonuses"
    private const val PREFS_NAME = "wl_system_notifications_prefs"
    private const val KEY_SHOWN_IDS = "shown_notification_ids"

    /**
     * Creates the Android Notification Channel required on Android 8.0 (API 26) and above.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = 0xFF007788.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel '$CHANNEL_ID' created/verified.")
        }
    }

    /**
     * Checks if the app has permission to post notifications (Android 13+ / API 33+).
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Checks if the app has permission to record audio (Microphone).
     */
    fun hasMicPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Check if a specific notification ID has already been posted to the phone status bar.
     */
    fun hasNotificationBeenShown(context: Context, id: String): Boolean {
        if (id.isBlank()) return false
        val shown = getPrefs(context).getStringSet(KEY_SHOWN_IDS, emptySet()) ?: emptySet()
        return shown.contains(id)
    }

    /**
     * Mark a notification ID as shown in the system status bar.
     */
    fun markNotificationAsShown(context: Context, id: String) {
        if (id.isBlank()) return
        val prefs = getPrefs(context)
        val shown = prefs.getStringSet(KEY_SHOWN_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
        shown.add(id)
        prefs.edit().putStringSet(KEY_SHOWN_IDS, shown).apply()
    }

    /**
     * Displays an Android System Notification in the phone's notification tray / status bar.
     */
    fun showSystemNotification(
        context: Context,
        id: String? = null,
        title: String,
        body: String,
        type: String = "general",
        skipDuplicateCheck: Boolean = false
    ): Boolean {
        try {
            // Verify permission
            if (!hasNotificationPermission(context)) {
                Log.w(TAG, "Notification permission not granted. Skipping system tray notification.")
                return false
            }

            // Ensure channel exists
            createNotificationChannel(context)

            // Prevent spamming duplicate alerts for already-posted notification IDs
            if (!skipDuplicateCheck && !id.isNullOrBlank()) {
                if (hasNotificationBeenShown(context, id)) {
                    Log.d(TAG, "Notification $id already shown on phone. Skipping duplicate.")
                    return false
                }
            }

            // Intent when user taps on the phone notification
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("extra_navigate_to", "notifications")
                putExtra("extra_notif_title", title)
                putExtra("extra_notif_body", body)
                putExtra("extra_notif_type", type)
            }

            val requestCode = (System.currentTimeMillis() % 100000).toInt()
            val pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            // Determine icon / badge style
            val smallIcon = R.mipmap.ic_launcher

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .setContentIntent(pendingIntent)

            val notifManager = NotificationManagerCompat.from(context)
            val notificationId = if (!id.isNullOrBlank()) id.hashCode() else requestCode
            notifManager.notify(notificationId, builder.build())

            if (!id.isNullOrBlank()) {
                markNotificationAsShown(context, id)
            }

            Log.i(TAG, "Successfully showed system notification: '$title' (ID: $notificationId)")
            return true
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while showing notification: ${e.message}")
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying system notification: ${e.message}", e)
            return false
        }
    }

    /**
     * Trigger a quick test notification to demonstrate system notification functionality.
     */
    fun showTestNotification(context: Context): Boolean {
        return showSystemNotification(
            context = context,
            id = "test_${System.currentTimeMillis()}",
            title = "✨ WonderLight Adventure Alert!",
            body = "Phone notifications are active! You will now receive flight updates, special discounts, and booking status in your notification bar.",
            type = "promotion",
            skipDuplicateCheck = true
        )
    }
}
