package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    referredUsers: List<UserEntity> = emptyList(),
    announcements: List<AnnouncementEntity> = emptyList(),
    selectedCurrency: AppCurrency = AppCurrency.RWF,
    isLoading: Boolean = false,
    onOpenDepositModal: (String) -> Unit,
    onFastForward: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onWithdrawClick: () -> Unit,
    onFaqClick: () -> Unit = {},
    onWebDownloadClick: () -> Unit = {},
    onOpenAnnouncements: () -> Unit = {},
    onSimulateReferral: (String) -> Unit = {},
    onClaimWelcomeBonus: () -> Unit = {}
) {
    if (isLoading) {
        BentoDashboardSkeletonScreen()
        return
    }

    val available = wallet?.availableBalance ?: 12450.0
    val locked = wallet?.lockedBalance ?: 45000.0
    val earned = wallet?.totalEarned ?: 6750.0
    val deposited = wallet?.totalDeposited ?: 70000.0
    val withdrawn = wallet?.totalWithdrawn ?: 23300.0
    val referralBonus = wallet?.referralBonus ?: 1250.0
    val hasClaimedBonus = wallet?.hasClaimedWelcomeBonus ?: false

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

        // 🎁 Welcome Starter Bonus Card (1,000 RWF Free Starter Funds)
        if (!hasClaimedBonus) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_bonus_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF001A40)),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(GoldAccent, Color(0xFFFBBF24), GoldAccent))
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(GoldAccent.copy(alpha = 0.22f), Color.Transparent),
                                    center = Offset(size.width * 0.9f, size.height * 0.2f),
                                    radius = size.width * 0.5f
                                )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldAccent,
                                    contentColor = NavyDark
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Celebration,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = NavyDark
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "FREE WELCOME GIFT 🎁",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                                ) {
                                    Text(
                                        text = "Instant 1,000 RWF ⚡",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            Brush.linearGradient(listOf(GoldAccent, Color(0xFFD97706))),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = "Welcome Bonus",
                                        tint = NavyDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "1,000 RWF Welcome Bonus",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = (-0.3).sp
                                    )
                                    Text(
                                        text = "Claim your free starter funds now and add them directly to your available balance!",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.82f),
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onClaimWelcomeBonus,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("claim_welcome_bonus_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent,
                                    contentColor = NavyDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Savings,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = NavyDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Claim 1,000 RWF & Add to Funds 🎉",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = NavyDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Latest Announcement Marquee Banner
        if (announcements.isNotEmpty()) {
            item {
                val latest = announcements.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenAnnouncements() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (latest.isImportant) Color(0xFFFEF3C7) else Color(0xFFEFF6FF)
                    ),
                    border = BorderStroke(1.dp, if (latest.isImportant) GoldAccent else BluePrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (!latest.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = latest.imageUrl,
                                    contentDescription = latest.title,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (latest.isImportant) GoldAccent else BluePrimary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Campaign,
                                        contentDescription = "Announcement",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ANNOUNCEMENT 📢",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (latest.isImportant) GoldDark else BluePrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• Tap to view all",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = latest.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "View",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Platform Lock Maintenance Banner (if admin lock is active)
        if (adminConfig?.isPlatformLocked == true) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.5.dp, RedError)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = RedError, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Platform Deposit Status: TEMPORARILY LOCKED 🔒",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = RedError
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = adminConfig.lockNotice,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenLight
                        ) {
                            Text(
                                text = "✅ User Profits Guaranteed: Active savings cycles continue earning yields uninterrupted ('profit of user is income it').",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenSuccess,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 20 Million RWF Capital Reserve Transparency Banner
        item {
            val reserveAmt = adminConfig?.adminReserveFund ?: 20000000.0
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GoldAccent.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "System Liquidity Reserve 💰",
                                fontSize = 11.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%,.0f RWF (20M Reserve)".format(reserveAmt),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAccent,
                        contentColor = NavyDark
                    ) {
                        Text(
                            text = "100% Backed",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                selectedCurrency = selectedCurrency,
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
                        value = selectedCurrency.format(locked),
                        subtitle = strings.inActiveCycles,
                        icon = Icons.Default.Lock,
                        iconBgColor = BentoLockedBadgeBg,
                        iconTint = BentoLockedBadgeText,
                        modifier = Modifier.weight(1f)
                    )

                    StatOverviewCard(
                        title = strings.totalEarned,
                        value = selectedCurrency.format(earned),
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
                        value = selectedCurrency.format(referralBonus),
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
                    selectedCurrency = selectedCurrency,
                    onFastForward = onFastForward
                )
            }
        }

        // Referral Program & Invite Friends Bento Card
        item {
            ReferralProgramCard(
                user = user,
                wallet = wallet,
                referralBonus = referralBonus,
                referredUsers = referredUsers,
                onSimulateReferral = onSimulateReferral
            )
        }

        // Quick FAQ & Help Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFaqClick() }
                    .testTag("dashboard_faq_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GoldLight),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(GoldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = NavyDark, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Have questions about the 3-day cycle?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Read our FAQ guide on deposit limits, lock periods & verification.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OrangeWarning)
                }
            }
        }

        // Web & Windows Desktop Portal Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onWebDownloadClick() }
                    .testTag("dashboard_web_download_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(GoldAccent.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = GoldAccent)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Web Portal & Windows App 💻🌐",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Access on browser or download Windows PC app (.exe)",
                                fontSize = 11.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Icon(Icons.Default.Download, contentDescription = null, tint = GoldAccent)
                }
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

                    SummaryRow(label = strings.totalDeposited, value = selectedCurrency.format(deposited), valueColor = Color.White)
                    SummaryRow(label = strings.totalEarned, value = selectedCurrency.format(earned), valueColor = GreenSuccess)
                    SummaryRow(label = strings.totalWithdrawn, value = selectedCurrency.format(withdrawn), valueColor = RedDanger)
                    SummaryRow(label = strings.referralBonus, value = selectedCurrency.format(referralBonus), valueColor = GoldAccent)
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

@Composable
fun ReferralProgramCard(
    user: UserEntity,
    wallet: WalletEntity?,
    referralBonus: Double,
    referredUsers: List<UserEntity>,
    onSimulateReferral: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val referralLink = "https://futuresmartcapital.rw/ref/${user.referralCode}"
    val pendingBonusRate = wallet?.pendingBonusPercent ?: 0.0
    val bonusPercentString = "+%.1f%%".format(pendingBonusRate * 100)

    Card(
        modifier = Modifier.fillMaxWidth().testTag("referral_program_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(GoldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Referral",
                            tint = OrangeWarning,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "🎁 Refer a Friend & Earn Cycle Boost",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Earn +0.5% bonus profit percentage on your next savings cycle!",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bonus Percentage Cycle Boost Highlight Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (pendingBonusRate > 0) GoldLight else LightBackground,
                border = BorderStroke(1.dp, if (pendingBonusRate > 0) GoldAccent else BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(if (pendingBonusRate > 0) OrangeWarning.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Cycle Boost",
                                tint = if (pendingBonusRate > 0) OrangeWarning else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Next-Cycle Profit Boost",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (pendingBonusRate > 0) "Applied automatically on your next deposit!" else "Invite a friend to activate +0.5% boost",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (pendingBonusRate > 0) OrangeWarning else Color.Gray
                    ) {
                        Text(
                            text = if (pendingBonusRate > 0) "$bonusPercentString BOOST 🔥" else "0.0% BOOST",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Unique Referral Code & Link Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BentoHeroCardBg,
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "YOUR UNIQUE REFERRAL LINK:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = referralLink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoPrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Referral Link", referralLink)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Referral link copied to clipboard! 📋", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("copy_ref_link_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT,
                                        "Join Future Smart Capital and earn guaranteed profit yields in 3 days! Register with my referral link: $referralLink"
                                    )
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Invite Friends via"))
                            },
                            modifier = Modifier.weight(1f).testTag("share_ref_link_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Demo / Test Action: Simulate Friend Referral
            OutlinedButton(
                onClick = {
                    val randomFriends = listOf("Eric Mugisha", "Aline Uwase", "Patrick Hakizimana", "Diane Keza", "Jean-Paul Habimana")
                    onSimulateReferral(randomFriends.random())
                },
                modifier = Modifier.fillMaxWidth().testTag("simulate_referral_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GoldAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeWarning)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("⚡ Test Referral: Simulate Friend Joining", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatOverviewCard(
                    title = "Friends Invited",
                    value = "${referredUsers.size}",
                    subtitle = "Registered",
                    icon = Icons.Default.Group,
                    iconBgColor = BlueLight,
                    iconTint = BluePrimary,
                    modifier = Modifier.weight(1f)
                )

                StatOverviewCard(
                    title = "Next Boost",
                    value = bonusPercentString,
                    subtitle = "Cycle Yield Rate",
                    icon = Icons.Default.Bolt,
                    iconBgColor = GoldLight,
                    iconTint = OrangeWarning,
                    modifier = Modifier.weight(1f)
                )

                StatOverviewCard(
                    title = "Instant Cash",
                    value = "%,d RWF".format(referralBonus.toInt()),
                    subtitle = "Wallet Bonus",
                    icon = Icons.Default.MonetizationOn,
                    iconBgColor = GreenLight,
                    iconTint = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            // Invite History List
            if (referredUsers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Invited Friends (${referredUsers.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    referredUsers.forEach { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightBackground, shape = RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(BlueLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = friend.fullName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = friend.fullName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Phone: ${friend.phone}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GreenLight
                            ) {
                                Text(
                                    text = "+0.5% Yield & 1k RWF ⚡",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "💡 Tip: Share your unique referral link to earn +0.5% bonus percentage on your next savings cycle for every friend who joins!",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
