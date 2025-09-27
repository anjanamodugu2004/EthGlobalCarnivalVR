package com.eth.vrcarnival.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eth.vrcarnival.ui.components.ElegantPulse
import com.eth.vrcarnival.ui.theme.*
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
        delay(200)
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
                Brush.verticalGradient(
                    colors = listOf(
                        GameBackground,
                        GameBackground.copy(alpha = 0.95f),
                        GameSurface.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        SubtleBackgroundAccent()

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, easing = FastOutSlowInEasing)) +
                    slideInVertically(tween(600), initialOffsetY = { it / 8 })
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    AppLogo()

                    Spacer(modifier = Modifier.height(48.dp))

                    CleanAuthCard(
                        email = email,
                        onEmailChange = { email = it },
                        otp = otp,
                        onOtpChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) otp = it },
                        viewModel = viewModel
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    viewModel.error?.let { error ->
                        CleanErrorCard(error = error)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtleBackgroundAccent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
        ) {
            ElegantPulse(
                color = ElectricBlue.copy(alpha = 0.1f),
                size = 80f,
                intensity = 1.1f
            )
        }
    }
}

@Composable
private fun AppLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElectricBlue.copy(alpha = 0.2f),
                                NeonPurple.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Wallet,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = ElectricBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "VR CARNIVAL",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            letterSpacing = 2.sp
        )

        Text(
            text = "Web3 Wallet",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun CleanAuthCard(
    email: String,
    onEmailChange: (String) -> Unit,
    otp: String,
    onOtpChange: (String) -> Unit,
    viewModel: WalletViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = GameSurface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section header
            Text(
                text = if (viewModel.isOtpSent) "Verify Access" else "Sign In",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = if (viewModel.isOtpSent)
                    "Enter the verification code"
                else
                    "Enter your email to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email field
            CleanTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "your@email.com",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                enabled = !viewModel.isLoading
            )

            AnimatedVisibility(
                visible = viewModel.isOtpSent,
                enter = fadeIn(tween(500)) + expandVertically(tween(500)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))

                    CleanTextField(
                        value = otp,
                        onValueChange = onOtpChange,
                        label = "Verification Code",
                        placeholder = "000000",
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Number,
                        enabled = !viewModel.isLoading
                    )

                    // Clean progress indicator
                    if (otp.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(6) { index ->
                                val isActive = index < otp.length
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isActive) ElectricBlue else GameSurfaceVariant
                                        )
                                )
                                if (index < 5) Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            CleanActionButton(
                onClick = {
                    if (viewModel.isOtpSent) {
                        viewModel.verifyOtp(email, otp)
                    } else {
                        viewModel.sendOtp(email)
                    }
                },
                isLoading = viewModel.isLoading,
                enabled = if (viewModel.isOtpSent) {
                    email.isNotBlank() && otp.length == 6
                } else {
                    email.isNotBlank()
                },
                text = if (viewModel.isOtpSent) "Verify" else "Continue"
            )

            AnimatedVisibility(
                visible = viewModel.isOtpSent && !viewModel.isLoading,
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(300))
            ) {
                TextButton(
                    onClick = { onOtpChange("") },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Use different email")
                }
            }
        }
    }
}

@Composable
private fun CleanTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = if (enabled) ElectricBlue else TextSecondary.copy(alpha = 0.5f)
            )
        },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ElectricBlue,
            unfocusedBorderColor = GameSurfaceVariant,
            disabledBorderColor = GameSurfaceVariant.copy(alpha = 0.5f),
            focusedLabelColor = ElectricBlue,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            disabledTextColor = TextSecondary.copy(alpha = 0.5f),
            cursorColor = ElectricBlue,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CleanActionButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    text: String
) {
    val buttonColor by animateColorAsState(
        targetValue = if (enabled && !isLoading) ElectricBlue else GameSurfaceVariant,
        animationSpec = tween(300),
        label = "button_color"
    )

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = GameSurfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(tween(200)) with fadeOut(tween(200))
            },
            label = "button_content"
        ) { loading ->
            if (loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Processing...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            } else {
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun CleanErrorCard(error: String) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(400)) + slideInVertically(
            tween(400),
            initialOffsetY = { it / 4 }
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DangerRed.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = error,
                    color = DangerRed,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}