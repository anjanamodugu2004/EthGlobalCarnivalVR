package com.eth.vrcarnival

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eth.vrcarnival.ui.screens.AuthScreen
import com.eth.vrcarnival.ui.screens.WalletScreen
import com.eth.vrcarnival.ui.theme.VrCarnivalTheme
import com.eth.vrcarnival.viewmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VrCarnivalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: WalletViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = if (viewModel.authResponse != null) "wallet" else "auth"
                    ) {
                        composable("auth") {
                            AuthScreen(
                                viewModel = viewModel,
                                onAuthSuccess = {
                                    navController.navigate("wallet") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("wallet") {
                            WalletScreen(
                                viewModel = viewModel,
                                onLogout = {
                                    navController.navigate("auth") {
                                        popUpTo("wallet") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}