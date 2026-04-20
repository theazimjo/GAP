package abs.uits.gap.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import abs.uits.gap.features.auth.presentation.components.TelegramLoginButton
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.content.Intent
import android.net.Uri
import abs.uits.gap.core.telegram.TelegramLogin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToOtp: (String) -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Premium Color Palette
    val themeBg = Color(0xFFF0EFE9)
    val themeRust = Color(0xFFB24F2C)
    val themeText = Color(0xFF424242)
    val themeSecondaryText = Color(0xFF757575)

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            val errorMessage = (authState as AuthState.Error).message
            scope.launch { snackbarHostState.showSnackbar(errorMessage) }
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = themeBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Section
                Text(
                    text = "GAP",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = themeRust,
                    letterSpacing = 2.sp
                )
                
                Text(
                    text = "Do'stlar bilan birga",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeSecondaryText,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(80.dp))
                
                // Telegram Login Button
                TelegramLoginButton(
                    onClick = {
                        val botUsername = "gap_sign_bot"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$botUsername?start=login"))
                        context.startActivity(intent)
                    },
                    isLoading = authState == AuthState.Loading
                )
                
                Spacer(modifier = Modifier.height(100.dp))
            }
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 40.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tizimga kirish orqali siz foydalanish shartlariga va maxfiylik siyosatiga rozilik bildirasiz",
                    fontSize = 12.sp,
                    color = themeSecondaryText.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
