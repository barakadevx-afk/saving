package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.CycleStatus
import com.example.data.model.SavingsCycleEntity
import com.example.ui.components.ActiveCycleProgressCard
import com.example.ui.components.TransparencyNoticeBanner
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavingsCyclesScreen(
    cycles: List<SavingsCycleEntity>,
    strings: AppStrings,
    onFastForward: () -> Unit,
    onOpenDepositModal: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.savingsCycles,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Track your time-locked 3-day savings cycles and settlement rewards.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onFastForward,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("cycles_fast_forward_btn")
                ) {
                    Text(text = "Fast Forward ⚡", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            TransparencyNoticeBanner(text = strings.transparencyNotice)
        }

        val activeCycles = cycles.filter { it.status == CycleStatus.ACTIVE_LOCK }
        val pastCycles = cycles.filter { it.status != CycleStatus.ACTIVE_LOCK }

        item {
            Text(
                text = "Active Locked Cycles (${activeCycles.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.3).sp
            )
        }

        if (activeCycles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Active Savings Cycles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Start a 3-day cycle today to earn system-calculated rewards!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onOpenDepositModal,
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = strings.depositNow, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(activeCycles) { cycle ->
                ActiveCycleProgressCard(
                    cycle = cycle,
                    strings = strings,
                    onFastForward = onFastForward
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Completed & Available Cycles (${pastCycles.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.3).sp
            )
        }

        if (pastCycles.isEmpty()) {
            item {
                Text(
                    text = "No completed cycles yet.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        } else {
            items(pastCycles) { cycle ->
                PastCycleCard(cycle = cycle, strings = strings)
            }
        }
    }
}

@Composable
fun PastCycleCard(cycle: SavingsCycleEntity, strings: AppStrings) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(cycle.startDate))
    val settledStr = if (cycle.settledAt != null) SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(cycle.settledAt)) else "Completed"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Cycle ${cycle.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                Text(text = "Started: $dateStr", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Text(text = "Settled: $settledStr", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%,d RWF".format(cycle.depositAmount.toInt()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "+%,d RWF Reward".format(cycle.expectedReward.toInt()),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenSuccess
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoEarnedBadgeBg
                ) {
                    Text(
                        text = "COMPLETED ✅",
                        color = BentoEarnedBadgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
