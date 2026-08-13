package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CycleExpirationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val cycleId = intent.getStringExtra("EXTRA_CYCLE_ID") ?: "Savings Cycle"
        val depositAmount = intent.getDoubleExtra("EXTRA_DEPOSIT_AMOUNT", 0.0)
        val expectedReward = intent.getDoubleExtra("EXTRA_EXPECTED_REWARD", 0.0)
        val totalAmount = depositAmount + expectedReward

        Log.d("CycleExpirationReceiver", "Cycle $cycleId lock period expired! Triggering local notification...")

        val title = "🎉 3-Day Savings Lock Expired!"
        val message = if (totalAmount > 0) {
            "Your lock period for cycle $cycleId has ended! %,d RWF (Principal + 50%% profit) is now unlocked and available in your wallet for withdrawal.".format(totalAmount.toInt())
        } else {
            "Your 3-day savings cycle lock period has ended! Principal + 50% profit yield is unlocked and ready for withdrawal."
        }

        NotificationHelper.sendRewardNotification(
            context = context,
            title = title,
            message = message,
            notificationId = cycleId.hashCode()
        )
    }
}
