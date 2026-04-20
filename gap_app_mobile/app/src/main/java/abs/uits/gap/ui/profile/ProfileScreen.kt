package abs.uits.gap.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Premium Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Profil",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeText
                            )
                            Text(
                                text = "Tahrirlash",
                                color = themeRust,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { showEditModal = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Premium Avatar Section
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(themeRust.copy(alpha = 0.05f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                user.name.take(1).uppercase(),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeRust
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = themeText)
                        Text(
                            "GAP faol a'zosi", 
                            color = Color(0xFF4CAF50), 
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(48.dp))
                        
                        // Info Grouped Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                ProfileInfoRow(label = "Telefon raqam", value = user.phone, icon = Icons.Default.Phone)
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 54.dp),
                                    color = themeBeige,
                                    thickness = 1.dp
                                )
                                ProfileInfoRow(label = "A'zo ID", value = "#${user.id.takeLast(6).uppercase()}", icon = Icons.Default.ContactPage)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Logout Row
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clickable { onLogout() },
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Tizimdan chiqish",
                                    color = themeRed,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
fun ProfileInfoRow(label: String, value: String, icon: ImageVector) {
    val themeRust = Color(0xFFB24F2C)
    val themeSecondaryText = Color(0xFF757575)

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
        Text(value, fontSize = 16.sp, color = themeSecondaryText)
    }
}
