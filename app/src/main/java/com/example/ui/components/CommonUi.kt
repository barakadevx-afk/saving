package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.i18n.AppStrings
import com.example.data.model.Language
import com.example.data.model.SavingsCycleEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserRole
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun TopHeaderBar(
    strings: AppStrings,
    currentLanguage: Language,
    userRole: UserRole,
    userName: String,
    onLanguageChange: (Language) -> Unit,
    onToggleRole: () -> Unit,
    onOpenNav: () -> Unit
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BentoBg,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenNav,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BentoBadgeGreyBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Navigation Menu",
                        tint = BentoHeroText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BentoPrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "B",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = strings.appTitle,
                        color = BentoHeroText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language Selector Dropdown
                Box {
                    Button(
                        onClick = { languageMenuExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoBadgeGreyBg,
                            contentColor = BentoHeroText
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        modifier = Modifier.testTag("language_selector_btn")
                    ) {
                        val flag = when (currentLanguage) {
                            Language.EN -> "🇬🇧 EN"
                            Language.RW -> "🇷🇼 RW"
                            Language.FR -> "🇫🇷 FR"
                        }
                        Text(text = flag, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Language",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = languageMenuExpanded,
                        onDismissRequest = { languageMenuExpanded = false },
                        modifier = Modifier.background(BentoCardBg)
                    ) {
                        DropdownMenuItem(
                            text = { Text("🇬🇧 English", color = TextPrimary, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onLanguageChange(Language.EN)
                                languageMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇷🇼 Kinyarwanda", color = TextPrimary, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onLanguageChange(Language.RW)
                                languageMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇫🇷 Français", color = TextPrimary, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onLanguageChange(Language.FR)
                                languageMenuExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Role Switcher Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (userRole == UserRole.ADMIN) OrangeWarning else BentoHeroCardBg)
                        .clickable { onToggleRole() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("role_toggle_btn")
                ) {
                    Text(
                        text = if (userRole == UserRole.ADMIN) "ADMIN ⚡" else "USER",
                        color = if (userRole == UserRole.ADMIN) Color.White else BentoHeroText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // User Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoHeroCardBg)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(2).uppercase(),
                        color = BentoHeroText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BentoHeroBalanceCard(
    availableBalance: Double,
    strings: AppStrings,
    onOpenDepositModal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = BentoHeroCardBg),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.8f),
                    Color.White.copy(alpha = 0.2f),
                    BentoPrimaryBlue.copy(alpha = 0.3f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.45f)
                ) {
                    Text(
                        text = strings.availableBalance.uppercase(),
                        color = BentoHeroText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onOpenDepositModal,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.6f))
                        .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Deposit",
                        tint = BentoHeroText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "%,d RWF".format(availableBalance.toInt()),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = BentoHeroText,
                letterSpacing = (-0.8).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GreenSuccess)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+2.4% from last reward settlement",
                    fontSize = 12.sp,
                    color = BentoHeroText.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StatOverviewCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoBadgeGreyBg.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = title.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ActiveCycleProgressCard(
    cycle: SavingsCycleEntity,
    strings: AppStrings,
    onFastForward: () -> Unit
) {
    val depositFormatted = "%,d RWF".format(cycle.depositAmount.toInt())
    val rewardFormatted = "%,d RWF".format(cycle.expectedReward.toInt())
    val ratePct = "%.2f%%".format(cycle.rate * 100)

    val now = System.currentTimeMillis()
    val totalDuration = (cycle.endDate - cycle.startDate).coerceAtLeast(1L)
    val remaining = (cycle.endDate - now).coerceAtLeast(0L)
    val progress = 1f - (remaining.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)

    val remainingHours = (remaining / (1000 * 3600)).toInt()
    val daysLeft = remainingHours / 24

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = BentoDarkCardBg),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.05f),
                    BentoPrimaryBlue.copy(alpha = 0.4f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACTIVE CYCLE",
                        color = BentoDarkCardText.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tier $depositFormatted Savings",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoPrimaryBlue
                ) {
                    Text(
                        text = "$daysLeft DAYS LEFT",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Progress Bar Bento Style
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = BentoPrimaryBlue,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DAY 1: LOCKED",
                        color = BentoDarkCardText.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "DAY 3: RELEASE",
                        color = BentoPrimaryBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Est. Reward: +$rewardFormatted ($ratePct)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(GreenSuccess)
                        )
                        Text(
                            text = "PROCESSING IN TIME LOCK",
                            color = GreenSuccess,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = onFastForward,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyDark),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("fast_forward_btn")
                ) {
                    Text(text = "Fast Forward ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TierSelectionCard(
    tierName: String, // e.g. "6,000 RWF"
    amount: Double,
    ratePct: Double,
    strings: AppStrings,
    isPopular: Boolean = false,
    onChoosePlan: () -> Unit
) {
    val estReward = (amount * ratePct).toInt()
    val rewardFormatted = "%,d RWF".format(estReward)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) BlueLight else LightSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isPopular) listOf(BluePrimary, GoldAccent) else listOf(LightBorder, LightBorder)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (isPopular) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BluePrimary
                ) {
                    Text(
                        text = strings.popularBadge,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = tierName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Est. Reward: $rewardFormatted",
                fontSize = 12.sp,
                color = GreenSuccess,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "%.2f%% in 3 days".format(ratePct * 100),
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onChoosePlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("choose_plan_${tierName.replace(" ", "_")}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) BluePrimary else LightBackground,
                    contentColor = if (isPopular) Color.White else TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = strings.choosePlanBtn, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun DepositModalDialog(
    selectedTier: String,
    strings: AppStrings,
    availableBalance: Double,
    onDismiss: () -> Unit,
    onConfirmDeposit: (payFromAvailable: Boolean) -> Unit
) {
    var payFromAvailable by remember { mutableStateOf(false) }
    var agreedDisclaimer by remember { mutableStateOf(false) }

    val amount = when (selectedTier) {
        "A" -> 6000.0
        "B" -> 10000.0
        "C" -> 15000.0
        "D" -> 45000.0
        else -> 15000.0
    }
    val estReward = (amount * 0.02).toInt()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Deposit into Savings Cycle",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlueLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Tier $selectedTier Deposit", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "%,d RWF".format(amount.toInt()),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estimated Reward: +%,d RWF (2.00%% in 3 days)".format(estReward),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenSuccess
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Payment Source", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !payFromAvailable,
                        onClick = { payFromAvailable = false }
                    )
                    Text(text = "Mobile Money / External Deposit", fontSize = 13.sp, color = TextPrimary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = payFromAvailable,
                        onClick = { payFromAvailable = true }
                    )
                    Text(
                        text = "Available Balance (%,d RWF)".format(availableBalance.toInt()),
                        fontSize = 13.sp,
                        color = if (availableBalance >= amount) TextPrimary else RedDanger
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TransparencyNoticeBanner(
                    text = "Cycle duration is 3 days. Funds will be locked and cannot be withdrawn until completion."
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { agreedDisclaimer = !agreedDisclaimer }
                ) {
                    Checkbox(
                        checked = agreedDisclaimer,
                        onCheckedChange = { agreedDisclaimer = it }
                    )
                    Text(
                        text = "I understand rewards are system-defined incentives, not guaranteed profit.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onConfirmDeposit(payFromAvailable) },
                    enabled = agreedDisclaimer && (!payFromAvailable || availableBalance >= amount),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_deposit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Confirm & Lock Deposit 🔒", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransparencyNoticeBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GoldLight),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GoldAccent),
                contentAlignment = Alignment.Center
            ) {
                Text("B", fontWeight = FontWeight.Bold, color = NavyDark, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontSize = 12.sp,
                    color = NavyDark,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun TransactionItemRow(tx: TransactionEntity) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
    val isPositive = tx.amount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BentoBg, shape = RoundedCornerShape(16.dp))
            .border(1.dp, BentoBorder, shape = RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when (tx.type) {
                            TransactionType.DEPOSIT -> BentoHeroCardBg
                            TransactionType.CYCLE_REWARD -> BentoEarnedBadgeBg
                            TransactionType.WITHDRAWAL -> BentoLockedBadgeBg
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
                        TransactionType.DEPOSIT -> BentoPrimaryBlue
                        TransactionType.CYCLE_REWARD -> BentoEarnedBadgeText
                        TransactionType.WITHDRAWAL -> BentoLockedBadgeText
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
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
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
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when (tx.status) {
                    TransactionStatus.COMPLETED, TransactionStatus.APPROVED -> BentoEarnedBadgeBg
                    TransactionStatus.LOCKED -> BentoHeroCardBg
                    TransactionStatus.PENDING -> GoldLight
                    TransactionStatus.REJECTED -> BentoLockedBadgeBg
                }
            ) {
                Text(
                    text = tx.status.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (tx.status) {
                        TransactionStatus.COMPLETED, TransactionStatus.APPROVED -> BentoEarnedBadgeText
                        TransactionStatus.LOCKED -> BentoHeroText
                        TransactionStatus.PENDING -> OrangeWarning
                        TransactionStatus.REJECTED -> BentoLockedBadgeText
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun BentoEarningsChartCard(
    earnedAmount: Double,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("Weekly") }

    val weeklyData = remember { listOf(1400f, 2800f, 2100f, 4200f, 3600f, 5400f, 6800f) }
    val weeklyLabels = remember { listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") }

    val monthlyData = remember { listOf(12500f, 24000f, 38000f, 51000f, 69000f, 88500f) }
    val monthlyLabels = remember { listOf("Mar", "Apr", "May", "Jun", "Jul", "Aug") }

    val dataPoints = if (selectedTimeframe == "Weekly") weeklyData else monthlyData
    val labels = if (selectedTimeframe == "Weekly") weeklyLabels else monthlyLabels
    val totalPeriodEarning = if (selectedTimeframe == "Weekly") 26300.0 else earnedAmount.coerceAtLeast(283000.0)
    val growthRate = if (selectedTimeframe == "Weekly") "+18.4%" else "+32.1%"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.9f),
                    BentoBorder,
                    BentoPrimaryBlue.copy(alpha = 0.25f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Header Row: Title & Toggle Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FINANCIAL GROWTH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Visual Earnings Chart",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoBadgeGreyBg.copy(alpha = 0.6f)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedTimeframe == "Weekly") BentoPrimaryBlue else Color.Transparent)
                                .clickable { selectedTimeframe = "Weekly" }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("timeframe_weekly_btn")
                        ) {
                            Text(
                                text = "Weekly",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTimeframe == "Weekly") Color.White else TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedTimeframe == "Monthly") BentoPrimaryBlue else Color.Transparent)
                                .clickable { selectedTimeframe = "Monthly" }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("timeframe_monthly_btn")
                        ) {
                            Text(
                                text = "Monthly",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTimeframe == "Monthly") Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Growth stat badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%,d RWF".format(totalPeriodEarning.toInt()),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedTimeframe == "Weekly") "this week" else "this month",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoEarnedBadgeBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = BentoEarnedBadgeText,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = growthRate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoEarnedBadgeText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Canvas Line Chart
            val primaryColor = BentoPrimaryBlue
            val gradientStartColor = BentoPrimaryBlue.copy(alpha = 0.35f)
            val gradientEndColor = BentoPrimaryBlue.copy(alpha = 0.02f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val width = size.width
                val height = size.height
                val maxVal = (dataPoints.maxOrNull() ?: 1f) * 1.15f
                val minVal = 0f

                val stepX = width / (dataPoints.size - 1)

                val points = dataPoints.mapIndexed { index, value ->
                    val x = index * stepX
                    val y = height - ((value - minVal) / (maxVal - minVal) * height)
                    Offset(x, y)
                }

                val strokePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val controlPoint1 = Offset(
                                p1.x + (p2.x - p1.x) / 2f,
                                p1.y
                            )
                            val controlPoint2 = Offset(
                                p1.x + (p2.x - p1.x) / 2f,
                                p2.y
                            )
                            cubicTo(
                                controlPoint1.x, controlPoint1.y,
                                controlPoint2.x, controlPoint2.y,
                                p2.x, p2.y
                            )
                        }
                    }
                }

                val fillPath = Path().apply {
                    addPath(strokePath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                // Draw Gradient Fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientStartColor, gradientEndColor),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw Line Stroke
                drawPath(
                    path = strokePath,
                    color = primaryColor,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Nodes
                points.forEachIndexed { index, point ->
                    val isLast = index == points.size - 1
                    if (isLast) {
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.25f),
                            radius = 9.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = 5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = point
                        )
                    } else {
                        drawCircle(
                            color = Color.White,
                            radius = 3.5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = 2.dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


