package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.data.model.UserRole
import com.example.ui.theme.*

enum class PortalTab {
    LANDING, SIGN_IN, REGISTER, CALCULATOR
}

@Composable
fun FutureSmartCapitalLogo(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NavyDark,
            border = BorderStroke(2.dp, GoldAccent),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NavyDark, BentoPrimaryBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Logo",
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "FSC",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldAccent,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "FUTURE SMART CAPITAL",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            letterSpacing = (-0.5).sp
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldLight,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "⚡ 50% Profit in 3 Days Guaranteed",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun LoginRegisterScreen(
    strings: AppStrings,
    onLogin: (phoneOrId: String, pass: String) -> Unit,
    onRegister: (phone: String, fullName: String, pass: String, role: UserRole, referralCode: String) -> Unit,
    onDemoLoginUser: () -> Unit,
    onDemoLoginAdmin: () -> Unit
) {
    var activePortalTab by remember { mutableStateOf(PortalTab.LANDING) }
    var isRegisterMode by remember { mutableStateOf(false) }

    var phoneOrIdText by remember { mutableStateOf("0792828727") }
    var passwordText by remember { mutableStateOf("BARAKA@123!") }
    var confirmPasswordText by remember { mutableStateOf("BARAKA@123!") }
    var phoneText by remember { mutableStateOf("0788123456") }
    var fullNameText by remember { mutableStateOf("Jean Nshimiyimana") }
    var referralCodeInputText by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.USER) }

    var adminPassText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Calculator State
    var calcAmountText by remember { mutableStateOf("50000") }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Website Navigation Header Bar
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoCardBg,
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 700.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(GoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("F", color = NavyDark, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "FUTURE SMART CAPITAL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Official Web Portal",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = activePortalTab == PortalTab.LANDING,
                                onClick = { activePortalTab = PortalTab.LANDING },
                                label = { Text("🌐 Web Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BentoPrimaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = activePortalTab == PortalTab.SIGN_IN || activePortalTab == PortalTab.REGISTER,
                                onClick = { activePortalTab = PortalTab.SIGN_IN },
                                label = { Text("🔑 Login / App", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BentoPrimaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Web Portal Main Banner / Calculator View
            if (activePortalTab == PortalTab.LANDING || activePortalTab == PortalTab.CALCULATOR) {
                item {
                    // Hero Banner Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FutureSmartCapitalLogo()

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Grow Your Money with 50% Guaranteed Profit in 3 Days",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Rwanda's Most Trusted Mobile Money Smart Vault. Deposit via MoMo Code 1799283 and receive principal + 50% return straight to your Mobile Money after 72 hours.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        activePortalTab = PortalTab.REGISTER
                                        isRegisterMode = true
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Start Investing Now 🚀", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        activePortalTab = PortalTab.SIGN_IN
                                        isRegisterMode = false
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, GoldAccent)
                                ) {
                                    Text("Sign In to Portal 🔑", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Interactive 50% Profit Calculator Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("⚡ Investment Profit Calculator (50% / 3 Days)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = calcAmountText,
                                onValueChange = { calcAmountText = it.filter { char -> char.isDigit() } },
                                label = { Text("Deposit Amount (RWF)") },
                                leadingIcon = { Text("RWF ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoPrimaryBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            val depositVal = calcAmountText.toDoubleOrNull() ?: 0.0
                            val profitVal = depositVal * 0.50
                            val totalVal = depositVal + profitVal

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BentoHeroCardBg)
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Guaranteed 3-Day Return", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "${String.format("%,.0f", totalVal)} RWF",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GreenSuccess
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Profit (+50%)", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "+${String.format("%,.0f", profitVal)} RWF",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }
                            }
                        }
                    }
                }

                // How MoMo Payment Works Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("📱 How to Invest via Mobile Money (MoMo)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = GoldLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("MoMo Merchant Code:", fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.Medium)
                                    Text("1799283", fontSize = 26.sp, fontWeight = FontWeight.Black, color = NavyDark, letterSpacing = 2.sp)
                                    Text("Merchant Name: FUTURE SMART CAPITAL", fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:*182*8*1*1799283%23"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pay via MoMo (*182#)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/250792828727"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = GreenSuccess)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("WhatsApp Support", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Sign In / Register Application Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header Logo
                        FutureSmartCapitalLogo()

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tab Switcher for Sign In / Create Account
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(BentoHeroCardBg)
                                .padding(4.dp)
                        ) {
                            Button(
                                onClick = {
                                    activePortalTab = PortalTab.SIGN_IN
                                    isRegisterMode = false
                                    errorMessage = null
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isRegisterMode) BentoPrimaryBlue else Color.Transparent,
                                    contentColor = if (!isRegisterMode) Color.White else TextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = null
                            ) {
                                Text(text = "Sign In", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    activePortalTab = PortalTab.REGISTER
                                    isRegisterMode = true
                                    errorMessage = null
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRegisterMode) BentoPrimaryBlue else Color.Transparent,
                                    contentColor = if (isRegisterMode) Color.White else TextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = null
                            ) {
                                Text(text = "Create Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = RedDanger.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, RedDanger.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    color = RedDanger,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (isRegisterMode) {
                            OutlinedTextField(
                                value = fullNameText,
                                onValueChange = { fullNameText = it },
                                label = { Text("Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BentoPrimaryBlue) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = phoneText,
                                onValueChange = { phoneText = it },
                                label = { Text("Phone Number (e.g. 0788123456)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BentoPrimaryBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        } else {
                            OutlinedTextField(
                                value = phoneOrIdText,
                                onValueChange = { phoneOrIdText = it },
                                label = { Text("Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BentoPrimaryBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().testTag("login_phone_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedTextField(
                            value = passwordText,
                            onValueChange = { passwordText = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BentoPrimaryBlue) },
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
                            singleLine = true
                        )

                        if (isRegisterMode) {
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = confirmPasswordText,
                                onValueChange = { confirmPasswordText = it },
                                label = { Text("Confirm Password") },
                                leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = BentoPrimaryBlue) },
                                visualTransformation = PasswordVisualTransformation(),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = referralCodeInputText,
                                onValueChange = { referralCodeInputText = it.uppercase() },
                                label = { Text("Referral Code (Optional - Earn 1k Bonus)") },
                                leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldAccent) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_referral_code_input"),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Account Role Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Account Type: ", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            RadioButton(
                                selected = selectedRole == UserRole.USER,
                                onClick = { selectedRole = UserRole.USER }
                            )
                            Text(text = "USER", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.width(16.dp))

                            RadioButton(
                                selected = selectedRole == UserRole.ADMIN,
                                onClick = { selectedRole = UserRole.ADMIN }
                            )
                            Text(text = "ADMIN ⚡", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        // If Admin Role is selected, require Admin Security Code
                        if (selectedRole == UserRole.ADMIN) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = adminPassText,
                                onValueChange = { adminPassText = it },
                                label = { Text("Admin Security Password (BARAKA@123! or 1799283)") },
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = OrangeWarning) },
                                visualTransformation = PasswordVisualTransformation(),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().testTag("admin_security_key_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangeWarning,
                                    unfocusedBorderColor = OrangeWarning.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (selectedRole == UserRole.ADMIN && adminPassText != "1799283" && adminPassText != "admin123" && adminPassText != "0792828727" && adminPassText != "BARAKA@123!") {
                                    errorMessage = "Invalid Admin Security Password. Enter BARAKA@123! or 1799283"
                                    return@Button
                                }

                                if (isRegisterMode) {
                                    if (fullNameText.isBlank() || phoneText.isBlank() || passwordText.isBlank()) {
                                        errorMessage = "Please fill in all required fields"
                                        return@Button
                                    }
                                    if (passwordText != confirmPasswordText) {
                                        errorMessage = "Passwords do not match!"
                                        return@Button
                                    }
                                    onRegister(phoneText, fullNameText, passwordText, selectedRole, referralCodeInputText)
                                } else {
                                    if (phoneOrIdText.isBlank() || passwordText.isBlank()) {
                                        errorMessage = "Please enter phone number and password"
                                        return@Button
                                    }
                                    onLogin(phoneOrIdText, passwordText)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = if (isRegisterMode) "Create Account Now 🚀" else "Sign In to Account 🔑",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Demo Profiles
                        Text(text = "Quick Demo Access:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDemoLoginUser,
                                modifier = Modifier.weight(1f).testTag("demo_user_btn"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, BentoBorder)
                            ) {
                                Text(text = "Demo User 👤", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            OutlinedButton(
                                onClick = onDemoLoginAdmin,
                                modifier = Modifier.weight(1f).testTag("demo_admin_btn"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, BentoBorder)
                            ) {
                                Text(text = "Demo Admin ⚡", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}



