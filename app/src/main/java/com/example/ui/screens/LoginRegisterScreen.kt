package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
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

@Composable
fun LoginRegisterScreen(
    strings: AppStrings,
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (email: String, phone: String, fullName: String, pass: String, role: UserRole) -> Unit,
    onDemoLoginUser: () -> Unit,
    onDemoLoginAdmin: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    var emailText by remember { mutableStateOf("jean@barakavault.rw") }
    var passwordText by remember { mutableStateOf("user123") }
    var phoneText by remember { mutableStateOf("+250 788 123 456") }
    var fullNameText by remember { mutableStateOf("Jean Nshimiyimana") }
    var selectedRole by remember { mutableStateOf(UserRole.USER) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = BentoCardBg),
            border = BorderStroke(1.dp, BentoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Branding Logo
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GoldAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text("B", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isRegisterMode) "Create an Account" else "Welcome Back!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isRegisterMode) "Join Baraka Savings Vault today." else "Login to continue to your account",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = fullNameText,
                        onValueChange = { fullNameText = it },
                        label = { Text("Full Name") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        label = { Text("Phone Number") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = emailText,
                    onValueChange = { emailText = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BentoPrimaryBlue) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("login_email_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(14.dp))

                // Role selector for test/demo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Role: ", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
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
                    Text(text = "ADMIN", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (isRegisterMode) {
                            onRegister(emailText, phoneText, fullNameText, passwordText, selectedRole)
                        } else {
                            onLogin(emailText, passwordText)
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
                        text = if (isRegisterMode) "Register Now" else "Login",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick Demo Login Shortcut Buttons
                Text(text = "Or quick-start with demo profiles:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(10.dp))

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

                Spacer(modifier = Modifier.height(18.dp))

                TextButton(
                    onClick = { isRegisterMode = !isRegisterMode },
                    modifier = Modifier.testTag("toggle_register_mode_btn")
                ) {
                    Text(
                        text = if (isRegisterMode) "Already have an account? Login" else "Don't have an account? Register Now",
                        color = BentoPrimaryBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

