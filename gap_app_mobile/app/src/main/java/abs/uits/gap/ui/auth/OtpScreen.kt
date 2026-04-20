package abs.uits.gap.ui.auth

import android.Manifest
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    phone: String,
    otpCode: String,
    viewModel: AuthViewModel,
    onNavigateNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var codeInput by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // iOS System Colors
    val iosBlue = Color(0xFF007AFF)
    val iosBg = Color(0xFFF2F2F7)
    val iosGray = Color(0xFF8E8E93)
    
    var timeLeft by remember { mutableStateOf(45) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                val smsManager = SmsManager.getDefault()
                // phone already starts with 998, just add "+"
                val destPhone = if (phone.startsWith("+")) phone else "+$phone"
                smsManager.sendTextMessage(destPhone, null, "GAP tasdiqlash kodi: $otpCode", null, null)
                Toast.makeText(context, "SMS yuborildi!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {}
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
    }

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateNext()
        } else if (authState is AuthState.Error) {
            val errorMessage = (authState as AuthState.Error).message
            scope.launch { snackbarHostState.showSnackbar(errorMessage) }
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = iosBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tasdiqlash", fontSize = 17.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp), tint = iosBlue)
                            Text("Orqaga", color = iosBlue, fontSize = 17.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                "Kodni kiriting",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Biz ${formatPhoneNumber(phone)} raqamiga\n6 xonali tasdiqlash kodini yubordik",
                fontSize = 17.sp,
                color = iosGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // 6 Digit OTP Input
            BasicTextField(
                value = codeInput,
                onValueChange = { if (it.length <= 6) codeInput = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                decorationBox = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(6) { index ->
                            val char = when {
                                index >= codeInput.length -> ""
                                else -> codeInput[index].toString()
                            }
                            val isFocused = index == codeInput.length
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.85f)
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isFocused) 2.dp else 0.5.dp,
                                        color = if (isFocused) iosBlue else Color(0xFFC6C6C8),
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    if (codeInput.length == 6) {
                        viewModel.verifyOtp(phone, codeInput)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = authState != AuthState.Loading && codeInput.length == 6,
                colors = ButtonDefaults.buttonColors(
                    containerColor = iosBlue,
                    disabledContainerColor = iosBlue.copy(alpha = 0.5f)
                )
            ) {
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Tasdiqlash", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (timeLeft > 0) {
                Text(
                    "Kodni qayta yuborish: 00:${if (timeLeft < 10) "0$timeLeft" else timeLeft}",
                    color = iosGray,
                    fontSize = 15.sp
                )
            } else {
                Text(
                    "Kod kelmadimi?",
                    color = iosGray,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { 
                        timeLeft = 45
                        viewModel.requestOtp(phone)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Qayta yuborish",
                    color = iosBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { 
                        timeLeft = 45
                        viewModel.requestOtp(phone)
                    }
                )
            }
        }
    }
}

fun formatPhoneNumber(phone: String): String {
    val cleaned = phone.replace(Regex("[^0-9]"), "")
    return if (cleaned.length == 12 && cleaned.startsWith("998")) {
        "+998 ${cleaned.substring(3, 5)} ${cleaned.substring(5, 8)} ${cleaned.substring(8, 10)} ${cleaned.substring(10, 12)}"
    } else {
        if (phone.startsWith("+")) phone else "+$phone"
    }
}
