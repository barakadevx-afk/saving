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
import com.example.data.model.AppCurrency
import com.example.data.model.UserRole
import com.example.ui.components.DepositModalDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.components.AnnouncementsDialog
import com.example.ui.screens.*
import com.example.ui.theme.SmartFutureTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.VaultViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.notification.NotificationHelper.createNotificationChannel(this)
        setContent {
            val viewModel: VaultViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            SmartFutureTheme(darkTheme = isDarkMode) {
                // Request Notification Permission on Android 13+
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            android.util.Log.d("MainActivity", "Notification Permission Granted: $isGranted")
                        }
                    )
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                SmartFutureApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SmartFutureApp(viewModel: VaultViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentWallet by viewModel.currentWallet.collectAsStateWithLifecycle()
    val userCycles by viewModel.userCycles.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val referredUsers by viewModel.referredUsers.collectAsStateWithLifecycle()
    val adminConfig by viewModel.adminConfig.collectAsStateWithLifecycle()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsStateWithLifecycle()
    val pendingDepositRequests by viewModel.pendingDepositRequests.collectAsStateWithLifecycle()
    val allDepositRequests by viewModel.allDepositRequests.collectAsStateWithLifecycle()
    val allWithdrawals by viewModel.allWithdrawals.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val adminLogs by viewModel.adminLogs.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()

    var showAnnouncementsDialog by remember { mutableStateOf(false) }

    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val showDepositModal by viewModel.showDepositModal.collectAsStateWithLifecycle()
    val selectedDepositTier by viewModel.selectedDepositTier.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

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
            onLogin = { phoneOrId, pass -> viewModel.login(phoneOrId, pass) },
            onRegister = { phone, fullName, pass, refCode ->
                viewModel.register(phone, fullName, pass, refCode)
            },
            onDemoLoginUser = { viewModel.login("0788123456", "user123") },
            onDemoLoginAdmin = { viewModel.login("0792828727", "ADMIN@123!") }
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
                        label = { Text(strings.dashboard, color = Color.White) },
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

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color.White) },
                        label = { Text(strings.helpAndFaq, color = Color.White) },
                        selected = activeTab == NavigationTab.FAQ,
                        onClick = {
                            viewModel.selectTab(NavigationTab.FAQ)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_faq")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = GoldAccent) },
                        label = { Text("${strings.webAndDesktop} 🌐💻", color = Color.White, fontWeight = FontWeight.Bold) },
                        selected = activeTab == NavigationTab.WEB_DOWNLOAD,
                        onClick = {
                            viewModel.selectTab(NavigationTab.WEB_DOWNLOAD)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_web_download")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White) },
                        label = { Text(strings.profileSettings, color = Color.White) },
                        selected = activeTab == NavigationTab.PROFILE,
                        onClick = {
                            viewModel.selectTab(NavigationTab.PROFILE)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_settings")
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
                        icon = { Icon(if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = null, tint = Color.White) },
                        label = { Text(if (isDarkMode) strings.lightTheme else strings.darkTheme, color = Color.White) },
                        selected = false,
                        onClick = {
                            viewModel.toggleDarkMode()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp).testTag("nav_theme_toggle")
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.LightGray) },
                        label = { Text(strings.logout, color = Color.LightGray) },
                        selected = false,
                        onClick = {
                            viewModel.logout()
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
                        currentCurrency = selectedCurrency,
                        isDarkMode = isDarkMode,
                        userRole = user.role,
                        userName = user.fullName,
                        unreadAnnouncementsCount = announcements.size,
                        onOpenAnnouncements = { showAnnouncementsDialog = true },
                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                        onCurrencyChange = { curr -> viewModel.setCurrency(curr) },
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onToggleRole = { viewModel.toggleRole() },
                        onOpenNav = { coroutineScope.launch { drawerState.open() } },
                        onRefresh = { viewModel.refreshData() },
                        isLoading = isLoading
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
                            label = { Text(strings.dashboard, fontSize = 10.sp) },
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
                            label = { Text(strings.savingsCycles, fontSize = 10.sp) },
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
                            label = { Text(strings.myWallet, fontSize = 10.sp) },
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
                            label = { Text(strings.transactions, fontSize = 10.sp) },
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
                                label = { Text(strings.adminPanel, fontSize = 10.sp) },
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
                                referredUsers = referredUsers,
                                announcements = announcements,
                                selectedCurrency = selectedCurrency,
                                isLoading = isLoading,
                                onOpenDepositModal = { tierId -> viewModel.openDepositModal(tierId) },
                                onFastForward = { viewModel.triggerCycleWorkerFastForward() },
                                onViewAllTransactions = { viewModel.selectTab(NavigationTab.TRANSACTIONS) },
                                onWithdrawClick = { viewModel.selectTab(NavigationTab.WITHDRAW) },
                                onFaqClick = { viewModel.selectTab(NavigationTab.FAQ) },
                                onWebDownloadClick = { viewModel.selectTab(NavigationTab.WEB_DOWNLOAD) },
                                onOpenAnnouncements = { showAnnouncementsDialog = true },
                                onSimulateReferral = { friendName -> viewModel.simulateFriendReferral(friendName) },
                                onClaimWelcomeBonus = { viewModel.claimWelcomeBonus() }
                            )
                        }

                        NavigationTab.CYCLES -> {
                            SavingsCyclesScreen(
                                cycles = userCycles,
                                strings = strings,
                                selectedCurrency = selectedCurrency,
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
                                selectedCurrency = selectedCurrency,
                                onRequestWithdrawal = { amount, method, accountNum ->
                                    viewModel.requestWithdrawal(amount, method, accountNum)
                                }
                            )
                        }

                        NavigationTab.TRANSACTIONS -> {
                            TransactionsScreen(
                                transactions = userTransactions,
                                strings = strings,
                                selectedCurrency = selectedCurrency
                            )
                        }

                        NavigationTab.FAQ -> {
                            FaqScreen(
                                strings = strings,
                                onNavigateToDeposit = { viewModel.openDepositModal("C") }
                            )
                        }

                        NavigationTab.WEB_DOWNLOAD -> {
                            WebDownloadScreen(
                                strings = strings,
                                onShowMessage = { msg -> viewModel.showMessage(msg) }
                            )
                        }

                        NavigationTab.PROFILE -> {
                            SettingsScreen(
                                user = user,
                                strings = strings,
                                currentLanguage = currentLanguage,
                                selectedCurrency = selectedCurrency,
                                isDarkMode = isDarkMode,
                                onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                                onCurrencyChange = { curr -> viewModel.setCurrency(curr) },
                                onToggleDarkMode = { viewModel.toggleDarkMode() },
                                onToggleRole = { viewModel.toggleRole() },
                                onLogout = { viewModel.logout() }
                            )
                        }

                        NavigationTab.ADMIN -> {
                            AdminPanelScreen(
                                adminConfig = adminConfig,
                                pendingWithdrawals = pendingWithdrawals,
                                pendingDepositRequests = pendingDepositRequests,
                                allDepositRequests = allDepositRequests,
                                allWithdrawals = allWithdrawals,
                                allTransactions = allTransactions,
                                allUsers = allUsers,
                                adminLogs = adminLogs,
                                announcements = announcements,
                                strings = strings,
                                onApproveWithdrawal = { id -> viewModel.approveWithdrawal(id) },
                                onRejectWithdrawal = { id -> viewModel.rejectWithdrawal(id) },
                                onApproveDeposit = { id -> viewModel.approveDepositRequest(id) },
                                onRejectDeposit = { id, note -> viewModel.rejectDepositRequest(id, note) },
                                onUpdateRates = { a, b, c, d -> viewModel.updateRates(a, b, c, d) },
                                onTriggerSettlement = { viewModel.triggerCycleWorkerFastForward() },
                                onAddFundsToUser = { targetUserId, amount, note ->
                                    viewModel.addFundsToUser(targetUserId, amount, note)
                                },
                                onAddNewUserOrAdmin = { fullName, phone, email, pass, role ->
                                    viewModel.addNewUserOrAdmin(fullName, phone, email, pass, role)
                                },
                                onUpdateUserRole = { userId, newRole ->
                                    viewModel.updateUserRole(userId, newRole)
                                },
                                onPostAnnouncement = { title, content, category, isUrgent, imageUrl ->
                                    viewModel.postAnnouncement(title, content, category, isUrgent, imageUrl)
                                },
                                onDeleteAnnouncement = { id ->
                                    viewModel.deleteAnnouncement(id)
                                },
                                onTogglePlatformLock = { isLocked, notice ->
                                    viewModel.togglePlatformLock(isLocked, notice)
                                },
                                onUpdateAdminReserveFund = { amount ->
                                    viewModel.updateAdminReserveFund(amount)
                                }
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
                                referredUsers = referredUsers,
                                isLoading = isLoading,
                                onOpenDepositModal = { tierId -> viewModel.openDepositModal(tierId) },
                                onFastForward = { viewModel.triggerCycleWorkerFastForward() },
                                onViewAllTransactions = { viewModel.selectTab(NavigationTab.TRANSACTIONS) },
                                onWithdrawClick = { viewModel.selectTab(NavigationTab.WITHDRAW) },
                                onFaqClick = { viewModel.selectTab(NavigationTab.FAQ) },
                                onSimulateReferral = { friendName -> viewModel.simulateFriendReferral(friendName) }
                            )
                        }
                    }

                    if (showDepositModal) {
                        DepositModalDialog(
                            selectedTier = selectedDepositTier,
                            strings = strings,
                            availableBalance = currentWallet?.availableBalance ?: 0.0,
                            pendingBonusPercent = currentWallet?.pendingBonusPercent ?: 0.0,
                            onDismiss = { viewModel.closeDepositModal() },
                            onConfirmDeposit = { payFromAvailable ->
                                viewModel.makeDeposit(selectedDepositTier, payFromAvailable)
                            },
                            onSubmitDepositRequest = { amount, txId, screenshotUri ->
                                viewModel.submitDepositRequest(
                                    amount = amount,
                                    tierId = selectedDepositTier,
                                    transactionId = txId,
                                    proofScreenshotUri = screenshotUri
                                )
                            }
                        )
                    }

                    if (showAnnouncementsDialog) {
                        AnnouncementsDialog(
                            announcements = announcements,
                            onDismiss = { showAnnouncementsDialog = false },
                            onDeleteAnnouncement = { annId: String -> viewModel.deleteAnnouncement(annId) },
                            isAdmin = user.role == UserRole.ADMIN
                        )
                    }
                }
            }
        }
    }
}

