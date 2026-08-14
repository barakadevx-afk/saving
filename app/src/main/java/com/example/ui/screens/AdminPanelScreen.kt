package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.AsyncImage
import com.example.ui.components.launchPhoneCallIntent
import com.example.ui.components.launchWhatsAppIntent

@Composable
fun AdminPanelScreen(
    adminConfig: AdminConfigEntity?,
    pendingWithdrawals: List<WithdrawalEntity>,
    pendingDepositRequests: List<DepositRequestEntity> = emptyList(),
    allUsers: List<UserEntity>,
    adminLogs: List<AdminLogEntity>,
    announcements: List<AnnouncementEntity> = emptyList(),
    strings: AppStrings,
    onApproveWithdrawal: (String) -> Unit,
    onRejectWithdrawal: (String) -> Unit,
    onApproveDeposit: (String) -> Unit = {},
    onRejectDeposit: (requestId: String, note: String) -> Unit = { _, _ -> },
    onUpdateRates: (rateA: Double, rateB: Double, rateC: Double, rateD: Double) -> Unit,
    onTriggerSettlement: () -> Unit,
    onAddFundsToUser: (userId: String, amount: Double, note: String) -> Unit = { _, _, _ -> },
    onAddNewUserOrAdmin: (fullName: String, phone: String, email: String, pass: String, role: UserRole) -> Unit = { _, _, _, _, _ -> },
    onUpdateUserRole: (userId: String, newRole: UserRole) -> Unit = { _, _ -> },
    onPostAnnouncement: (title: String, content: String, category: String, isImportant: Boolean) -> Unit = { _, _, _, _ -> },
    onDeleteAnnouncement: (String) -> Unit = {},
    onTogglePlatformLock: (isLocked: Boolean, notice: String) -> Unit = { _, _ -> },
    onUpdateAdminReserveFund: (Double) -> Unit = {}
) {
    val context = LocalContext.current
    var rateAStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierA ?: 0.02) * 100)) }
    var rateBStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierB ?: 0.02) * 100)) }
    var rateCStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierC ?: 0.02) * 100)) }
    var rateDStr by remember { mutableStateOf("%.2f".format((adminConfig?.rateTierD ?: 0.02) * 100)) }

    var announcementTitle by remember { mutableStateOf("") }
    var announcementContent by remember { mutableStateOf("") }
    var announcementCategory by remember { mutableStateOf("NEWS") }
    var isAnnouncementUrgent by remember { mutableStateOf(false) }

    var lockNoticeText by remember { mutableStateOf(adminConfig?.lockNotice ?: "SFC Platform deposits are temporarily scheduled for maintenance. Active savings cycles continue earning yields as normal!") }
    var reserveFundText by remember { mutableStateOf("%.0f".format(adminConfig?.adminReserveFund ?: 20000000.0)) }

    var userQuery by remember { mutableStateOf("") }
    var selectedUserForFunds by remember { mutableStateOf<UserEntity?>(null) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var selectedScreenshotForZoom by remember { mutableStateOf<String?>(null) }

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

    if (selectedScreenshotForZoom != null) {
        Dialog(onDismissRequest = { selectedScreenshotForZoom = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Payment Proof Screenshot 📸", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { selectedScreenshotForZoom = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    AsyncImage(
                        model = selectedScreenshotForZoom,
                        contentDescription = "Zoomed Payment Proof Screenshot",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    }

    if (showAddUserDialog) {
        var newFullName by remember { mutableStateOf("") }
        var newPhone by remember { mutableStateOf("") }
        var newEmail by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var selectedRole by remember { mutableStateOf(UserRole.USER) }

        Dialog(onDismissRequest = { showAddUserDialog = false }) {
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
                            text = "Add New User / Admin 👤",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { showAddUserDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newFullName,
                        onValueChange = { newFullName = it },
                        label = { Text("Full Name*") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_user_fullname_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Phone Number*") },
                        placeholder = { Text("e.g. 078xxxxxxx") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_user_phone_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Email Address (Optional)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_user_email_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Password*") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_user_pass_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Account Role*", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == UserRole.USER,
                            onClick = { selectedRole = UserRole.USER }
                        )
                        Text("Member / Standard User", fontSize = 13.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == UserRole.ADMIN,
                            onClick = { selectedRole = UserRole.ADMIN }
                        )
                        Text("Administrator (Full Admin Access 👑)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OrangeWarning)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (newFullName.isNotBlank() && newPhone.isNotBlank() && newPass.isNotBlank()) {
                                onAddNewUserOrAdmin(newFullName, newPhone, newEmail, newPass, selectedRole)
                                showAddUserDialog = false
                            }
                        },
                        enabled = newFullName.isNotBlank() && newPhone.isNotBlank() && newPass.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("confirm_create_user_btn")
                    ) {
                        Text(text = "Create ${selectedRole.name} Account 🎉", fontWeight = FontWeight.Bold)
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

        // Admin Website / Platform Lock Controls Card
        item {
            val isLocked = adminConfig?.isPlatformLocked == true
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLocked) Color(0xFFFEF2F2) else BentoCardBg
                ),
                border = BorderStroke(1.5.dp, if (isLocked) RedError else BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLocked) "🔒 Platform Status: LOCKED" else "🟢 Platform Status: OPEN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocked) RedError else GreenSuccess
                        )

                        Button(
                            onClick = {
                                onTogglePlatformLock(!isLocked, lockNoticeText)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLocked) GreenSuccess else RedError
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isLocked) "Open Deposits 🔓" else "Lock Deposits 🔒",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Admin Website Lock Schedule & Maintenance Notice:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = lockNoticeText,
                        onValueChange = { lockNoticeText = it },
                        label = { Text("Maintenance / Closed Lock Message") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = GoldAccent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "💡 Note: When platform deposits are locked, users cannot start new deposits. Active savings cycles continue running and earning profits automatically ('profit of user is income it').",
                            fontSize = 11.sp,
                            color = NavyDark,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // System Capital Reserve (20M RWF) Card
        item {
            val reserveVal = adminConfig?.adminReserveFund ?: 20000000.0
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "System Capital Reserve 💰",
                                fontSize = 13.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%,.0f RWF".format(reserveVal),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "(20 Million RWF Admin Reserve)",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = GoldAccent.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, GoldAccent)
                        ) {
                            Text(
                                text = "20M RWF",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = reserveFundText,
                            onValueChange = { reserveFundText = it },
                            label = { Text("Update Reserve (RWF)", color = Color.LightGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Button(
                            onClick = {
                                val amount = reserveFundText.toDoubleOrNull()
                                if (amount != null && amount >= 0) {
                                    onUpdateAdminReserveFund(amount)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Update 20M Reserve", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Post News & Announcements Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Post News & Announcements 📢",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Broadcast updates to all SMART FUTURE CAPITAL (SFC) users",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = announcementTitle,
                        onValueChange = { announcementTitle = it },
                        label = { Text("Announcement Title*") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = announcementContent,
                        onValueChange = { announcementContent = it },
                        label = { Text("Announcement Message / Details*") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = announcementCategory == "NEWS",
                            onClick = { announcementCategory = "NEWS" },
                            label = { Text("NEWS") }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = announcementCategory == "IMPORTANT",
                            onClick = { announcementCategory = "IMPORTANT" },
                            label = { Text("IMPORTANT") }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = announcementCategory == "PROMO",
                            onClick = { announcementCategory = "PROMO" },
                            label = { Text("PROMO") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isAnnouncementUrgent,
                            onCheckedChange = { isAnnouncementUrgent = it }
                        )
                        Text("Mark as Important / Priority Urgent", fontSize = 12.sp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (announcementTitle.isNotBlank() && announcementContent.isNotBlank()) {
                                onPostAnnouncement(
                                    announcementTitle,
                                    announcementContent,
                                    announcementCategory,
                                    isAnnouncementUrgent
                                )
                                announcementTitle = ""
                                announcementContent = ""
                            }
                        },
                        enabled = announcementTitle.isNotBlank() && announcementContent.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Post Announcement Now 📢", fontWeight = FontWeight.Bold)
                    }

                    if (announcements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BentoBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Active Posted Announcements (${announcements.size}):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        announcements.take(5).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = item.content, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                }
                                IconButton(onClick = { onDeleteAnnouncement(item.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
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

        // Pending Deposit Requests Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📥 Pending Deposit Verifications (${pendingDepositRequests.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.3).sp
                )
                if (pendingDepositRequests.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldLight
                    ) {
                        Text("Action Required", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NavyDark, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }
        }

        if (pendingDepositRequests.isEmpty()) {
            item {
                Text(text = "No pending deposit verification requests.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            items(pendingDepositRequests) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, GoldAccent)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Deposit Request #${req.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BentoPrimaryBlue)
                                Text(text = "User ID: ${req.userId}", fontSize = 11.sp, color = TextSecondary)
                                Text(text = req.paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                            Text(
                                text = "%,d RWF".format(req.amount.toInt()),
                                fontWeight = FontWeight.Bold,
                                color = GreenSuccess,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tx ID / Ref: ${req.transactionId}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NavyDark
                                )
                                val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(req.requestedAt))
                                Text(text = dateStr, fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        if (req.proofScreenshotUri.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { selectedScreenshotForZoom = req.proofScreenshotUri }
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp))
                                    .padding(8.dp)
                            ) {
                                AsyncImage(
                                    model = req.proofScreenshotUri,
                                    contentDescription = "Proof Screenshot",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Payment Proof Screenshot 📸", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                    Text("Tap to zoom and view full receipt screenshot", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onApproveDeposit(req.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("approve_dep_${req.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Approve & Credit 💰", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onRejectDeposit(req.id, "Invalid Tx ID or Screenshot") },
                                colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("reject_dep_${req.id}")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Reject ❌", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Pending Withdrawal Requests Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
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

                Button(
                    onClick = { showAddUserDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("admin_add_user_btn")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add User/Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        Text(text = "ID: ${user.id.take(8)}...", fontSize = 11.sp, color = TextSecondary)

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val targetRole = if (user.role == UserRole.ADMIN) UserRole.USER else UserRole.ADMIN
                                    onUpdateUserRole(user.id, targetRole)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("toggle_role_${user.id}")
                            ) {
                                Text(
                                    text = if (user.role == UserRole.ADMIN) "Make User" else "Make Admin 👑",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { selectedUserForFunds = user },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
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

