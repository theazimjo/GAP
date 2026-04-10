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

    // iOS Colors
    val iosBlue = Color(0xFF007AFF)
    val iosBg = Color(0xFFF2F2F7)
    val iosRed = Color(0xFFFF3B30)

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
        containerColor = iosBg
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = state) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = iosBlue
                    )
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentState.message, color = iosRed)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.fetchProfile() }) {
                            Text("Qayta urinish", color = iosBlue)
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
                        // iOS Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Profil",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Tahrirlash",
                                color = iosBlue,
                                fontSize = 17.sp,
                                modifier = Modifier.clickable { showEditModal = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // iOS Avatar Section
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(Color(0xFFE5E5EA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                user.name.take(1).uppercase(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF8E8E93)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(user.name, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Text(
                            "Faol a'zo", 
                            color = Color(0xFF34C759), 
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Info Grouped Card (iOS Settings Style)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                ProfileInfoRow(label = "Telefon raqam", value = user.phone, icon = Icons.Default.Phone)
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 54.dp),
                                    color = Color(0xFFC6C6C8),
                                    thickness = 0.5.dp
                                )
                                ProfileInfoRow(label = "A'zo ID", value = "#${user.id.takeLast(6).uppercase()}", icon = Icons.Default.ContactPage)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Logout as an iOS row
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clickable { onLogout() },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Tizimdan chiqish",
                                    color = iosRed,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Normal
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
    val iosBlue = Color(0xFF007AFF)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF2F2F7),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            // iOS Modal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bekor qilish",
                    color = iosBlue,
                    fontSize = 17.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
                Text(
                    text = "Tahrirlash",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (isUpdating) {
                    CircularProgressIndicator(color = iosBlue, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Saqlash",
                        color = if (name.isNotBlank()) iosBlue else Color(0xFF8E8E93),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(enabled = name.isNotBlank()) { onSave(name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // iOS Input Row
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Ism", color = Color(0xFFC7C7CC)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = iosBlue
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 17.sp)
                )
            }
            HorizontalDivider(color = Color(0xFFC6C6C8), thickness = 0.5.dp)
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(Color(0xFF007AFF), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 17.sp, color = Color.Black)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, fontSize = 17.sp, color = Color(0xFF8E8E93))
    }
}
