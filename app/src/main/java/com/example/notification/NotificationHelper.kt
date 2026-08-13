package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_ID = "rewards_channel"
    private const val CHANNEL_NAME = "3-Day Yield Lock Rewards"
    private const val CHANNEL_DESC = "Notifications when deposit lock periods end and 50% rewards are released."

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendRewardNotification(context: Context, title: String, message: String, notificationId: Int = (System.currentTimeMillis() % 10000).toInt()) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    fun scheduleCycleExpirationNotification(
        context: Context,
        cycleId: String,
        depositAmount: Double,
        expectedReward: Double,
        triggerAtMillis: Long
    ) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, CycleExpirationReceiver::class.java).apply {
            putExtra("EXTRA_CYCLE_ID", cycleId)
            putExtra("EXTRA_DEPOSIT_AMOUNT", depositAmount)
            putExtra("EXTRA_EXPECTED_REWARD", expectedReward)
        }

        val requestCode = cycleId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = System.currentTimeMillis()
        if (triggerAtMillis <= now) {
            // Cycle has already expired, send local notification immediately
            val total = depositAmount + expectedReward
            sendRewardNotification(
                context = context,
                title = "🎉 3-Day Savings Lock Expired!",
                message = "Your lock period for $cycleId has ended! %,d RWF total (Principal + 50%% profit yield) is unlocked and available for withdrawal.".format(total.toInt()),
                notificationId = requestCode
            )
            return
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Log.d("NotificationHelper", "Scheduled local notification for cycle $cycleId at $triggerAtMillis")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Failed to schedule alarm notification: ${e.message}")
        }
    }

    fun cancelCycleExpirationNotification(context: Context, cycleId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, CycleExpirationReceiver::class.java)
        val requestCode = cycleId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}

