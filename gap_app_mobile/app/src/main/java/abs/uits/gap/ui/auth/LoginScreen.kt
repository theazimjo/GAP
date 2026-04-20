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
import abs.uits.gap.core.telegram.TelegramLogin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToOtp: (String) -> Unit
) {
    var phone by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // iOS System Colors
    val iosBlue = Color(0xFF007AFF)
    val iosBg = Color(0xFFF2F2F7)

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.OtpSent -> {
                onNavigateToOtp(state.originalPhone)
            }
            is AuthState.Error -> {
                val errorMessage = state.message
                scope.launch { snackbarHostState.showSnackbar(errorMessage) }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = iosBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "Xush kelibsiz",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Davom etish uchun telefon raqamingizni kiriting",
                fontSize = 17.sp,
                color = Color(0xFF8E8E93),
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // iOS Style Input Group
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "+998",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    
                    VerticalDivider(
                        modifier = Modifier.height(24.dp),
                        color = Color(0xFFC6C6C8),
                        thickness = 0.5.dp
                    )
                    
                    TextField(
                        value = phone,
                        onValueChange = { if (it.length <= 9) phone = it },
                        placeholder = { Text("000000000", color = Color(0xFFC7C7CC)) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = iosBlue
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (phone.length == 9) {
                        viewModel.requestOtp(phone)
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Telefon raqamini to'liq kiriting") }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = authState != AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = iosBlue,
                    disabledContainerColor = iosBlue.copy(alpha = 0.5f)
                )
            ) {
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Davom etish", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // OR Divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFC6C6C8), thickness = 0.5.dp)
                Text(
                    "YOKI",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93),
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFC6C6C8), thickness = 0.5.dp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TelegramLoginButton(
                onClick = {
                    val telegramLogin = TelegramLogin.Builder("8673065585").build()
                    telegramLogin.login(context as Activity)
                },
                isLoading = authState == AuthState.Loading
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Davom etish orqali siz foydalanish shartlariga rozilik bildirasiz",
                fontSize = 13.sp,
                color = Color(0xFF8E8E93),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )
        }
    }
}
