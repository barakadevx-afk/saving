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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.*

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    strings: AppStrings
) {
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = transactions.filter { tx ->
        (selectedFilter == null || tx.type == selectedFilter) &&
        (searchQuery.isBlank() || tx.description.contains(searchQuery, ignoreCase = true) || tx.id.contains(searchQuery, ignoreCase = true))
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
                text = strings.transactions,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Full auditable transaction statement history.",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search transactions...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_search_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = selectedFilter == TransactionType.DEPOSIT,
                    onClick = { selectedFilter = TransactionType.DEPOSIT },
                    label = { Text("Deposits", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = selectedFilter == TransactionType.CYCLE_REWARD,
                    onClick = { selectedFilter = TransactionType.CYCLE_REWARD },
                    label = { Text("Rewards", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = selectedFilter == TransactionType.WITHDRAWAL,
                    onClick = { selectedFilter = TransactionType.WITHDRAWAL },
                    label = { Text("Withdrawals", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No matching transactions found.", color = TextSecondary, fontSize = 13.sp)
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
