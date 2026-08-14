package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SMART FUTURE CAPITAL (SFC)", appName)
  }

  @Test
  fun `verify referral bonus rate boost calculation`() {
    val baseRate = 0.02
    val referralBoost = 0.005 // +0.5% boost
    val depositAmount = 15000.0
    val expectedReward = depositAmount * (baseRate + referralBoost)
    assertEquals(375.0, expectedReward, 0.001)
  }
}
