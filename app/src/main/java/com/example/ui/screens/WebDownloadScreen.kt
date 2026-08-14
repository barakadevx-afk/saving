package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.i18n.AppStrings
import com.example.ui.theme.*

@Composable
fun WebDownloadScreen(
    strings: AppStrings,
    onShowMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val webAppUrl = "https://smartfuturecapital.vercel.app"
    val vercelDeployUrl = "https://smartfuturecapital.vercel.app"
    val winDownloadUrl = "https://smartfuturecapital.vercel.app/downloads/SFC_Vault_v3.0_Setup.exe"
    val winZipUrl = "https://smartfuturecapital.vercel.app/downloads/SFC_Vault_v3.0_Portable.zip"
    val apkUrl = "https://smartfuturecapital.vercel.app/downloads/SFC_Vault_v3.0.apk"

    var isDownloadingWin by remember { mutableStateOf(false) }
    var isDownloadingZip by remember { mutableStateOf(false) }
    var isDownloadingApk by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(NavyDark, NavySidebar)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, GoldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(GreenSuccess, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cross-Platform Access Hub",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "SMART FUTURE CAPITAL (SFC) Anywhere 🌐💻📱",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Manage your high-yield savings cycles effortlessly on Web (Hosted on Vercel), Windows Desktop PC, or Android Mobile.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Web Version Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(BlueLight, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = BluePrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Official Web Application", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text(text = "No installation required • Works in browser", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenLight
                        ) {
                            Text(
                                text = "ONLINE 🟢",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenSuccess,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = webAppUrl,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Baraka Vault Web URL", webAppUrl)
                                    clipboard.setPrimaryClip(clip)
                                    onShowMessage("Web Portal link copied to clipboard! 📋")
                                },
                                modifier = Modifier.size(32.dp).testTag("copy_web_url_btn")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = BluePrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webAppUrl))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    onShowMessage("Opening Web App at $webAppUrl")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("launch_web_portal_btn")
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Open Web Portal 🌐", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Windows Desktop App Download Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.5.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(NavyDark, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(26.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Windows Desktop App 💻", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text(text = "Native Windows 10/11 Edition • v2.4.0", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldLight
                        ) {
                            Text(
                                text = "OFFICIAL 64-BIT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Features highlight grid
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verified Publisher: SMART FUTURE CAPITAL (SFC) Ltd.", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hosted & Deployed via Vercel Global Edge Network with zero downtime", fontSize = 11.sp, color = TextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("High performance, low memory usage, real-time sync with mobile app", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Download Options", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary Windows Installer (.exe)
                    Button(
                        onClick = {
                            isDownloadingWin = true
                            onShowMessage("Starting Download: SFC_Vault_v3.0_Setup.exe (64.2 MB) 📥")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(winDownloadUrl))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("download_windows_exe_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(text = "Download Windows Installer (.exe)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text(text = "Windows 10/11 64-bit • 64.2 MB", fontSize = 10.sp, color = GoldAccent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary Portable (.zip)
                    OutlinedButton(
                        onClick = {
                            isDownloadingZip = true
                            onShowMessage("Starting Download: SFC_Vault_v3.0_Portable.zip (58.1 MB) 📦")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(winZipUrl))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NavyDark),
                        modifier = Modifier.fillMaxWidth().testTag("download_windows_zip_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FolderZip, contentDescription = null, tint = NavyDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Download Windows Portable (.zip) • 58.1 MB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = NavyDark)
                        }
                    }
                }
            }
        }

        // Android APK Download Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(GreenLight, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Android, contentDescription = null, tint = GreenSuccess)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Android APK Installer 📱", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text(text = "Direct APK for Android phones & tablets", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            isDownloadingApk = true
                            onShowMessage("Starting Download: SFC_Vault_v3.0.apk (24.5 MB) 📱")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("download_android_apk_btn")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Download Android APK (v3.0.0) • 24.5 MB", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Installation Guide
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "📖 Windows Installation Instructions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("1. Click 'Download Windows Installer (.exe)' above.", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2. Run 'SFC_Vault_v3.0_Setup.exe' from your Downloads folder.", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("3. If Windows SmartScreen appears: click 'More info' -> 'Run anyway'.", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("4. Launch SMART FUTURE CAPITAL and log in with your phone number and password.", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }
    }
}
