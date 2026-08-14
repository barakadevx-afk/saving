package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.ui.theme.*

@Composable
fun FutureSmartCapitalLogo(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = NavyDark,
            border = BorderStroke(2.dp, GoldAccent),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
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
                        modifier = Modifier.size(26.dp)
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "FUTURE SMART CAPITAL",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "⚡ 50% Profit in 3 Days Guaranteed",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDark
        )
    }
}

@Composable
fun LoginRegisterScreen(
    strings: AppStrings,
    onLogin: (phoneOrId: String, pass: String) -> Unit,
    onRegister: (phone: String, fullName: String, pass: String, referralCode: String) -> Unit,
    onDemoLoginUser: () -> Unit = {},
    onDemoLoginAdmin: () -> Unit = {}
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    var phoneOrIdText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var confirmPasswordText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var fullNameText by remember { mutableStateOf("") }
    var referralCodeInputText by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    border = BorderStroke(1.dp, BentoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Logo Header
                        FutureSmartCapitalLogo()

                        Spacer(modifier = Modifier.height(22.dp))

                        // Segmented Control Pill: Sign In | Create Account
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEDF2F7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isRegisterMode = false
                                        errorMessage = null
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("tab_signin"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isRegisterMode) BentoPrimaryBlue else Color.Transparent,
                                        contentColor = if (!isRegisterMode) Color.White else TextSecondary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = null
                                ) {
                                    Text(
                                        text = "Sign In",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        isRegisterMode = true
                                        errorMessage = null
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("tab_create_account"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isRegisterMode) BentoPrimaryBlue else Color.Transparent,
                                        contentColor = if (isRegisterMode) Color.White else TextSecondary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = null
                                ) {
                                    Text(
                                        text = "Create Account",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Error message feedback banner
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
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Form Fields
                        if (isRegisterMode) {
                            // Full Name Input
                            OutlinedTextField(
                                value = fullNameText,
                                onValueChange = { fullNameText = it },
                                label = { Text("Full Name") },
                                placeholder = { Text("Enter your full name") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Full Name",
                                        tint = BentoPrimaryBlue
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_name_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Phone Number Input
                            OutlinedTextField(
                                value = phoneText,
                                onValueChange = { phoneText = it.filter { ch -> ch.isDigit() || ch == '+' } },
                                label = { Text("Phone Number") },
                                placeholder = { Text("0792828727") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Phone",
                                        tint = BentoPrimaryBlue
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_phone_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Password Input
                            OutlinedTextField(
                                value = passwordText,
                                onValueChange = { passwordText = it },
                                label = { Text("Password") },
                                placeholder = { Text("Enter password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Password",
                                        tint = BentoPrimaryBlue
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Confirm Password Input
                            OutlinedTextField(
                                value = confirmPasswordText,
                                onValueChange = { confirmPasswordText = it },
                                label = { Text("Confirm Password") },
                                placeholder = { Text("Repeat password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LockReset,
                                        contentDescription = "Confirm Password",
                                        tint = BentoPrimaryBlue
                                    )
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_confirm_password_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Optional Referral Code Input
                            OutlinedTextField(
                                value = referralCodeInputText,
                                onValueChange = { referralCodeInputText = it.uppercase() },
                                label = { Text("Referral Code (Optional)") },
                                placeholder = { Text("e.g. BARAKA50") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = "Referral Code",
                                        tint = GoldAccent
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_referral_code_input"),
                                singleLine = true
                            )
                        } else {
                            // Phone Number Input
                            OutlinedTextField(
                                value = phoneOrIdText,
                                onValueChange = { phoneOrIdText = it },
                                label = { Text("Phone Number") },
                                placeholder = { Text("0792828727") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Phone Number",
                                        tint = BentoPrimaryBlue
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_phone_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Password Input
                            OutlinedTextField(
                                value = passwordText,
                                onValueChange = { passwordText = it },
                                label = { Text("Password") },
                                placeholder = { Text("Enter password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Password",
                                        tint = BentoPrimaryBlue
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Primary Action Button
                        Button(
                            onClick = {
                                if (isRegisterMode) {
                                    if (fullNameText.isBlank() || phoneText.isBlank() || passwordText.isBlank()) {
                                        errorMessage = "Please fill in all required fields"
                                        return@Button
                                    }
                                    if (passwordText != confirmPasswordText) {
                                        errorMessage = "Passwords do not match"
                                        return@Button
                                    }
                                    onRegister(
                                        phoneText.trim(),
                                        fullNameText.trim(),
                                        passwordText,
                                        referralCodeInputText.trim()
                                    )
                                } else {
                                    if (phoneOrIdText.isBlank() || passwordText.isBlank()) {
                                        errorMessage = "Please enter phone number and password"
                                        return@Button
                                    }
                                    onLogin(phoneOrIdText.trim(), passwordText)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("auth_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = if (isRegisterMode) "Create Account 🚀" else "Sign In to Account 🔑",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
