package com.example.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM Registration Token: $token")
        // Store or transmit token to backend server
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a notification payload
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "🎉 50% Reward Released!"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "Your 3-day savings lock period has completed! Your principal deposit + 50% profit yield is now available in your wallet."

        // Trigger system heads-up notification
        NotificationHelper.sendRewardNotification(
            context = applicationContext,
            title = title,
            message = body
        )
    }

    companion object {
        private const val TAG = "FCM_Service"
    }
}
