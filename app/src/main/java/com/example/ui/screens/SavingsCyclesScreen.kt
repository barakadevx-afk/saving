package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.AppCurrency
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
    selectedCurrency: AppCurrency = AppCurrency.RWF,
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

        // Transaction & Cycle State Indicator Confidence Legend
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CYCLE LIFECYCLE INDICATORS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Maturing
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3B82F6))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Maturing (72h)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        // Processing
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Processing", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        // Completed
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(GreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Completed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        // Flagged
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Flagged", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
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
                    selectedCurrency = selectedCurrency,
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
                PastCycleCard(cycle = cycle, strings = strings, selectedCurrency = selectedCurrency)
            }
        }
    }
}

@Composable
fun PastCycleCard(cycle: SavingsCycleEntity, strings: AppStrings, selectedCurrency: AppCurrency = AppCurrency.RWF) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(cycle.startDate))
    val settledStr = if (cycle.settledAt != null) SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(cycle.settledAt)) else "Settled"

    val (statusLabel, statusBg, statusText, statusBorder, statusIcon) = when (cycle.status) {
        CycleStatus.COMPLETED -> StatusTuple("COMPLETED ✅", Color(0xFFD1FAE5), Color(0xFF065F46), Color(0xFF6EE7B7), Icons.Default.CheckCircle)
        CycleStatus.AVAILABLE -> StatusTuple("SETTLED 🎁", Color(0xFFD1FAE5), Color(0xFF065F46), Color(0xFF6EE7B7), Icons.Default.CheckCircle)
        CycleStatus.ACTIVE_LOCK -> StatusTuple("MATURING ⚡", Color(0xFFE0E7FF), Color(0xFF1E3A8A), Color(0xFF93C5FD), Icons.Default.Timer)
    }

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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(statusBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusText,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = "Cycle #${cycle.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Started: $dateStr", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Text(text = "Settled: $settledStr", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = selectedCurrency.format(cycle.depositAmount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "+${selectedCurrency.format(cycle.expectedReward)} Profit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenSuccess
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

private data class StatusTuple(
    val label: String,
    val bg: Color,
    val text: Color,
    val border: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
