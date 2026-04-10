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
import abs.uits.gap.ui.auth.AuthViewModel
import abs.uits.gap.ui.auth.AuthViewModelFactory
import abs.uits.gap.ui.auth.LoginScreen
import abs.uits.gap.ui.auth.OtpScreen
import abs.uits.gap.ui.group.GroupListScreen
import abs.uits.gap.ui.theme.GapTheme

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
                            
                            GroupListScreen(
                                authViewModel = authViewModel,
                                groupViewModel = groupViewModel,
                                onLogout = {
                                    app.tokenStorage.clear()
                                    authViewModel.resetState()
                                    navController.navigate("login") {
                                        popUpTo("groupList") { inclusive = true }
                                    }
                                },
                                onNavigateToDetail = { groupId ->
                                    navController.navigate("groupDetail/$groupId")
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
}