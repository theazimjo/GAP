package abs.uits.gap.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import abs.uits.gap.ui.main.ProfileViewModel
import abs.uits.gap.ui.main.ProfileState
import abs.uits.gap.ui.main.UpdateProfileState
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showEditModal by remember { mutableStateOf(false) }

    // Premium Colors
    val themeRust = Color(0xFFB24F2C)
    val themeBeige = Color(0xFFF2EEE4)
    val themeText = Color(0xFF424242)
    val themeSecondaryText = Color(0xFF757575)
    val themeRed = Color(0xFFD32F2F)

    LaunchedEffect(updateState) {
        if (updateState is UpdateProfileState.Success) {
            showEditModal = false
            scope.launch { snackbarHostState.showSnackbar("Profil yangilandi ✨") }
            viewModel.resetUpdateState()
        } else if (updateState is UpdateProfileState.Error) {
            scope.launch { snackbarHostState.showSnackbar((updateState as UpdateProfileState.Error).message) }
            viewModel.resetUpdateState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = themeBeige
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = state) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themeRust
                    )
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentState.message, color = themeRed)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.fetchProfile() }) {
                            Text("Qayta urinish", color = themeRust)
                        }
                    }
                }
                is ProfileState.Success -> {
                    val user = currentState.user
                    val isVerified = user.phone != null && !user.phone.startsWith("tg_")
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Premium Toolbar-like Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Profil",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeText
                            )
                            Surface(
                                color = themeRust.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { showEditModal = true }
                            ) {
                                Text(
                                    text = "Tahrirlash",
                                    color = themeRust,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Centered Profile Identity
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                modifier = Modifier.size(110.dp),
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 8.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(themeRust.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        user.name.take(1).uppercase(),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeRust
                                    )
                                }
                            }
                            if (isVerified) {
                                Surface(
                                    color = Color(0xFF4CAF50),
                                    shape = CircleShape,
                                    modifier = Modifier.size(28.dp),
                                    border = BorderStroke(2.dp, Color.White)
                                ) {
                                    Icon(
                                        Icons.Default.Check, 
                                        contentDescription = "Verified", 
                                        tint = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = themeText)
                        
                        if (isVerified) {
                            Surface(
                                color = themeRust.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    "TASDIQLANGAN", 
                                    color = themeRust, 
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem("Guruhlar", "12", themeRust)
                            ProfileStatItem("Mablag'", "2.4M", themeRust)
                            ProfileStatItem("Ball", "850", themeRust)
                        }
                        
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        // Info Grouped Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                ProfileInfoRow(
                                    label = "Telefon raqam", 
                                    value = if (isVerified) user.phone else "Tasdiqlanmagan", 
                                    icon = Icons.Default.Phone,
                                    isWarning = !isVerified
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 64.dp, end = 16.dp),
                                    color = themeBeige,
                                    thickness = 1.dp
                                )
                                ProfileInfoRow(
                                    label = "A'zo ID", 
                                    value = "#${user.id.takeLast(6).uppercase()}", 
                                    icon = Icons.Default.ContactPage
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Logout Action
                        TextButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .height(56.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = themeRed)
                        ) {
                            Text(
                                "Tizimdan chiqish",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (showEditModal) {
                        EditProfileModal(
                            currentName = user.name,
                            onDismiss = { showEditModal = false },
                            onSave = { newName ->
                                viewModel.updateProfile(newName)
                            },
                            isUpdating = updateState is UpdateProfileState.Loading
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileModal(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    isUpdating: Boolean
) {
    var name by remember { mutableStateOf(currentName) }
    val themeRust = Color(0xFFB24F2C)
    val themeBeige = Color(0xFFF2EEE4)
    val themeSecondaryText = Color(0xFF757575)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeBeige,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            // Premium Modal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bekor qilish",
                    color = themeSecondaryText,
                    fontSize = 17.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
                Text(
                    text = "Tahrirlash",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
                if (isUpdating) {
                    CircularProgressIndicator(color = themeRust, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Saqlash",
                        color = if (name.isNotBlank()) themeRust else themeSecondaryText.copy(alpha = 0.4f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(enabled = name.isNotBlank()) { onSave(name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Premium Input Surface
            Surface(
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Ism", color = themeSecondaryText.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = themeRust
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 17.sp, color = Color(0xFF424242))
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, icon: ImageVector, isWarning: Boolean = false) {
    val themeRust = Color(0xFFB24F2C)
    val themeSecondaryText = Color(0xFF757575)
    val themeRed = Color(0xFFD32F2F)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(themeRust.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = themeRust)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, color = Color(0xFF424242), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            value, 
            fontSize = 15.sp, 
            color = if (isWarning) themeRed else themeSecondaryText,
            fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal
        )
    }
}
