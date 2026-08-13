package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.i18n.Translations
import com.example.data.model.UserRole
import com.example.ui.components.DepositModalDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.*
import com.example.ui.theme.BarakaVaultTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.VaultViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarakaVaultTheme {
                BarakaVaultApp()
            }
        }
    }
}

@Composable
fun BarakaVaultApp(viewModel: VaultViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentWallet by viewModel.currentWallet.collectAsStateWithLifecycle()
    val userCycles by viewModel.userCycles.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val adminConfig by viewModel.adminConfig.collectAsStateWithLifecycle()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val adminLogs by viewModel.adminLogs.collectAsStateWithLifecycle()

    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val showDepositModal by viewModel.showDepositModal.collectAsStateWithLifecycle()
    val selectedDepositTier by viewModel.selectedDepositTier.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val strings = Translations.get(currentLanguage)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val user = currentUser
    if (user == null) {
        LoginRegisterScreen(
            strings = strings,
            onLogin = { email, pass -> viewModel.login(email, pass) },
            onRegister = { email, phone, fullName, pass, role ->
                viewModel.register(email, phone, fullName, pass, role)
            },
            onDemoLoginUser = { viewModel.login("jean@barakavault.rw", "user123") },
            onDemoLoginAdmin = { viewModel.login("admin@barakavault.rw", "admin123") }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = NavyDark,
                    drawerContentColor = Color.White
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GoldAccent, shape = MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("B", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.appTitle,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 12.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.White) },
                        label = { Text("Dashboard", color = Color.White) },
                        selected = activeTab == NavigationTab.DASHBOARD,
                        onClick = {
                            viewModel.selectTab(NavigationTab.DASHBOARD)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_dashboard")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White) },
                        label = { Text(strings.myWallet, color = Color.White) },
                        selected = activeTab == NavigationTab.WALLET,
                        onClick = {
                            viewModel.selectTab(NavigationTab.WALLET)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_wallet")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.AddCard, contentDescription = null, tint = Color.White) },
                        label = { Text(strings.depositFunds, color = Color.White) },
                        selected = activeTab == NavigationTab.DEPOSIT,
                        onClick = {
                            viewModel.openDepositModal("C")
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_deposit")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Autorenew, contentDescription = null, tint = Color.White) },
                        label = { Text(strings.savingsCycles, color = Color.White) },
                        selected = activeTab == NavigationTab.CYCLES,
                        onClick = {
                            viewModel.selectTab(NavigationTab.CYCLES)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_cycles")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White) },
                        label = { Text(strings.transactions, color = Color.White) },
                        selected = activeTab == NavigationTab.TRANSACTIONS,
                        onClick = {
                            viewModel.selectTab(NavigationTab.TRANSACTIONS)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_transactions")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.NorthEast, contentDescription = null, tint = Color.White) },
                        label = { Text(strings.withdraw, color = Color.White) },
                        selected = activeTab == NavigationTab.WITHDRAW,
                        onClick = {
                            viewModel.selectTab(NavigationTab.WITHDRAW)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_withdraw")
                    )

                    if (user.role == UserRole.ADMIN) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent) },
                            label = { Text(strings.adminPanel, color = GoldAccent, fontWeight = FontWeight.Bold) },
                            selected = activeTab == NavigationTab.ADMIN,
                            onClick = {
                                viewModel.selectTab(NavigationTab.ADMIN)
                                coroutineScope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_admin")
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.LightGray) },
                        label = { Text(strings.logout, color = Color.LightGray) },
                        selected = false,
                        onClick = {
                            viewModel.login("", "")
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp).testTag("nav_logout")
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopHeaderBar(
                        strings = strings,
                        currentLanguage = currentLanguage,
                        userRole = user.role,
                        userName = user.fullName,
                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                        onToggleRole = { viewModel.toggleRole() },
                        onOpenNav = { coroutineScope.launch { drawerState.open() } }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = NavyDark,
                        contentColor = Color.White
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Dashboard", fontSize = 10.sp) },
                            selected = activeTab == NavigationTab.DASHBOARD,
                            onClick = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyDark,
                                selectedTextColor = GoldAccent,
                                indicatorColor = GoldAccent,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )

                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Autorenew, contentDescription = "Cycles") },
                            label = { Text("Cycles", fontSize = 10.sp) },
                            selected = activeTab == NavigationTab.CYCLES,
                            onClick = { viewModel.selectTab(NavigationTab.CYCLES) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyDark,
                                selectedTextColor = GoldAccent,
                                indicatorColor = GoldAccent,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )

                        NavigationBarItem(
                            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                            label = { Text("Wallet", fontSize = 10.sp) },
                            selected = activeTab == NavigationTab.WALLET || activeTab == NavigationTab.WITHDRAW,
                            onClick = { viewModel.selectTab(NavigationTab.WALLET) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyDark,
                                selectedTextColor = GoldAccent,
                                indicatorColor = GoldAccent,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )

                        NavigationBarItem(
                            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Transactions") },
                            label = { Text("Activity", fontSize = 10.sp) },
                            selected = activeTab == NavigationTab.TRANSACTIONS,
                            onClick = { viewModel.selectTab(NavigationTab.TRANSACTIONS) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyDark,
                                selectedTextColor = GoldAccent,
                                indicatorColor = GoldAccent,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )

                        if (user.role == UserRole.ADMIN) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                                label = { Text("Admin", fontSize = 10.sp) },
                                selected = activeTab == NavigationTab.ADMIN,
                                onClick = { viewModel.selectTab(NavigationTab.ADMIN) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyDark,
                                    selectedTextColor = GoldAccent,
                                    indicatorColor = GoldAccent,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.LightGray
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        NavigationTab.DASHBOARD, NavigationTab.DEPOSIT -> {
                            DashboardScreen(
                                user = user,
                                wallet = currentWallet,
                                cycles = userCycles,
                                transactions = userTransactions,
                                adminConfig = adminConfig,
                                strings = strings,
                                onOpenDepositModal = { tierId -> viewModel.openDepositModal(tierId) },
                                onFastForward = { viewModel.triggerCycleWorkerFastForward() },
                                onViewAllTransactions = { viewModel.selectTab(NavigationTab.TRANSACTIONS) },
                                onWithdrawClick = { viewModel.selectTab(NavigationTab.WITHDRAW) }
                            )
                        }

                        NavigationTab.CYCLES -> {
                            SavingsCyclesScreen(
                                cycles = userCycles,
                                strings = strings,
                                onFastForward = { viewModel.triggerCycleWorkerFastForward() },
                                onOpenDepositModal = { viewModel.openDepositModal("C") }
                            )
                        }

                        NavigationTab.WALLET, NavigationTab.WITHDRAW -> {
                            WalletWithdrawScreen(
                                wallet = currentWallet,
                                withdrawals = userTransactions.filter { it.type == com.example.data.model.TransactionType.WITHDRAWAL }
                                    .map { tx ->
                                        com.example.data.model.WithdrawalEntity(
                                            id = tx.id,
                                            userId = tx.userId,
                                            amount = Math.abs(tx.amount),
                                            payoutMethod = "Mobile Money / Bank",
                                            accountNumber = "+250 788 123 456",
                                            status = tx.status,
                                            requestedAt = tx.timestamp
                                        )
                                    },
                                strings = strings,
                                onRequestWithdrawal = { amount, method, accountNum ->
                                    viewModel.requestWithdrawal(amount, method, accountNum)
                                }
                            )
                        }

                        NavigationTab.TRANSACTIONS -> {
                            TransactionsScreen(
                                transactions = userTransactions,
                                strings = strings
                            )
                        }

                        NavigationTab.ADMIN -> {
                            AdminPanelScreen(
                                adminConfig = adminConfig,
                                pendingWithdrawals = pendingWithdrawals,
                                allUsers = allUsers,
                                adminLogs = adminLogs,
                                strings = strings,
                                onApproveWithdrawal = { id -> viewModel.approveWithdrawal(id) },
                                onRejectWithdrawal = { id -> viewModel.rejectWithdrawal(id) },
                                onUpdateRates = { a, b, c, d -> viewModel.updateRates(a, b, c, d) },
                                onTriggerSettlement = { viewModel.triggerCycleWorkerFastForward() }
                            )
                        }

                        else -> {
                            DashboardScreen(
                                user = user,
                                wallet = currentWallet,
                                cycles = userCycles,
                                transactions = userTransactions,
                                adminConfig = adminConfig,
                                strings = strings,
                                onOpenDepositModal = { tierId -> viewModel.openDepositModal(tierId) },
                                onFastForward = { viewModel.triggerCycleWorkerFastForward() },
                                onViewAllTransactions = { viewModel.selectTab(NavigationTab.TRANSACTIONS) },
                                onWithdrawClick = { viewModel.selectTab(NavigationTab.WITHDRAW) }
                            )
                        }
                    }

                    if (showDepositModal) {
                        DepositModalDialog(
                            selectedTier = selectedDepositTier,
                            strings = strings,
                            availableBalance = currentWallet?.availableBalance ?: 0.0,
                            onDismiss = { viewModel.closeDepositModal() },
                            onConfirmDeposit = { payFromAvailable ->
                                viewModel.makeDeposit(selectedDepositTier, payFromAvailable)
                            }
                        )
                    }
                }
            }
        }
    }
}

