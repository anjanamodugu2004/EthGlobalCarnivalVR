package com.eth.vrcarnival.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eth.vrcarnival.ui.components.AnimatedGradientCard
import com.eth.vrcarnival.ui.components.LoadingButton
import com.eth.vrcarnival.ui.components.PulsingDot
import com.eth.vrcarnival.viewmodel.WalletViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
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

                // Main Auth Card
                AnimatedGradientCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (viewModel.isOtpSent) "Verify Your Email" else "Welcome Back",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (viewModel.isOtpSent)
                                "Enter the 6-digit code sent to your email"
                            else
                                "Enter your email to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // OTP Field (animated visibility)
                        AnimatedVisibility(
                            visible = viewModel.isOtpSent,
                            enter = fadeIn(tween(500)) + expandVertically(tween(500)),
                            exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                        ) {
                            Column {
                                OutlinedTextField(
                                    value = otp,
                                    onValueChange = { if (it.length <= 6) otp = it },
                                    label = { Text("Verification Code") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                                )

                                if (otp.isNotEmpty()) {
                                    Text(
                                        text = "${otp.length}/6 digits",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (otp.length == 6)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Action Button
                        LoadingButton(
                            text = if (viewModel.isOtpSent) "Verify & Sign In" else "Send Verification Code",
                            isLoading = viewModel.isLoading,
                            onClick = {
                                if (viewModel.isOtpSent) {
                                    viewModel.verifyOtp(email, otp)
                                } else {
                                    viewModel.sendOtp(email)
                                }
                            },
                            enabled = if (viewModel.isOtpSent) {
                                otp.length == 6 && email.isNotBlank()
                            } else {
                                email.isNotBlank()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )

                        // Back to email button when OTP is shown
                        AnimatedVisibility(
                            visible = viewModel.isOtpSent && !viewModel.isLoading,
                            enter = fadeIn(tween(500)),
                            exit = fadeOut(tween(300))
                        ) {
                            TextButton(
                                onClick = {
                                    otp = ""
                                    // You might want to add a reset OTP function to viewModel
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Use different email")
                            }
                        }
                    }
                }

                // Error Card
                AnimatedVisibility(
                    visible = viewModel.error != null,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)),
                    exit = fadeOut(tween(300)) + slideOutVertically(tween(300))
                ) {
                    viewModel.error?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}