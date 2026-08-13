package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.example.ui.components.launchPhoneCallIntent
import com.example.ui.components.launchWhatsAppIntent

@Composable
fun AdminPanelScreen(
    adminConfig: AdminConfigEntity?,
    pendingWithdrawals: List<WithdrawalEntity>,
    allUsers: List<UserEntity>,
    adminLogs: List<AdminLogEntity>,
    strings: AppStrings,
    onApproveWithdrawal: (String) -> Unit,
    onRejectWithdrawal: (String) -> Unit,
    onUpdateRates: (rateA: Double, rateB: Double, rateC: Double, rateD: Double) -> Unit,
    onTriggerSettlement: () -> Unit,
    onAddFundsToUser: (userId: String, amount: Double, note: String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    var rateAStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierA ?: 0.02) * 100)) }
    var rateBStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierB ?: 0.02) * 100)) }
    var rateCStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierC ?: 0.02) * 100)) }
    var rateDStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierD ?: 0.02) * 100)) }

    var userQuery by remember { mutableStateOf("") }
    var selectedUserForFunds by remember { mutableStateOf<UserEntity?>(null) }

    val filteredUsers = allUsers.filter {
        it.fullName.contains(userQuery, ignoreCase = true) ||
        it.email.contains(userQuery, ignoreCase = true) ||
        it.phone.contains(userQuery, ignoreCase = true)
    }

    if (selectedUserForFunds != null) {
        val user = selectedUserForFunds!!
        var addAmountText by remember { mutableStateOf("6000") }
        var noteText by remember { mutableStateOf("MoMo Code 1799283 Payment Verified") }

        Dialog(onDismissRequest = { selectedUserForFunds = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
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
                            text = "Add Funds to User 💵",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { selectedUserForFunds = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "User: ${user.fullName} (${user.phone})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoPrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Quick Amount Options:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("6000", "10000", "15000", "45000").forEach { preset ->
                            FilterChip(
                                selected = addAmountText == preset,
                                onClick = { addAmountText = preset },
                                label = { Text("${preset.toInt() / 1000}k RWF", fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("100000", "500000", "1000000").forEach { preset ->
                            FilterChip(
                                selected = addAmountText == preset,
                                onClick = { addAmountText = preset },
                                label = { Text("${preset.toInt() / 1000}k RWF", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = addAmountText,
                        onValueChange = { addAmountText = it.filter { c -> c.isDigit() } },
                        label = { Text("Deposit Amount (6,000 - 1,000,000 RWF)") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("admin_add_amount_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Verification Note / Reference") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("admin_add_note_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val amt = addAmountText.toDoubleOrNull() ?: 0.0
                            if (amt >= 1.0) {
                                onAddFundsToUser(user.id, amt, noteText)
                                selectedUserForFunds = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("admin_confirm_add_funds_btn")
                    ) {
                        Text(text = "Credit Balance Now 💰", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

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
                        text = "⚙️ " + strings.adminPanel,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "FUTURE SMART CAPITAL - Payment Code 1799283 Verification & User Management",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onTriggerSettlement,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("admin_settlement_trigger_btn")
                ) {
                    Text(text = "Run Settlement ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Admin Merchant Code Info Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GoldLight),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💳 Payment Merchant Codes: 1799283 (Alt: 1799273)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyDark
                    )
                    Text(
                        text = "Admin Contact & WhatsApp: 0792828727",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Deposit Limits: Minimum 6,000 RWF (6k) | Maximum 1,000,000 RWF (1M)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { launchPhoneCallIntent(context, "0792828727") },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Call Admin 📞", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { launchWhatsAppIntent(context, "250792828727", "Admin Verification Panel: Checking payments for code 1799283") },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("WhatsApp Admin 💬", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Analytics Overview Row Bento Cards
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Total Registered Users", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${allUsers.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BentoPrimaryBlue, letterSpacing = (-0.5).sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Pending Withdrawals", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${pendingWithdrawals.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OrangeWarning, letterSpacing = (-0.5).sp)
                    }
                }
            }
        }

        // Configure Tier Rates Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Configure Tier Reward Rates (%)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = rateAStr,
                            onValueChange = { rateAStr = it },
                            label = { Text("Tier A (6k)") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).testTag("rate_a_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = rateBStr,
                            onValueChange = { rateBStr = it },
                            label = { Text("Tier B (10k)") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).testTag("rate_b_input"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = rateCStr,
                            onValueChange = { rateCStr = it },
                            label = { Text("Tier C (15k)") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).testTag("rate_c_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = rateDStr,
                            onValueChange = { rateDStr = it },
                            label = { Text("Tier D (45k)") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).testTag("rate_d_input"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val rA = (rateAStr.toDoubleOrNull() ?: 2.0) / 100.0
                            val rB = (rateBStr.toDoubleOrNull() ?: 2.0) / 100.0
                            val rC = (rateCStr.toDoubleOrNull() ?: 2.0) / 100.0
                            val rD = (rateDStr.toDoubleOrNull() ?: 2.0) / 100.0
                            onUpdateRates(rA, rB, rC, rD)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_rates_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = "Save Reward Rates Configuration", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Pending Withdrawal Requests Section
        item {
            Text(
                text = strings.pendingWithdrawals + " (${pendingWithdrawals.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.3).sp
            )
        }

        if (pendingWithdrawals.isEmpty()) {
            item {
                Text(text = "No pending withdrawal requests.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            items(pendingWithdrawals) { wth ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "User ID: ${wth.userId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "${wth.payoutMethod} - ${wth.accountNumber}", fontSize = 12.sp, color = TextSecondary)
                            }
                            Text(
                                text = "%,d RWF".format(wth.amount.toInt()),
                                fontWeight = FontWeight.Bold,
                                color = RedDanger,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onApproveWithdrawal(wth.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("approve_wth_${wth.id}")
                            ) {
                                Text(text = strings.approve, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onRejectWithdrawal(wth.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("reject_wth_${wth.id}")
                            ) {
                                Text(text = strings.reject, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Users Management List with Search and Add Funds
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "User Management & Funds Credit", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = (-0.3).sp)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlueLight
                ) {
                    Text("${filteredUsers.size} users", fontSize = 11.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = userQuery,
                onValueChange = { userQuery = it },
                label = { Text("Search users by name, email, or phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (userQuery.isNotEmpty()) {
                        IconButton(onClick = { userQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("user_search_input"),
                singleLine = true
            )
        }

        items(filteredUsers) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text(text = "${user.email} | ${user.phone}", fontSize = 11.sp, color = TextSecondary)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (user.role == UserRole.ADMIN) GoldLight else BentoHeroCardBg
                        ) {
                            Text(
                                text = user.role.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user.role == UserRole.ADMIN) OrangeWarning else BentoHeroText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ID: ${user.id.take(12)}...", fontSize = 11.sp, color = TextSecondary)

                        Button(
                            onClick = { selectedUserForFunds = user },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_funds_user_${user.id}")
                        ) {
                            Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Funds 💵", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Admin Audit Logs Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "System Audit Logs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = (-0.3).sp)
        }

        items(adminLogs.take(10)) { log ->
            val dateStr = SimpleDateFormat("MMM dd HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoPrimaryBlue)
                        Text(text = dateStr, fontSize = 10.sp, color = TextSecondary)
                    }
                    Text(text = log.details, fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}

