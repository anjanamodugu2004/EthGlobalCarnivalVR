package com.eth.vrcarnival.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eth.vrcarnival.ui.components.AnimatedGradientCard
import com.eth.vrcarnival.ui.components.LoadingButton
import com.eth.vrcarnival.ui.components.PulsingDot
import com.eth.vrcarnival.viewmodel.WalletViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AuthScreen(
    viewModel: WalletViewModel,
    onAuthSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    LaunchedEffect(viewModel.authResponse) {
        if (viewModel.authResponse != null) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(800)) + slideInVertically(
                tween(800),
                initialOffsetY = { it / 3 }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo/Title Section
                Card(
                    modifier = Modifier.padding(bottom = 32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PulsingDot(
                                color = MaterialTheme.colorScheme.primary,
                                size = 12f
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "VR Carnival",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            PulsingDot(
                                color = MaterialTheme.colorScheme.secondary,
                                size = 8f
                            )
                        }
                        Text(
                            text = "Secure Web3 Wallet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                AnimatedGradientCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(bottom = 28.dp)
                        ) {
                            Text(
                                text = if (viewModel.isOtpSent) "Verify Your Email" else "Welcome Back",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (viewModel.isOtpSent)
                                    "Enter the 6-digit code sent to your email"
                                else
                                    "Enter your email to get started",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }

                        // Email Field with enhanced styling
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text(
                                    "Email Address",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            placeholder = {
                                Text(
                                    "your@email.com",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // OTP Field with improved animations and styling
                        AnimatedVisibility(
                            visible = viewModel.isOtpSent,
                            enter = fadeIn(tween(600, easing = FastOutSlowInEasing)) +
                                    expandVertically(tween(600, easing = FastOutSlowInEasing)),
                            exit = fadeOut(tween(400)) + shrinkVertically(tween(400))
                        ) {
                            Column {
                                OutlinedTextField(
                                    value = otp,
                                    onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) otp = it },
                                    label = {
                                        Text(
                                            "Verification Code",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            "000000",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        focusedLabelColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        cursorColor = MaterialTheme.colorScheme.secondary
                                    )
                                )

                                // Enhanced OTP progress indicator
                                if (otp.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                                    ) {
                                        repeat(6) { index ->
                                            val isActive = index < otp.length
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isActive)
                                                            MaterialTheme.colorScheme.secondary
                                                        else
                                                            MaterialTheme.colorScheme.surfaceVariant
                                                    )
                                                    .animateContentSize()
                                            )
                                            if (index < 5) Spacer(modifier = Modifier.width(6.dp))
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = "${otp.length}/6",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (otp.length == 6)
                                                MaterialTheme.colorScheme.secondary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        // Enhanced Action Button
                        Button(
                            onClick = {
                                if (viewModel.isOtpSent) {
                                    viewModel.verifyOtp(email, otp)
                                } else {
                                    viewModel.sendOtp(email)
                                }
                            },
                            enabled = if (viewModel.isOtpSent) {
                                otp.length == 6 && email.isNotBlank() && !viewModel.isLoading
                            } else {
                                email.isNotBlank() && !viewModel.isLoading
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 2.dp,
                                disabledElevation = 0.dp
                            )
                        ) {
                            AnimatedContent(
                                targetState = viewModel.isLoading,
                                transitionSpec = {
                                    fadeIn(tween(300)) with fadeOut(tween(300))
                                },
                                label = "button_content"
                            ) { isLoading ->
                                if (isLoading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Processing...",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Text(
                                        text = if (viewModel.isOtpSent) "Verify & Sign In" else "Send Verification Code",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Refined back button
                        AnimatedVisibility(
                            visible = viewModel.isOtpSent && !viewModel.isLoading,
                            enter = fadeIn(tween(600)),
                            exit = fadeOut(tween(400))
                        ) {
                            TextButton(
                                onClick = {
                                    otp = ""
                                    // Reset OTP state in viewModel if available
                                },
                                modifier = Modifier.padding(top = 12.dp),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Use different email",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = viewModel.error != null,
                    enter = fadeIn(tween(500)) + slideInVertically(
                        tween(500, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    ),
                    exit = fadeOut(tween(400)) + slideOutVertically(
                        tween(400),
                        targetOffsetY = { it / 4 }
                    )
                ) {
                    viewModel.error?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}