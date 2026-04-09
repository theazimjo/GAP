package abs.uits.gap.ui.auth

import android.Manifest
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Launcher to request SEND_SMS permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                val smsManager = SmsManager.getDefault()
                val finalCode = otpCode 
                smsManager.sendTextMessage(phone, null, "Sizning GAP tasdiqlash kodingiz: \$finalCode", null, null)
                Toast.makeText(context, "SMS muvaffaqiyatli jo'natildi!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Xatolik: \${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "SMS ruxsati berilmadi", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateNext()
        } else if (authState is AuthState.Error) {
            scope.launch {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tasdiqlash kodi") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Orqaga")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Kodni kiriting",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "\$phone raqamiga (yoki orqa fonda o'zingizga) tasdiqlash kodi yuborildi.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = codeInput,
                onValueChange = { codeInput = it },
                label = { Text("SMS Kod") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (codeInput.isNotEmpty()) {
                        viewModel.verifyOtp(phone, codeInput)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = authState != AuthState.Loading
            ) {
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Tasdiqlash", fontSize = 18.sp)
                }
            }
        }
    }
}
