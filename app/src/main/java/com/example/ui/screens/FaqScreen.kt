package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.ui.theme.*

data class FaqItem(
    val id: String,
    val category: FaqCategory,
    val question: String,
    val answer: String,
    val proTip: String? = null
)

enum class FaqCategory(val displayName: String, val emoji: String) {
    ALL("All Questions", "❓"),
    SAVINGS_CYCLE("3-Day Savings Cycle", "⚡"),
    DEPOSIT_LIMITS("Deposit Limits & Tiers", "💰"),
    VERIFICATION("Verification & KYC", "🛡️"),
    WITHDRAWALS("Withdrawals & MoMo", "📱"),
    REFERRAL("Referrals & Rewards", "🎁")
}

val sampleFaqList = listOf(
    FaqItem(
        id = "faq_1",
        category = FaqCategory.SAVINGS_CYCLE,
        question = "How does the 3-day savings cycle work?",
        answer = "When you deposit funds into any savings tier (e.g. 15,000 RWF), your principal enters an automated 72-hour compounding cycle. During these 3 days, your funds remain safely locked in our reserve liquidity pool. At the exact end of the 72 hours, 100% of your principal PLUS your guaranteed 50% profit reward (e.g. 22,500 RWF total) is automatically unlocked and credited to your wallet.",
        proTip = "You will receive an instant push notification the second your 3-day lock ends and your profit is released!"
    ),
    FaqItem(
        id = "faq_2",
        category = FaqCategory.SAVINGS_CYCLE,
        question = "When and how do I receive my 50% profit yield?",
        answer = "Your 50% yield is calculated automatically the moment your deposit is confirmed. Once the 72-hour timer completes, the yield and principal are combined and credited directly to your Available Wallet Balance. You can then withdraw to Mobile Money immediately or re-invest into a new cycle.",
        proTip = "In demo mode, you can use the 'Fast Forward 3 Days' button on the dashboard to test instant cycle completion!"
    ),
    FaqItem(
        id = "faq_3",
        category = FaqCategory.SAVINGS_CYCLE,
        question = "Can I cancel or withdraw my deposit before 3 days?",
        answer = "No. Funds are strictly locked for the 72-hour duration to preserve liquidity pool backing and guarantee the 50% return yield across all user cycles. Early withdrawals are not permitted, but full principal + reward is guaranteed at settlement.",
        proTip = "Track the live countdown timer on your Savings Cycles screen at any time."
    ),
    FaqItem(
        id = "faq_4",
        category = FaqCategory.DEPOSIT_LIMITS,
        question = "What are the minimum and maximum deposit limits?",
        answer = "We offer structured deposit tiers tailored for all savings goals:\n• Tier A: 5,000 RWF (Earn 2,500 RWF reward)\n• Tier B: 10,000 RWF (Earn 5,000 RWF reward)\n• Tier C: 15,000 RWF (Earn 7,500 RWF reward)\n• Tier D: 25,000 RWF (Earn 12,500 RWF reward)\n• Tier E: 35,000 RWF (Earn 17,500 RWF reward)\n• Tier F: 50,000 RWF (Earn 25,000 RWF reward)\n\nThe maximum single cycle limit is capped at 50,000 RWF to ensure financial security and risk management.",
        proTip = "You can participate in multiple savings cycles simultaneously across different tiers!"
    ),
    FaqItem(
        id = "faq_5",
        category = FaqCategory.DEPOSIT_LIMITS,
        question = "Which payment methods are supported for deposits?",
        answer = "Deposits can be made using MTN Mobile Money (*182#), Airtel Money (*182#), or via direct Wallet Balance transfer if you have earnings available. Official USSD MoMo Pay merchant codes are provided during deposit checkout.",
        proTip = "Make sure to enter your MoMo Transaction Reference ID for automatic instant payment verification."
    ),
    FaqItem(
        id = "faq_6",
        category = FaqCategory.VERIFICATION,
        question = "Why is account & identity verification required?",
        answer = "Account verification protects your funds from unauthorized access and ensures that Mobile Money withdrawals are dispatched strictly to your verified phone number. It prevents multi-account fraud and satisfies financial safety regulations in Rwanda.",
        proTip = "Always register with the exact Mobile Money phone number you use to receive cashouts."
    ),
    FaqItem(
        id = "faq_7",
        category = FaqCategory.VERIFICATION,
        question = "Is my capital and money safe with Future Smart Capital?",
        answer = "Yes! All user deposits are backed by verified reserve funds and managed via automated ledger contracts. Payouts are protected with 256-bit encryption and audited double-entry accounting.",
        proTip = "You can review every deposit and payout in your complete transparent Activity Ledger."
    ),
    FaqItem(
        id = "faq_8",
        category = FaqCategory.WITHDRAWALS,
        question = "How long do Mobile Money withdrawals take?",
        answer = "Once requested, Mobile Money payout requests are processed automatically and typically reach your MTN or Airtel account within 5 to 15 minutes. Admin approvals for large withdrawals are completed within 1 business hour.",
        proTip = "Withdrawals are available 24/7 with zero hidden processing fees."
    ),
    FaqItem(
        id = "faq_9",
        category = FaqCategory.REFERRAL,
        question = "How does the Invite & Referral bonus work?",
        answer = "Every registered user receives a unique Referral Link and Code. When a friend signs up using your link or code and completes their first deposit, you receive an instant 1,000 RWF cash bonus added straight to your wallet balance!",
        proTip = "Share your referral link directly on WhatsApp, Facebook, or Telegram to maximize earnings."
    )
)

@Composable
fun FaqScreen(
    strings: AppStrings,
    onNavigateToDeposit: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FaqCategory.ALL) }
    var expandedFaqId by remember { mutableStateOf<String?>("faq_1") } // First item expanded by default

    val context = LocalContext.current

    val filteredFaqs = remember(searchQuery, selectedCategory) {
        sampleFaqList.filter { item ->
            val matchesCategory = (selectedCategory == FaqCategory.ALL || item.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    item.question.contains(searchQuery, ignoreCase = true) ||
                    item.answer.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GoldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = OrangeWarning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Help & FAQ Center",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Learn how the 3-day savings cycle works, deposit limits, verification, and instant payouts.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Trust Summary Bento Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrustBadgeItem("⚡ 3-Day Lock", "50% Profit Yield")
                    Divider(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp),
                        color = BentoBorder
                    )
                    TrustBadgeItem("🛡️ 100% Safe", "Backed Reserves")
                    Divider(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp),
                        color = BentoBorder
                    )
                    TrustBadgeItem("📱 Instant MoMo", "24/7 Cashout")
                }
            }
        }

        // Search Bar Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search questions (e.g., 'withdraw', 'limit', 'profit')...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("faq_search_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FaqCategory.values().forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = "${category.emoji} ${category.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // FAQ Items List
        if (filteredFaqs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FindInPage, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No questions found matching your search.", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredFaqs, key = { it.id }) { faq ->
                val isExpanded = expandedFaqId == faq.id

                ExpandableFaqCard(
                    faq = faq,
                    isExpanded = isExpanded,
                    onToggle = {
                        expandedFaqId = if (isExpanded) null else faq.id
                    }
                )
            }
        }

        // Customer Support & Contact Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoHeroCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BentoPrimaryBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Still have questions?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoHeroText
                            )
                            Text(
                                text = "Our Rwanda support team is available 24/7 on WhatsApp.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val whatsappUrl = "https://wa.me/250788123456?text=Hello%20Future%20Smart%20Capital%20Support,%20I%20have%20a%20question%20about%20my%20savings%20account."
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f).testTag("whatsapp_support_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Support Phone", "+250788123456")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Support phone copied: +250 788 123 456 📞", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("call_support_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Phone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrustBadgeItem(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = subtitle, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ExpandableFaqCard(
    faq: FaqItem,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("faq_card_${faq.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = BorderStroke(1.dp, if (isExpanded) BentoPrimaryBlue else BentoBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldLight
                    ) {
                        Text(
                            text = "${faq.category.emoji} ${faq.category.displayName.uppercase()}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OrangeWarning,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = faq.question,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (isExpanded) BentoHeroCardBg else LightBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = if (isExpanded) BentoPrimaryBlue else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = BentoBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = faq.answer,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Normal
                    )

                    faq.proTip?.let { tip ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenLight,
                            border = BorderStroke(1.dp, GreenSuccess.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tip,
                                    fontSize = 11.sp,
                                    color = BentoEarnedBadgeText,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
