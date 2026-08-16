package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.i18n.AppStrings
import com.example.data.model.AppCurrency
import com.example.data.model.TransactionStatus
import com.example.data.model.WalletEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.components.TransparencyNoticeBanner
import com.example.ui.theme.*
import java.io.File
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
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val available = wallet?.availableBalance ?: 0.0
    val locked = wallet?.lockedBalance ?: 0.0

    var withdrawAmountText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("MTN Mobile Money") }
    var accountNumberText by remember { mutableStateOf("+250 788 123 456") }
    var methodExpanded by remember { mutableStateOf(false) }

    var viewingReceiptForWithdrawal by remember { mutableStateOf<WithdrawalEntity?>(null) }

    val isCrypto = selectedMethod.contains("USDT")
    val isTrc20 = selectedMethod.contains("TRC20")
    val isBep20 = selectedMethod.contains("BEP20")

    val amountVal = withdrawAmountText.toDoubleOrNull() ?: 0.0
    val estimatedUsdt = if (amountVal > 0) amountVal / 1450.0 else 0.0
    val isValid = amountVal > 0 && amountVal <= available && accountNumberText.isNotBlank()

    // Receipt Dialog Preview
    if (viewingReceiptForWithdrawal != null) {
        val wth = viewingReceiptForWithdrawal!!
        val receiptSummaryText = generateWithdrawalReceiptText(wth)

        Dialog(onDismissRequest = { viewingReceiptForWithdrawal = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth().testTag("receipt_preview_dialog")
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = BentoPrimaryBlue)
                            Text("Withdrawal Receipt 📄", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        }
                        IconButton(onClick = { viewingReceiptForWithdrawal = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = receiptSummaryText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF38BDF8),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(receiptSummaryText))
                                Toast.makeText(context, "Receipt text copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("copy_receipt_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                downloadOrShareWithdrawalReceipt(context, wth)
                                viewingReceiptForWithdrawal = null
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                            modifier = Modifier.weight(1.5f).testTag("download_receipt_action_btn")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download / Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
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
                                text = { Text("📱 MTN Mobile Money", color = TextPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedMethod = "MTN Mobile Money"
                                    if (accountNumberText.startsWith("0x") || accountNumberText.startsWith("T")) {
                                        accountNumberText = "+250 788 123 456"
                                    }
                                    methodExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📱 Airtel Money", color = TextPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedMethod = "Airtel Money"
                                    if (accountNumberText.startsWith("0x") || accountNumberText.startsWith("T")) {
                                        accountNumberText = "+250 733 123 456"
                                    }
                                    methodExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🏦 Bank Account (BK / I&M / Equity)", color = TextPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedMethod = "Bank Account"
                                    if (accountNumberText.startsWith("0x") || accountNumberText.startsWith("T")) {
                                        accountNumberText = "00044-012345678-90"
                                    }
                                    methodExpanded = false
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BentoBorder)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("🪙 Crypto USDT (TRC20)", color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Surface(
                                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("TRON", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF059669), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    selectedMethod = "Crypto USDT (TRC20)"
                                    accountNumberText = "TR7NHkorK8y62zMpWC2AbWGE8326xTRON"
                                    methodExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("🪙 Crypto USDT (BEP20)", color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Surface(
                                            color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("BSC", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    selectedMethod = "Crypto USDT (BEP20)"
                                    accountNumberText = "0x7130d2A12B9BCbFAe4f2634d864A1Ee1Ce3Ead9c"
                                    methodExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = accountNumberText,
                        onValueChange = { accountNumberText = it },
                        label = {
                            Text(
                                when {
                                    isTrc20 -> "USDT TRC20 Wallet Address (TRON)"
                                    isBep20 -> "USDT BEP20 Wallet Address (BNB Chain)"
                                    selectedMethod == "Bank Account" -> "Bank Account & Routing (BK / I&M)"
                                    else -> "MoMo Phone Number"
                                }
                            )
                        },
                        placeholder = {
                            Text(
                                when {
                                    isTrc20 -> "Starts with T (e.g. TR7NH...)"
                                    isBep20 -> "Starts with 0x (e.g. 0x7130...)"
                                    selectedMethod == "Bank Account" -> "Account number & bank name"
                                    else -> "+250 788..."
                                }
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payout_account_input"),
                        singleLine = true
                    )

                    if (isCrypto) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isTrc20) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, if (isTrc20) Color(0xFFA7F3D0) else Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isTrc20) "🪙 TRON TRC-20 Network" else "🪙 BNB Smart Chain (BEP-20)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTrc20) Color(0xFF065F46) else Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "⚡ 0 Gas Fee (Treasury Sponsored)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Estimated Payout:",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "≈ %.2f USDT".format(estimatedUsdt),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "Rate: 1 USD ≈ 1,450 RWF • Instant on-chain settlement upon admin dispatch.",
                                    fontSize = 9.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

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
                        Text(
                            text = if (isCrypto) "Submit USDT Withdrawal Request 🪙" else "Submit Withdrawal Request ⬆️",
                            fontWeight = FontWeight.Bold
                        )
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
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Ref: ${wth.id.take(12).uppercase()}",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Receipt Action Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewingReceiptForWithdrawal = wth },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BentoPrimaryBlue.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = BentoPrimaryBlue
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("download_receipt_btn_${wth.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Download Receipt",
                                    modifier = Modifier.size(13.dp),
                                    tint = BentoPrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Download Receipt 📄",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun generateWithdrawalReceiptText(wth: WithdrawalEntity): String {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(wth.requestedAt))
    val processedDateStr = if (wth.processedAt != null) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(wth.processedAt))
    } else "PENDING DISBURSEMENT"

    val statusDescription = when (wth.status) {
        TransactionStatus.COMPLETED, TransactionStatus.APPROVED -> "COMPLETED & DISBURSED"
        TransactionStatus.PENDING -> "PROCESSING / QUEUED"
        TransactionStatus.REJECTED -> "FLAGGED / UNDER REVIEW"
        TransactionStatus.LOCKED -> "MATURING"
    }

    val isCrypto = wth.payoutMethod.contains("USDT", ignoreCase = true)
    val estimatedUsdt = wth.amount / 1450.0

    val formattedGross = "%,d RWF".format(wth.amount.toInt())
    val formattedNet = "%,d RWF".format(wth.amount.toInt())
    val cryptoDetails = if (isCrypto) {
        "Crypto Equivalent (Est): ~ %.2f USDT (1 USD ≈ 1,450 RWF)\nNetwork Gas Fee (USDT): 0 USDT (Treasury Sponsored)\n".format(estimatedUsdt)
    } else ""

    return """
============================================================
              FUTURE SMART CAPITAL
          OFFICIAL WITHDRAWAL PROOF OF RECEIPT
============================================================
Transaction Reference : ${wth.id}
Request Timestamp     : $dateStr
Disbursement Status   : $statusDescription (${wth.status.name})
Processed Timestamp   : $processedDateStr

------------------- PAYMENT BREAKDOWN ----------------------
Payout Destination    : ${wth.payoutMethod}
${if (isCrypto) "Recipient Wallet Addr : ${wth.accountNumber}" else "Recipient Account     : ${wth.accountNumber}"}
Gross Withdrawal (RWF): $formattedGross
${cryptoDetails}Processing & MoMo Fee : 0 RWF (FREE)
Net Disbursed Amount  : $formattedNet

------------------ SECURITY & LIQUIDITY --------------------
Liquidity Reserve     : Backed by 20,000,000 RWF Capital Fund
Platform Merchant Code: 1799283
Helpline / WhatsApp   : +250 792 828 727
Support Email         : support@futuresmartcapital.rw

============================================================
This receipt serves as official proof of payout transaction
from FUTURE SMART CAPITAL. Keep this record for your audit.
============================================================
""".trimIndent()
}

fun downloadOrShareWithdrawalReceipt(context: Context, wth: WithdrawalEntity) {
    val receiptText = generateWithdrawalReceiptText(wth)
    val fileName = "SFC_Withdrawal_Receipt_${wth.id.take(8)}.txt"

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(receiptText.toByteArray())
                }
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            file.writeText(receiptText)
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "SFC Withdrawal Receipt #${wth.id.take(8)}")
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Download / Share Withdrawal Receipt"))
        Toast.makeText(context, "Receipt saved to Downloads & ready to share! 📄", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "SFC Withdrawal Receipt #${wth.id.take(8)}")
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        context.startActivity(Intent.createChooser(sendIntent, "SFC Withdrawal Receipt"))
        Toast.makeText(context, "Receipt ready! 📄", Toast.LENGTH_SHORT).show()
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
