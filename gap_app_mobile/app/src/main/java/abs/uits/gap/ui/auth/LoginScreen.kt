package abs.uits.gap.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

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

    val bluePrimary = Color(0xFF1976D2)
    val lightGray = Color(0xFFF0F4F8)

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
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("Gap", color = bluePrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back or exit */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = bluePrimary)
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
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = buildAnnotatedString {
                    append("Welcome to your\n")
                    withStyle(style = SpanStyle(color = bluePrimary)) {
                        append("Sanctuary.")
                    }
                },
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Connect seamlessly with your world.\nEnter your phone number to get started.",
                fontSize = 15.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Phone input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(lightGray, RoundedCornerShape(28.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "UZ +998",
                    fontWeight = FontWeight.Bold,
                    color = bluePrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Divider(
                    modifier = Modifier.width(1.dp).height(24.dp),
                    color = Color.LightGray
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("000 000 0000", color = Color.Gray) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("We'll send a code to verify this number", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (phone.isNotEmpty()) {
                        viewModel.requestOtp(phone)
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Please enter your phone number") }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp), spotColor = bluePrimary),
                shape = RoundedCornerShape(28.dp),
                enabled = authState != AuthState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
            ) {
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Or Continue With
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text("OR CONTINUE WITH", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Mock Google Button
                Box(modifier = Modifier.weight(1f).height(56.dp).background(lightGray, RoundedCornerShape(12.dp)).clickable { }, contentAlignment = Alignment.Center) {
                    Text("Google", fontWeight = FontWeight.Bold)
                }
                // Mock iOS Button
                Box(modifier = Modifier.weight(1f).height(56.dp).background(lightGray, RoundedCornerShape(12.dp)).clickable { }, contentAlignment = Alignment.Center) {
                    Text("iOS", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer
            Text(
                text = buildAnnotatedString {
                    append("By continuing, you agree to our ")
                    withStyle(style = SpanStyle(color = bluePrimary)) { append("Terms of Service") }
                    append(" and ")
                    withStyle(style = SpanStyle(color = bluePrimary)) { append("Privacy Policy") }
                    append(".")
                },
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )
        }
    }
}
