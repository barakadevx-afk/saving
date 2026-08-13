package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    user: UserEntity,
    wallet: WalletEntity?,
    cycles: List<SavingsCycleEntity>,
    transactions: List<TransactionEntity>,
    adminConfig: AdminConfigEntity?,
    strings: AppStrings,
    onOpenDepositModal: (String) -> Unit,
    onFastForward: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    val available = wallet?.availableBalance ?: 12450.0
    val locked = wallet?.lockedBalance ?: 45000.0
    val earned = wallet?.totalEarned ?: 6750.0
    val deposited = wallet?.totalDeposited ?: 70000.0
    val withdrawn = wallet?.totalWithdrawn ?: 23300.0
    val referralBonus = wallet?.referralBonus ?: 1250.0

    val activeCycle = cycles.find { it.status == CycleStatus.ACTIVE_LOCK } ?: cycles.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Date
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${strings.welcomeBack}, ${user.fullName.split(" ").firstOrNull() ?: "Jean"}! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = strings.subtitleHero,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoCardBg,
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date()),
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hero Bento Available Balance Card
        item {
            BentoHeroBalanceCard(
                availableBalance = available,
                strings = strings,
                onOpenDepositModal = { onOpenDepositModal("B") }
            )
        }

        // Bento Grid Stat Cards (3 Cards in 2 rows or Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatOverviewCard(
                        title = strings.lockedBalance,
                        value = "%,d RWF".format(locked.toInt()),
                        subtitle = strings.inActiveCycles,
                        icon = Icons.Default.Lock,
                        iconBgColor = BentoLockedBadgeBg,
                        iconTint = BentoLockedBadgeText,
                        modifier = Modifier.weight(1f)
                    )

                    StatOverviewCard(
                        title = strings.totalEarned,
                        value = "%,d RWF".format(earned.toInt()),
                        subtitle = strings.allTimeEarnings,
                        icon = Icons.Default.TrendingUp,
                        iconBgColor = BentoEarnedBadgeBg,
                        iconTint = BentoEarnedBadgeText,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatOverviewCard(
                        title = strings.activeCycles,
                        value = "${cycles.count { it.status == CycleStatus.ACTIVE_LOCK }}",
                        subtitle = strings.runningCycles,
                        icon = Icons.Default.Autorenew,
                        iconBgColor = BentoBadgeGreyBg,
                        iconTint = BentoHeroText,
                        modifier = Modifier.weight(1f)
                    )

                    StatOverviewCard(
                        title = "Referral Bonus",
                        value = "%,d RWF".format(referralBonus.toInt()),
                        subtitle = "Friend rewards",
                        icon = Icons.Default.CardGiftcard,
                        iconBgColor = GoldLight,
                        iconTint = OrangeWarning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Bento Visual Earnings Analytics Chart Card
        item {
            BentoEarningsChartCard(
                earnedAmount = earned
            )
        }

        // Active Savings Cycle Dark Bento Card
        item {
            if (activeCycle != null) {
                ActiveCycleProgressCard(
                    cycle = activeCycle,
                    strings = strings,
                    onFastForward = onFastForward
                )
            }
        }

        // Choose a Savings Plan Section Bento Style
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = strings.chooseSavingsPlan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = strings.choosePlanSubtitle,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                TierSelectionCard(
                                    tierName = "6,000 RWF",
                                    amount = 6000.0,
                                    ratePct = adminConfig?.rateTierA ?: 0.02,
                                    strings = strings,
                                    isPopular = false,
                                    onChoosePlan = { onOpenDepositModal("A") }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                TierSelectionCard(
                                    tierName = "10,000 RWF",
                                    amount = 10000.0,
                                    ratePct = adminConfig?.rateTierB ?: 0.02,
                                    strings = strings,
                                    isPopular = true,
                                    onChoosePlan = { onOpenDepositModal("B") }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                TierSelectionCard(
                                    tierName = "15,000 RWF",
                                    amount = 15000.0,
                                    ratePct = adminConfig?.rateTierC ?: 0.02,
                                    strings = strings,
                                    isPopular = false,
                                    onChoosePlan = { onOpenDepositModal("C") }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                TierSelectionCard(
                                    tierName = "45,000 RWF",
                                    amount = 45000.0,
                                    ratePct = adminConfig?.rateTierD ?: 0.02,
                                    strings = strings,
                                    isPopular = false,
                                    onChoosePlan = { onOpenDepositModal("D") }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Transparency Notice Banner
        item {
            TransparencyNoticeBanner(text = strings.transparencyNotice)
        }

        // Account Summary Panel Bento Style
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoDarkCardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = strings.growSavingsPromoTitle,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.growSavingsPromoBody,
                        color = BentoDarkCardText.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { onOpenDepositModal("C") },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue, contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("promo_deposit_btn")
                        ) {
                            Text(text = strings.depositNow, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onWithdrawClick,
                            colors = ButtonDefaults.buttonColors(containerColor = BentoBadgeGreyBg, contentColor = BentoHeroText),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("promo_withdraw_btn")
                        ) {
                            Text(text = strings.withdraw, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = strings.accountSummary,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SummaryRow(label = strings.totalDeposited, value = "%,d RWF".format(deposited.toInt()), valueColor = Color.White)
                    SummaryRow(label = strings.totalEarned, value = "%,d RWF".format(earned.toInt()), valueColor = GreenSuccess)
                    SummaryRow(label = strings.totalWithdrawn, value = "%,d RWF".format(withdrawn.toInt()), valueColor = RedDanger)
                    SummaryRow(label = strings.referralBonus, value = "%,d RWF".format(referralBonus.toInt()), valueColor = GoldAccent)
                }
            }
        }

        // Recent Transactions Section Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.recentTransactions,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = (-0.5).sp
                        )

                        TextButton(
                            onClick = onViewAllTransactions,
                            modifier = Modifier.testTag("view_all_tx_btn")
                        ) {
                            Text(text = strings.viewAll, color = BentoPrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (transactions.isEmpty()) {
                        Text(
                            text = "No recent transactions found.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            transactions.take(5).forEach { tx ->
                                TransactionItemRow(tx = tx)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondaryDark, fontSize = 13.sp)
        Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TransactionItemRow(tx: TransactionEntity) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
    val isPositive = tx.amount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBackground, shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (tx.type) {
                            TransactionType.DEPOSIT -> BlueLight
                            TransactionType.CYCLE_REWARD -> GreenLight
                            TransactionType.WITHDRAWAL -> Color(0xFFFEE2E2)
                            else -> GoldLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (tx.type) {
                        TransactionType.DEPOSIT -> Icons.Default.ArrowDownward
                        TransactionType.CYCLE_REWARD -> Icons.Default.CardGiftcard
                        TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward
                        else -> Icons.Default.SwapHoriz
                    },
                    contentDescription = null,
                    tint = when (tx.type) {
                        TransactionType.DEPOSIT -> BluePrimary
                        TransactionType.CYCLE_REWARD -> GreenSuccess
                        TransactionType.WITHDRAWAL -> RedDanger
                        else -> OrangeWarning
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = tx.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = (if (isPositive) "+" else "") + "%,d RWF".format(tx.amount.toInt()),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) GreenSuccess else TextPrimary
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (tx.status) {
                    TransactionStatus.COMPLETED, TransactionStatus.APPROVED -> GreenLight
                    TransactionStatus.LOCKED -> Color(0xFFF3E8FF)
                    TransactionStatus.PENDING -> GoldLight
                    TransactionStatus.REJECTED -> Color(0xFFFEE2E2)
                }
            ) {
                Text(
                    text = tx.status.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (tx.status) {
                        TransactionStatus.COMPLETED, TransactionStatus.APPROVED -> GreenSuccess
                        TransactionStatus.LOCKED -> Color(0xFF9333EA)
                        TransactionStatus.PENDING -> OrangeWarning
                        TransactionStatus.REJECTED -> RedDanger
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
