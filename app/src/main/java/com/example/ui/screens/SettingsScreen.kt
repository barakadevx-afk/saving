package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.AppCurrency
import com.example.data.model.Language
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: UserEntity?,
    strings: AppStrings,
    currentLanguage: Language,
    selectedCurrency: AppCurrency,
    isDarkMode: Boolean,
    onLanguageChange: (Language) -> Unit,
    onCurrencyChange: (AppCurrency) -> Unit,
    onToggleDarkMode: () -> Unit,
    onToggleRole: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showLanguageDropdown by remember { mutableStateOf(false) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Unspecified)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(NavyDark, BentoPrimaryBlue)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Text(
                                text = strings.profileSettings,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "App preferences, language, currency & security",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Language Selector Card (DataStore Persisted)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BentoPrimaryBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = BentoPrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.selectLanguage,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Switches system language & persists choice in DataStore",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Language Selection Dropdown Field
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { showLanguageDropdown = !showLanguageDropdown },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = BentoPrimaryBlue.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .testTag("language_dropdown_button"),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val currentLangFlag = when (currentLanguage) {
                                        Language.EN -> "🇬🇧"
                                        Language.RW -> "🇷🇼"
                                        Language.FR -> "🇫🇷"
                                    }
                                    val currentLangName = when (currentLanguage) {
                                        Language.EN -> "English"
                                        Language.RW -> "Kinyarwanda"
                                        Language.FR -> "Français"
                                    }

                                    Text(text = currentLangFlag, fontSize = 20.sp)
                                    Text(
                                        text = currentLangName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }

                                Icon(
                                    imageVector = if (showLanguageDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Toggle language dropdown",
                                    tint = TextSecondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showLanguageDropdown,
                            onDismissRequest = { showLanguageDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("🇬🇧", fontSize = 18.sp)
                                        Text("English", fontWeight = if (currentLanguage == Language.EN) FontWeight.Bold else FontWeight.Normal)
                                    }
                                },
                                onClick = {
                                    onLanguageChange(Language.EN)
                                    showLanguageDropdown = false
                                },
                                modifier = Modifier.testTag("lang_option_en"),
                                leadingIcon = if (currentLanguage == Language.EN) {
                                    { Icon(Icons.Default.Check, contentDescription = null, tint = BentoPrimaryBlue) }
                                } else null
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("🇷🇼", fontSize = 18.sp)
                                        Text("Kinyarwanda", fontWeight = if (currentLanguage == Language.RW) FontWeight.Bold else FontWeight.Normal)
                                    }
                                },
                                onClick = {
                                    onLanguageChange(Language.RW)
                                    showLanguageDropdown = false
                                },
                                modifier = Modifier.testTag("lang_option_rw"),
                                leadingIcon = if (currentLanguage == Language.RW) {
                                    { Icon(Icons.Default.Check, contentDescription = null, tint = BentoPrimaryBlue) }
                                } else null
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("🇫🇷", fontSize = 18.sp)
                                        Text("Français", fontWeight = if (currentLanguage == Language.FR) FontWeight.Bold else FontWeight.Normal)
                                    }
                                },
                                onClick = {
                                    onLanguageChange(Language.FR)
                                    showLanguageDropdown = false
                                },
                                modifier = Modifier.testTag("lang_option_fr"),
                                leadingIcon = if (currentLanguage == Language.FR) {
                                    { Icon(Icons.Default.Check, contentDescription = null, tint = BentoPrimaryBlue) }
                                } else null
                            )
                        }
                    }
                }
            }
        }

        // Currency Selector Card (DataStore Persisted)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("currency_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GreenSuccess.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = GreenSuccess,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Display Currency",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Converts all balances & yields (1 USD = 1,380 RWF)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Currency Dropdown Button
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { showCurrencyDropdown = !showCurrencyDropdown },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = GreenSuccess.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .testTag("currency_dropdown_button"),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val currLabel = if (selectedCurrency == AppCurrency.RWF) "Rwandan Franc" else "US Dollar"
                                    Text(text = selectedCurrency.flag, fontSize = 20.sp)
                                    Text(
                                        text = "$currLabel (${selectedCurrency.code})",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }

                                Icon(
                                    imageVector = if (showCurrencyDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Toggle currency dropdown",
                                    tint = TextSecondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showCurrencyDropdown,
                            onDismissRequest = { showCurrencyDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            AppCurrency.entries.forEach { currency ->
                                val currLabel = if (currency == AppCurrency.RWF) "Rwandan Franc" else "US Dollar"
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(currency.flag, fontSize = 18.sp)
                                            Text(
                                                "$currLabel (${currency.code})",
                                                fontWeight = if (selectedCurrency == currency) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    },
                                    onClick = {
                                        onCurrencyChange(currency)
                                        showCurrencyDropdown = false
                                    },
                                    modifier = Modifier.testTag("curr_option_${currency.code.lowercase()}"),
                                    leadingIcon = if (selectedCurrency == currency) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = GreenSuccess) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }

        // Appearance / Dark Theme Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Dark Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isDarkMode) "Dark theme enabled 🌙" else "Light theme enabled ☀️",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldAccent,
                            checkedTrackColor = BentoPrimaryBlue.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("dark_mode_settings_switch")
                    )
                }
            }
        }

        // Account Profile & Security Details Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_profile_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Account & Security",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    HorizontalDivider(color = TextSecondary.copy(alpha = 0.15f))

                    // User Details Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = user?.fullName ?: "Logged-in User",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = user?.phone ?: "+250 788 000 000",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (user?.role == UserRole.ADMIN) GoldLight else BlueLight
                        ) {
                            Text(
                                text = if (user?.role == UserRole.ADMIN) "ADMIN" else "MEMBER",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user?.role == UserRole.ADMIN) GoldAccent else BentoPrimaryBlue
                            )
                        }
                    }

                    // Referral Code Section
                    if (user != null && user.referralCode.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Your Referral Code", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = user.referralCode,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(user.referralCode))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", tint = GoldAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Action Buttons: Switch Role & Logout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onToggleRole,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_switch_role_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Role", fontSize = 13.sp)
                        }

                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_logout_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Logout", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
