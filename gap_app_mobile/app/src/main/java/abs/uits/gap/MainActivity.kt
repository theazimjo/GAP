package abs.uits.gap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import android.content.Intent
import android.app.Activity
import abs.uits.gap.ui.auth.AuthViewModel
import abs.uits.gap.ui.auth.AuthViewModelFactory
import abs.uits.gap.ui.auth.LoginScreen
import abs.uits.gap.ui.auth.OtpScreen
import abs.uits.gap.ui.theme.GapTheme
import abs.uits.gap.core.telegram.TelegramLogin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val app = application as GapApplication
                    val factory = remember { AuthViewModelFactory(app.authRepository, app.tokenStorage) }
                    val authViewModel: AuthViewModel = viewModel(factory = factory)

                    val navController = rememberNavController()
                    val startDestination = if (app.tokenStorage.getToken() != null) "groupList" else "login"

                    // Handle Telegram Login Redirect
                    LaunchedEffect(intent) {
                        val telegramLogin = TelegramLogin.Builder("8753402796")
                            .setOnLoginSuccess { data ->
                                // Convert hash to params map for the existing ViewModel method
                                val params = mutableMapOf<String, Any>()
                                intent.data?.queryParameterNames?.forEach { name ->
                                    val value = intent.data?.getQueryParameter(name)
                                    if (value != null) params[name] = value
                                }
                                authViewModel.telegramLogin(params)
                            }
                            .setOnLoginError { error -> 
                                // Optional: handle error UI
                            }
                            .build()
                        
                        telegramLogin.handleIntent(intent)
                    }

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToOtp = { phoneStr ->
                                    navController.navigate("otp/$phoneStr")
                                }
                            )
                        }
                        composable("otp/{phone}") { backStackEntry ->
                            val phone = backStackEntry.arguments?.getString("phone") ?: ""
                            val otpCode = (authViewModel.authState.value as? abs.uits.gap.ui.auth.AuthState.OtpSent)?.receivedOtpCode ?: ""
                            
                            OtpScreen(
                                phone = phone,
                                otpCode = otpCode,
                                viewModel = authViewModel,
                                onNavigateNext = {
                                    navController.navigate("groupList") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("groupList") {
                            val groupFactory = remember { abs.uits.gap.ui.group.GroupViewModelFactory(app.groupRepository) }
                            val groupViewModel: abs.uits.gap.ui.group.GroupViewModel = viewModel(factory = groupFactory)
                            
                            abs.uits.gap.ui.main.MainContainer(
                                authViewModel = authViewModel,
                                groupViewModel = groupViewModel,
                                onNavigateToDetail = { groupId ->
                                    navController.navigate("groupDetail/$groupId")
                                },
                                onLogout = {
                                    app.tokenStorage.clear()
                                    authViewModel.resetState()
                                    navController.navigate("login") {
                                        popUpTo("groupList") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("groupDetail/{groupId}") { backStackEntry ->
                            val groupIdStr = backStackEntry.arguments?.getString("groupId") ?: "0"
                            val groupId = groupIdStr.toIntOrNull() ?: 0
                            
                            val detailFactory = remember(groupId) { abs.uits.gap.ui.group.GroupDetailViewModelFactory(app.groupRepository, groupId) }
                            val detailViewModel: abs.uits.gap.ui.group.GroupDetailViewModel = viewModel(key = "groupDetail_$groupId", factory = detailFactory)
                            
                            abs.uits.gap.ui.group.GroupDetailScreen(
                                viewModel = detailViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}