package com.eth.vrcarnival.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eth.vrcarnival.data.models.TokenInfo
import com.eth.vrcarnival.ui.components.*
import com.eth.vrcarnival.ui.theme.GameSurface
import com.eth.vrcarnival.ui.theme.GameSurfaceVariant
import com.eth.vrcarnival.ui.theme.GoldTrophy
import com.eth.vrcarnival.ui.theme.RarityEpic
import com.eth.vrcarnival.ui.theme.RarityRare
import com.eth.vrcarnival.ui.theme.RefinedGradients
import com.eth.vrcarnival.ui.theme.TextAccent
import com.eth.vrcarnival.ui.theme.TextPrimary
import com.eth.vrcarnival.ui.theme.TextSecondary
import com.eth.vrcarnival.viewmodel.WalletViewModel
import kotlinx.coroutines.delay
import com.eth.vrcarnival.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var showSendDialog by remember { mutableStateOf(false) }
    var showTokensDialog by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadWalletData()
        viewModel.loadAvailableTokens()
        delay(100)
        showContent = true
    }

    LaunchedEffect(viewModel.selectedChain.chainId) {
        viewModel.loadWalletData()
        viewModel.loadAvailableTokens()
    }

    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn(tween(500))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                WalletHeader(
                    onSendClick = { viewModel.openSendDialog() },
                    onLogoutClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    isSendEnabled = !viewModel.isSendingToken && !viewModel.isVerifyingTransaction
                )
            }

            // Network Selector
            item {
                NetworkSelector(
                    chains = viewModel.getChains(),
                    selectedChain = viewModel.selectedChain,
                    onChainSelected = { viewModel.selectChain(it) },
                    enabled = !viewModel.isLoadingWalletData
                )
            }

            // Wallet Address
            item {
                viewModel.authResponse?.let { auth ->
                    WalletAddressCard(
                        walletAddress = auth.walletAddress,
                        networkName = viewModel.selectedChain.name
                    )
                }
            }

            // Loading indicator
            item {
                AnimatedVisibility(
                    visible = viewModel.isLoadingWalletData,
                    enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                    exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                ) {
                    LoadingCard()
                }
            }

            if (viewModel.selectedChain.chainId == 11155111) {
                viewModel.carTokenBalance?.let { carToken ->
                    item {
                        CarTokenItem(
                            tokenName = carToken.name ?: "Carnival Token",
                            tokenSymbol = carToken.symbol ?: "CAR",
                            balance = carToken.displayValue ?: "0"
                        )
                    }
                }
            }

            item {
                PowerMeterBalance(
                    balance = viewModel.balance?.displayValue ?: "0",
                    symbol = viewModel.balance?.symbol ?: viewModel.selectedChain.symbol,
                    isLoading = viewModel.isLoadingWalletData
                )
            }

            // Tokens Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Assets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (viewModel.tokens.isNotEmpty()) {
                        Text(
                            text = "${viewModel.tokens.size} assets",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tokens List or Empty State
            if (viewModel.tokens.isEmpty() && !viewModel.isLoadingWalletData) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No assets found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "on ${viewModel.selectedChain.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Individual Token Items
                itemsIndexed(viewModel.tokens) { index, token ->
                    var isVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 50L)
                        isVisible = true
                    }

                    InventoryTokenItem(
                        tokenName = token.name ?: "Unknown Token",
                        tokenSymbol = token.symbol ?: "",
                        balance = token.balanceFormatted ?: "0",
                        rarity = when {
                            token.balanceFormatted?.toFloatOrNull() ?: 0f > 1000f -> "legendary"
                            token.balanceFormatted?.toFloatOrNull() ?: 0f > 100f -> "epic"
                            token.balanceFormatted?.toFloatOrNull() ?: 0f > 10f -> "rare"
                            else -> "common"
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (viewModel.showSendDialog) {
        SendTokenDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeSendDialog() }
        )
    }

    if (showTokensDialog) {
        AvailableTokensDialog(
            tokens = viewModel.availableTokens,
            chainName = viewModel.selectedChain.name,
            onDismiss = { showTokensDialog = false }
        )
    }

    LaunchedEffect(viewModel.sendTokenSuccess) {
        viewModel.sendTokenSuccess?.let { txId ->
            Toast.makeText(context, "Transaction sent: ${txId.take(8)}...", Toast.LENGTH_LONG).show()
            viewModel.clearSendTokenSuccess()
        }
    }

    LaunchedEffect(viewModel.sendCarTokenSuccess) {
        viewModel.sendCarTokenSuccess?.let { txId ->
            Toast.makeText(context, "CAR token sent: ${txId.take(8)}...", Toast.LENGTH_LONG).show()
            viewModel.sendCarTokenSuccess = null
        }
    }

    viewModel.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
}

@Composable
fun InventoryTokenItem(
    tokenName: String,
    tokenSymbol: String,
    balance: String,
    rarity: String = "common",
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val rarityGradient = when (rarity.lowercase()) {
        "legendary" -> RefinedGradients.legendaryGradient
        "epic" -> RefinedGradients.epicGradient
        "rare" -> RefinedGradients.rareGradient
        else -> Brush.linearGradient(colors = listOf(GameSurface, GameSurfaceVariant))
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)) + slideInHorizontally(
            tween(400),
            initialOffsetX = { it / 3 }
        )
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(rarityGradient)
                    .border(
                        2.dp,
                        when (rarity.lowercase()) {
                            "legendary" -> GoldTrophy
                            "epic" -> RarityEpic
                            "rare" -> RarityRare
                            else -> GameSurfaceVariant
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Token icon with rarity effect
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when (rarity.lowercase()) {
                                        "legendary" -> RefinedGradients.legendaryGradient
                                        "epic" -> RefinedGradients.epicGradient
                                        "rare" -> RefinedGradients.rareGradient
                                        else -> RefinedGradients.powerMeterGradient
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (tokenSymbol.firstOrNull() ?: "?").toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = tokenName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tokenSymbol,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Rarity indicator
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (rarity.lowercase()) {
                                                "legendary" -> GoldTrophy
                                                "epic" -> RarityEpic
                                                "rare" -> RarityRare
                                                else -> TextSecondary
                                            }
                                        )
                                )
                            }
                        }
                    }

                    // Balance with stack count style
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = balance,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "x${balance.split(".")[0]}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Loading wallet data...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TokenItem(
    token: com.eth.vrcarnival.data.models.TokenBalance,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (token.symbol?.firstOrNull() ?: "?").toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = token.name ?: "Unknown Token",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = token.symbol ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = token.balanceFormatted ?: "0",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SendTokenDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    var recipientAddress by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedTokenType by remember { mutableStateOf("native") } // "native" or "car"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Send Tokens",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "on ${viewModel.selectedChain.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Token type selector (only show on Sepolia)
                if (viewModel.selectedChain.chainId == 11155111 && viewModel.carTokenBalance != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            onClick = { selectedTokenType = "native" },
                            label = { Text(viewModel.selectedChain.symbol) },
                            selected = selectedTokenType == "native",
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            onClick = { selectedTokenType = "car" },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.carnival_token),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("CAR")
                                }
                            },
                            selected = selectedTokenType == "car",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = recipientAddress,
                    onValueChange = { recipientAddress = it },
                    label = { Text("Recipient Address") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccountBox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("0x...") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = {
                        Text("Amount (${if (selectedTokenType == "car") "CAR" else viewModel.selectedChain.symbol})")
                    },
                    leadingIcon = {
                        if (selectedTokenType == "car") {
                            Image(
                                painter = painterResource(id = R.drawable.carnival_token),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("0.001") },
                    shape = RoundedCornerShape(12.dp)
                )

                // Show available balance
                if (selectedTokenType == "car") {
                    viewModel.carTokenBalance?.let { carBalance ->
                        Text(
                            text = "Available: ${carBalance.displayValue} CAR",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldTrophy,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                } else {
                    viewModel.balance?.let { balance ->
                        Text(
                            text = "Available: ${balance.displayValue} ${balance.symbol ?: viewModel.selectedChain.symbol}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = viewModel.isVerifyingTransaction || viewModel.isSendingCarToken,
                    enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                    exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTokenType == "car")
                                GoldTrophy.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = if (selectedTokenType == "car") GoldTrophy else MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (selectedTokenType == "car") "Sending CAR tokens..." else "Verifying transaction...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    LoadingButton(
                        text = if (selectedTokenType == "car") "Send CAR" else "Send ${viewModel.selectedChain.symbol}",
                        isLoading = if (selectedTokenType == "car") viewModel.isSendingCarToken else viewModel.isSendingToken,
                        onClick = {
                            if (selectedTokenType == "car") {
                                viewModel.sendCarTokens(recipientAddress, amount)
                            } else {
                                viewModel.sendTokens(recipientAddress, amount)
                            }
                        },
                        enabled = recipientAddress.isNotBlank() && amount.isNotBlank() &&
                                !viewModel.isVerifyingTransaction && !viewModel.isSendingCarToken,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun AvailableTokensDialog(
    tokens: List<TokenInfo>,
    chainName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available Tokens",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "on $chainName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (tokens.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tokens available",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "on this network",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(tokens) { index, token ->
                            var isVisible by remember { mutableStateOf(false) }

                            LaunchedEffect(Unit) {
                                delay(index * 50L)
                                isVisible = true
                            }

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(tween(400)) + slideInHorizontally(
                                    tween(400),
                                    initialOffsetX = { it / 2 }
                                )
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (token.symbol?.firstOrNull() ?: "?").toString(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = token.name ?: "Unknown Token",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = token.symbol ?: "",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            token.address?.let { address ->
                                                Text(
                                                    text = "${address.take(8)}...${address.takeLast(6)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CarTokenItem(
    tokenName: String,
    tokenSymbol: String,
    balance: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)) + slideInHorizontally(
            tween(400),
            initialOffsetX = { it / 3 }
        )
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RefinedGradients.legendaryGradient)
                    .border(
                        2.dp,
                        GoldTrophy,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.carnival_token),
                            contentDescription = "Carnival Token",
                            modifier = Modifier.size(48.dp),
                            colorFilter = null
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = tokenName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tokenSymbol,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(GoldTrophy)
                                )
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = balance,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldTrophy
                        )
                        Text(
                            text = "CARNIVAL",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextAccent
                        )
                    }
                }
            }
        }
    }
}