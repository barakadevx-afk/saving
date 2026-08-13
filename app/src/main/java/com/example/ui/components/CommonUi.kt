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
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.data.i18n.AppStrings
import com.example.data.model.AppCurrency
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
fun SfcLogo(
    modifier: Modifier = Modifier,
    sizeDp: Int = 36,
    showFullText: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.5.dp, GoldAccent),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S",
                    fontWeight = FontWeight.Black,
                    fontSize = (sizeDp * 0.55).sp,
                    color = Color(0xFF2563EB)
                )
                Text(
                    text = "F",
                    fontWeight = FontWeight.Black,
                    fontSize = (sizeDp * 0.55).sp,
                    color = GoldAccent
                )
                Text(
                    text = "C",
                    fontWeight = FontWeight.Black,
                    fontSize = (sizeDp * 0.55).sp,
                    color = Color(0xFF2563EB)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                    modifier = Modifier.height((sizeDp * 0.45).dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight(0.4f)
                            .background(Color(0xFF2563EB), RoundedCornerShape(1.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight(0.7f)
                            .background(GoldAccent, RoundedCornerShape(1.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight(1.0f)
                            .background(GreenSuccess, RoundedCornerShape(1.dp))
                    )
                }
            }
        }

        if (showFullText) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row {
                    Text(text = "SMART ", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                    Text(text = "FUTURE ", fontWeight = FontWeight.Black, fontSize = 12.sp, color = GoldDark)
                    Text(text = "CAPITAL", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(3.dp).background(Color(0xFF2563EB), CircleShape))
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(modifier = Modifier.size(3.dp).background(GoldAccent, CircleShape))
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(modifier = Modifier.size(3.dp).background(GreenSuccess, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SFC VAULT • 50% PROFIT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TopHeaderBar(
    strings: AppStrings,
    currentLanguage: Language,
    currentCurrency: AppCurrency = AppCurrency.RWF,
    isDarkMode: Boolean = false,
    userRole: UserRole,
    userName: String,
    unreadAnnouncementsCount: Int = 0,
    onLanguageChange: (Language) -> Unit,
    onCurrencyChange: (AppCurrency) -> Unit = {},
    onToggleDarkMode: () -> Unit = {},
    onToggleRole: () -> Unit,
    onOpenNav: () -> Unit,
    onOpenAnnouncements: () -> Unit = {},
    onRefresh: (() -> Unit)? = null,
    isLoading: Boolean = false
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BentoBg,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenNav,
                    modifier = Modifier
                        .size(38.dp)
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

                Spacer(modifier = Modifier.width(8.dp))

                SfcLogo(sizeDp = 28, showFullText = true)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Announcements Notification Icon
                IconButton(
                    onClick = onOpenAnnouncements,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BentoBadgeGreyBg)
                        .testTag("announcements_bell_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadAnnouncementsCount > 0) {
                                Badge(
                                    containerColor = RedError,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadAnnouncementsCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Announcements & News",
                            tint = if (unreadAnnouncementsCount > 0) GoldAccent else BentoHeroText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))
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

                Spacer(modifier = Modifier.width(6.dp))

                // Currency Selector Dropdown
                Box {
                    Button(
                        onClick = { currencyMenuExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoBadgeGreyBg,
                            contentColor = BentoHeroText
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        modifier = Modifier.testTag("currency_selector_btn")
                    ) {
                        Text(text = "${currentCurrency.flag} ${currentCurrency.code}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Currency",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = currencyMenuExpanded,
                        onDismissRequest = { currencyMenuExpanded = false },
                        modifier = Modifier.background(BentoCardBg)
                    ) {
                        DropdownMenuItem(
                            text = { Text("🇷🇼 RWF (Rwandan Franc)", color = TextPrimary, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onCurrencyChange(AppCurrency.RWF)
                                currencyMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇺🇸 USD ($ USD Dollar)", color = TextPrimary, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onCurrencyChange(AppCurrency.USD)
                                currencyMenuExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Dark Mode Toggle Button
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BentoBadgeGreyBg)
                        .testTag("dark_mode_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = if (isDarkMode) GoldAccent else BentoHeroText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (onRefresh != null) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BentoBadgeGreyBg)
                            .testTag("refresh_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = BentoHeroText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

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
    selectedCurrency: AppCurrency = AppCurrency.RWF,
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
                text = selectedCurrency.format(availableBalance),
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
fun ThreeDayLockTimerComponent(
    startDate: Long,
    endDate: Long,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(endDate) {
        while (currentTime < endDate) {
            currentTime = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000L)
        }
    }

    val totalDuration = (endDate - startDate).coerceAtLeast(1L)
    val remaining = (endDate - currentTime).coerceAtLeast(0L)
    val elapsed = (currentTime - startDate).coerceAtLeast(0L)
    val progress = (elapsed.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)

    val days = remaining / (24 * 3600 * 1000)
    val hours = (remaining % (24 * 3600 * 1000)) / (3600 * 1000)
    val minutes = (remaining % (3600 * 1000)) / (60 * 1000)
    val seconds = (remaining % (60 * 1000)) / 1000

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(GoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Lock Timer",
                            tint = NavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3-DAY LOCK COUNTDOWN",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreenSuccess.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, GreenSuccess.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (remaining > 0) "🔒 50% Yield Locked" else "✅ Yield Unlocked!",
                        color = GreenSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Time Boxes Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeDigitBox(value = "%02d".format(days), label = "DAYS")
                Text(":", color = GoldAccent, fontWeight = FontWeight.Black, fontSize = 20.sp)
                TimeDigitBox(value = "%02d".format(hours), label = "HOURS")
                Text(":", color = GoldAccent, fontWeight = FontWeight.Black, fontSize = 20.sp)
                TimeDigitBox(value = "%02d".format(minutes), label = "MINS")
                Text(":", color = GoldAccent, fontWeight = FontWeight.Black, fontSize = 20.sp)
                TimeDigitBox(value = "%02d".format(seconds), label = "SECS")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Animated Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "72-Hour Reward Progress",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% Completed",
                        fontSize = 11.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = GoldAccent,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Milestone Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Day 1: Locked 🔒", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    Text("Day 2: Compounding ⚡", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    Text("Day 3: 50% Profit 🚀", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TimeDigitBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .height(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ActiveCycleProgressCard(
    cycle: SavingsCycleEntity,
    strings: AppStrings,
    selectedCurrency: AppCurrency = AppCurrency.RWF,
    onFastForward: () -> Unit
) {
    val depositFormatted = selectedCurrency.format(cycle.depositAmount)
    val rewardFormatted = selectedCurrency.format(cycle.expectedReward)
    val totalPayoutFormatted = selectedCurrency.format(cycle.depositAmount + cycle.expectedReward)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_cycle_card"),
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
                        text = "ACTIVE SAVINGS VAULT",
                        color = BentoDarkCardText.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$depositFormatted Principal Deposit",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
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

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Day Lock Timer Component
            ThreeDayLockTimerComponent(
                startDate = cycle.startDate,
                endDate = cycle.endDate
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Expected Payout: $totalPayoutFormatted",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Principal ($depositFormatted) + 50% Profit (+$rewardFormatted)",
                        color = GreenSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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

fun launchPhoneCallIntent(context: Context, phoneNumber: String = "0792828727") {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun launchWhatsAppIntent(
    context: Context,
    phoneNumber: String = "250792828727",
    message: String = "Hello Admin, I have completed payment to code 1799283 for FUTURE SMART CAPITAL."
) {
    try {
        val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {}
}

@Composable
fun DepositModalDialog(
    selectedTier: String,
    strings: AppStrings,
    availableBalance: Double,
    onDismiss: () -> Unit,
    onConfirmDeposit: (payFromAvailable: Boolean) -> Unit,
    onSubmitDepositRequest: (amount: Double, transactionId: String, proofScreenshotUri: String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    var payFromAvailable by remember { mutableStateOf(false) }
    var agreedDisclaimer by remember { mutableStateOf(false) }
    var transactionIdInput by remember { mutableStateOf("") }
    var proofScreenshotUri by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            proofScreenshotUri = uri.toString()
        }
    }

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

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Merchant Code Instructions Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldLight,
                    border = BorderStroke(1.dp, GoldAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💳 Mobile Money Payment Code: 1799283",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyDark
                        )
                        Text(
                            text = "(Alternative code: 1799273)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📌 Min Deposit: 6,000 RWF | Max Deposit: 1,000,000 RWF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "After sending money, input your Transaction ID and attach the payment screenshot below for Admin verification.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { launchPhoneCallIntent(context, "0792828727") },
                                modifier = Modifier.weight(1f).testTag("call_admin_btn"),
                                border = BorderStroke(1.dp, BluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = BluePrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Admin", fontSize = 11.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    launchWhatsAppIntent(
                                        context = context,
                                        phoneNumber = "250792828727",
                                        message = "Hello Admin, I paid %,d RWF to code 1799283. Tx ID: %s. Please verify and credit my account.".format(amount.toInt(), transactionIdInput.ifBlank { "N/A" })
                                    )
                                },
                                modifier = Modifier.weight(1f).testTag("whatsapp_admin_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Payment Method", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !payFromAvailable,
                        onClick = { payFromAvailable = false }
                    )
                    Text(text = "MoMo Code (1799283 / 1799273)", fontSize = 13.sp, color = TextPrimary)
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

                if (!payFromAvailable) {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = transactionIdInput,
                        onValueChange = { transactionIdInput = it },
                        label = { Text("Transaction ID / MoMo Reference No.*") },
                        placeholder = { Text("e.g. 1829302910") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("deposit_tx_id_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Upload Payment Screenshot Proof*", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (proofScreenshotUri.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, BluePrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = proofScreenshotUri,
                                    contentDescription = "Payment Screenshot",
                                    modifier = Modifier
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Screenshot Attached", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    TextButton(
                                        onClick = { proofScreenshotUri = "" },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Remove", fontSize = 11.sp, color = RedDanger)
                                    }
                                }
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f).testTag("upload_screenshot_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Image", fontSize = 11.sp)
                            }

                            TextButton(
                                onClick = { proofScreenshotUri = "https://picsum.photos/seed/momo_receipt_${System.currentTimeMillis()}/400/600" },
                                modifier = Modifier.testTag("sample_screenshot_btn")
                            ) {
                                Text("Use Sample Receipt 🖼️", fontSize = 11.sp, color = BluePrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TransparencyNoticeBanner(
                    text = "Cycle duration is 3 days. Funds will be locked and cannot be withdrawn until completion."
                )

                Spacer(modifier = Modifier.height(10.dp))

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

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (payFromAvailable) {
                            onConfirmDeposit(true)
                        } else {
                            onSubmitDepositRequest(amount, transactionIdInput, proofScreenshotUri)
                        }
                    },
                    enabled = agreedDisclaimer && (payFromAvailable && availableBalance >= amount || !payFromAvailable && transactionIdInput.isNotBlank()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_deposit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (payFromAvailable) "Confirm & Start 3-Day Cycle 🔒" else "Submit Deposit for Verification 📤",
                        fontWeight = FontWeight.Bold
                    )
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
    val dateStr = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
    var isExpanded by remember { mutableStateOf(false) }

    // Color coding logic for amount label & badge
    val amountColor = when {
        tx.type == TransactionType.CYCLE_REWARD || tx.type == TransactionType.REFERRAL_BONUS -> GreenSuccess
        tx.type == TransactionType.WITHDRAWAL -> Color(0xFFEF4444) // Bright Red
        tx.type == TransactionType.DEPOSIT -> BentoPrimaryBlue
        else -> TextPrimary
    }

    val amountPrefix = when {
        tx.type == TransactionType.WITHDRAWAL -> "-"
        tx.amount > 0 -> "+"
        else -> ""
    }

    // Status icon and background color determination
    val (statusIcon, iconBg, iconTint) = when (tx.status) {
        TransactionStatus.PENDING -> Triple(Icons.Default.HourglassTop, GoldLight, OrangeWarning)
        TransactionStatus.LOCKED -> Triple(Icons.Default.Lock, BentoHeroCardBg, BentoHeroText)
        TransactionStatus.REJECTED -> Triple(Icons.Default.Cancel, BentoLockedBadgeBg, BentoLockedBadgeText)
        TransactionStatus.COMPLETED, TransactionStatus.APPROVED -> when (tx.type) {
            TransactionType.CYCLE_REWARD -> Triple(Icons.Default.AutoAwesome, BentoEarnedBadgeBg, BentoEarnedBadgeText)
            TransactionType.REFERRAL_BONUS -> Triple(Icons.Default.CardGiftcard, GoldLight, OrangeWarning)
            TransactionType.WITHDRAWAL -> Triple(Icons.Default.ArrowUpward, BentoLockedBadgeBg, BentoLockedBadgeText)
            TransactionType.DEPOSIT -> Triple(Icons.Default.ArrowDownward, BentoHeroCardBg, BentoPrimaryBlue)
            else -> Triple(Icons.Default.CheckCircle, BentoEarnedBadgeBg, BentoEarnedBadgeText)
        }
    }

    // Status Pill Text and Style
    val (statusText, statusPillBg, statusPillText) = when (tx.status) {
        TransactionStatus.LOCKED -> Triple("🔒 3-DAY LOCK", BentoHeroCardBg, BentoHeroText)
        TransactionStatus.PENDING -> Triple("⏳ PENDING", GoldLight, OrangeWarning)
        TransactionStatus.COMPLETED -> Triple("✅ COMPLETED", BentoEarnedBadgeBg, BentoEarnedBadgeText)
        TransactionStatus.APPROVED -> Triple("✅ APPROVED", BentoEarnedBadgeBg, BentoEarnedBadgeText)
        TransactionStatus.REJECTED -> Triple("❌ REJECTED", BentoLockedBadgeBg, BentoLockedBadgeText)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("tx_item_${tx.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = tx.type.name,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.description,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = dateStr,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$amountPrefix%,d RWF".format(tx.amount.toInt()),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = amountColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = statusPillBg
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusPillText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BentoBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Transaction Ref:", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(tx.id.take(16).uppercase(), fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Category:", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(tx.type.name.replace("_", " "), fontSize = 11.sp, color = BentoPrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
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

@Composable
fun AnnouncementsDialog(
    announcements: List<com.example.data.model.AnnouncementEntity>,
    onDismiss: () -> Unit,
    onDeleteAnnouncement: ((String) -> Unit)? = null,
    isAdmin: Boolean = false
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BentoCardBg),
            border = BorderStroke(1.dp, BentoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(GoldAccent.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = GoldDark)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Announcements & News 📢", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text(text = "SMART FUTURE CAPITAL (SFC)", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (announcements.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No announcements at this time. Check back later!", fontSize = 13.sp, color = TextSecondary)
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(announcements.size) { index ->
                            val item = announcements[index]
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (item.isImportant) Color(0xFFFFFBEB) else Color(0xFFF8FAFC)
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (item.isImportant) GoldAccent else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (item.category) {
                                                "IMPORTANT", "URGENT" -> RedError.copy(alpha = 0.15f)
                                                "PROMO" -> GreenLight
                                                else -> BlueLight
                                            }
                                        ) {
                                            Text(
                                                text = item.category,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (item.category) {
                                                    "IMPORTANT", "URGENT" -> RedError
                                                    "PROMO" -> GreenSuccess
                                                    else -> BluePrimary
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(item.timestamp)),
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.content, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)

                                    if (isAdmin && onDeleteAnnouncement != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            TextButton(
                                                onClick = { onDeleteAnnouncement(item.id) },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Remove", fontSize = 11.sp, color = RedError)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


