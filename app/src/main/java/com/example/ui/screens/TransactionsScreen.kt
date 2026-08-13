package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.*

enum class TxFilterMode { ALL, PENDING, REWARDED, DEPOSITS, WITHDRAWALS }

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    strings: AppStrings
) {
    var selectedFilter by remember { mutableStateOf(TxFilterMode.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    // Summary Calculations
    val totalDeposited = transactions
        .filter { it.type == TransactionType.DEPOSIT && (it.status == TransactionStatus.COMPLETED || it.status == TransactionStatus.LOCKED) }
        .sumOf { it.amount }

    val totalRewardsEarned = transactions
        .filter { (it.type == TransactionType.CYCLE_REWARD || it.type == TransactionType.REFERRAL_BONUS) && it.status == TransactionStatus.COMPLETED }
        .sumOf { it.amount }

    val totalWithdrawn = transactions
        .filter { it.type == TransactionType.WITHDRAWAL && (it.status == TransactionStatus.APPROVED || it.status == TransactionStatus.COMPLETED) }
        .sumOf { it.amount }

    val pendingCount = transactions.count { it.status == TransactionStatus.PENDING || it.status == TransactionStatus.LOCKED }

    val filteredList = transactions.filter { tx ->
        val matchesCategory = when (selectedFilter) {
            TxFilterMode.ALL -> true
            TxFilterMode.PENDING -> tx.status == TransactionStatus.PENDING || tx.status == TransactionStatus.LOCKED
            TxFilterMode.REWARDED -> (tx.type == TransactionType.CYCLE_REWARD || tx.type == TransactionType.REFERRAL_BONUS) && tx.status == TransactionStatus.COMPLETED
            TxFilterMode.DEPOSITS -> tx.type == TransactionType.DEPOSIT
            TxFilterMode.WITHDRAWALS -> tx.type == TransactionType.WITHDRAWAL
        }

        val matchesSearch = searchQuery.isBlank() ||
                tx.description.contains(searchQuery, ignoreCase = true) ||
                tx.id.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = strings.transactions,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Full auditable transaction statement & ledger history.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Summary Statement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACCOUNT SUMMARY STATEMENT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.8.sp
                        )
                        if (pendingCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldLight
                            ) {
                                Text(
                                    text = "$pendingCount Active Lock(s)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangeWarning,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Deposited", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "%,d RWF".format(totalDeposited.toInt()),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimaryBlue
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("50% Profit Yields", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "+%,d RWF".format(totalRewardsEarned.toInt()),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenSuccess
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Payouts", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "%,d RWF".format(totalWithdrawn.toInt()),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by description or transaction ID...") },
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
                    .testTag("tx_search_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Category Filter Pills Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == TxFilterMode.ALL,
                    onClick = { selectedFilter = TxFilterMode.ALL },
                    label = { Text("All (${transactions.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = selectedFilter == TxFilterMode.PENDING,
                    onClick = { selectedFilter = TxFilterMode.PENDING },
                    label = { Text("Pending ⏳", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = selectedFilter == TxFilterMode.REWARDED,
                    onClick = { selectedFilter = TxFilterMode.REWARDED },
                    label = { Text("Rewarded 🎁", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = selectedFilter == TxFilterMode.DEPOSITS,
                    onClick = { selectedFilter = TxFilterMode.DEPOSITS },
                    label = { Text("Deposits 📥", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = selectedFilter == TxFilterMode.WITHDRAWALS,
                    onClick = { selectedFilter = TxFilterMode.WITHDRAWALS },
                    label = { Text("Withdrawals 📤", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Transactions List
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
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
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No transactions found in this view.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredList) { tx ->
                TransactionItemRow(tx = tx)
            }
        }
    }
}

