package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.AppCurrency
import com.example.data.model.TransactionStatus
import com.example.data.model.WalletEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.components.TransparencyNoticeBanner
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun WalletWithdrawScreen(
    wallet: WalletEntity?,
    withdrawals: List<WithdrawalEntity>,
    strings: AppStrings,
    selectedCurrency: AppCurrency = AppCurrency.RWF,
    onRequestWithdrawal: (amount: Double, method: String, accountNum: String) -> Unit
) {
    val available = wallet?.availableBalance ?: 0.0
    val locked = wallet?.lockedBalance ?: 0.0

    var withdrawAmountText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("MTN Mobile Money") }
    var accountNumberText by remember { mutableStateOf("+250 788 123 456") }
    var methodExpanded by remember { mutableStateOf(false) }

    val amountVal = withdrawAmountText.toDoubleOrNull() ?: 0.0
    val isValid = amountVal > 0 && amountVal <= available && accountNumberText.isNotBlank()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = strings.myWallet + " & " + strings.withdraw,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Manage your available funds and submit withdrawal payout requests.",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        // Wallet Balance Summary Cards Bento Style
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoHeroCardBg)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = strings.availableBalance, fontSize = 12.sp, color = BentoHeroText.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedCurrency.format(available),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoHeroText,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = strings.availableToWithdraw, fontSize = 11.sp, color = BentoHeroText.copy(alpha = 0.7f))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = strings.lockedBalance, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedCurrency.format(locked),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = strings.inActiveCycles, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        item {
            TransparencyNoticeBanner(
                text = "Only Available Balance can be withdrawn. Locked funds remain safely time-locked until cycle completion."
            )
        }

        // Withdrawal Form Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Request Payout / Withdrawal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it.filter { char -> char.isDigit() } },
                        label = { Text("Withdrawal Amount (RWF)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        isError = amountVal > available,
                        supportingText = {
                            if (amountVal > available) {
                                Text("Amount exceeds available balance", color = RedDanger)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box {
                        OutlinedTextField(
                            value = selectedMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Payout Method") },
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = {
                                IconButton(onClick = { methodExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Method")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("payout_method_select")
                        )

                        DropdownMenu(
                            expanded = methodExpanded,
                            onDismissRequest = { methodExpanded = false },
                            modifier = Modifier.background(BentoCardBg)
                        ) {
                            DropdownMenuItem(
                                text = { Text("MTN Mobile Money", color = TextPrimary) },
                                onClick = {
                                    selectedMethod = "MTN Mobile Money"
                                    methodExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Airtel Money", color = TextPrimary) },
                                onClick = {
                                    selectedMethod = "Airtel Money"
                                    methodExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Bank Account (BK / I&M)", color = TextPrimary) },
                                onClick = {
                                    selectedMethod = "Bank Account"
                                    methodExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = accountNumberText,
                        onValueChange = { accountNumberText = it },
                        label = { Text("Account / Phone Number") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payout_account_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            onRequestWithdrawal(amountVal, selectedMethod, accountNumberText)
                            withdrawAmountText = ""
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_withdrawal_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = "Submit Withdrawal Request ⬆️", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Withdrawal Status Verification Legend
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "WITHDRAWAL STATUS VERIFICATION GUIDE",
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
                        // Processing
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Processing (1-10m)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        // Completed
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(GreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Completed & Paid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        // Flagged
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Flagged / Review", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Withdrawal Requests History
        item {
            Text(
                text = "Withdrawal Requests History (${withdrawals.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.3).sp
            )
        }

        if (withdrawals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No withdrawal requests yet.", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Payouts you request will appear here with live verification tracking.", color = TextSecondary.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(withdrawals) { wth ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(wth.requestedAt))

                val (statusLabel, statusBg, statusText, statusBorder, statusIcon, subStatusText) = when (wth.status) {
                    TransactionStatus.PENDING -> WithdrawalStatusTuple(
                        label = "PROCESSING ⏳",
                        bg = Color(0xFFFEF3C7),
                        text = Color(0xFFB45309),
                        border = Color(0xFFFCD34D),
                        icon = Icons.Default.HourglassTop,
                        subtext = "Queued for instant network disbursement"
                    )
                    TransactionStatus.APPROVED, TransactionStatus.COMPLETED -> WithdrawalStatusTuple(
                        label = "COMPLETED ✅",
                        bg = Color(0xFFD1FAE5),
                        text = Color(0xFF065F46),
                        border = Color(0xFF6EE7B7),
                        icon = Icons.Default.CheckCircle,
                        subtext = "Disbursed successfully to recipient"
                    )
                    TransactionStatus.REJECTED -> WithdrawalStatusTuple(
                        label = "FLAGGED 🚩",
                        bg = Color(0xFFFEE2E2),
                        text = Color(0xFF991B1B),
                        border = Color(0xFFFCA5A5),
                        icon = Icons.Default.Flag,
                        subtext = "Account flagged • Contact support"
                    )
                    TransactionStatus.LOCKED -> WithdrawalStatusTuple(
                        label = "MATURING ⚡",
                        bg = Color(0xFFE0E7FF),
                        text = Color(0xFF1E3A8A),
                        border = Color(0xFF93C5FD),
                        icon = Icons.Default.HourglassTop,
                        subtext = "Processing maturity"
                    )
                }

                // Payment method badge color & icon
                val (methodBg, methodColor, methodIcon) = when {
                    wth.payoutMethod.contains("MTN", ignoreCase = true) -> Triple(Color(0xFFFEF08A), Color(0xFF854D0E), Icons.Default.PhoneAndroid)
                    wth.payoutMethod.contains("Airtel", ignoreCase = true) -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), Icons.Default.PhoneAndroid)
                    else -> Triple(Color(0xFFE0E7FF), Color(0xFF1E3A8A), Icons.Default.AccountBalance)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(statusBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = statusIcon,
                                        contentDescription = null,
                                        tint = statusText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = methodBg
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = methodIcon,
                                                    contentDescription = null,
                                                    tint = methodColor,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = wth.payoutMethod,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = methodColor
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "To: ${wth.accountNumber}",
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = dateStr,
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "-%,d RWF".format(wth.amount.toInt()),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RedDanger,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = statusBg,
                                    border = BorderStroke(1.dp, statusBorder)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = statusIcon,
                                            contentDescription = null,
                                            tint = statusText,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = statusLabel,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = statusText
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BentoBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ $subStatusText",
                                fontSize = 10.sp,
                                color = statusText,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Ref: ${wth.id.take(12).uppercase()}",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class WithdrawalStatusTuple(
    val label: String,
    val bg: Color,
    val text: Color,
    val border: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val subtext: String
)
