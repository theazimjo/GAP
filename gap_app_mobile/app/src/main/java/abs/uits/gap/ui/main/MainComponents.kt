package abs.uits.gap.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Asosiy", Icons.Default.Group)
    object History : BottomNavItem("history", "Tarix", Icons.Default.History)
    object Notifications : BottomNavItem("notifications", "Bildirish", Icons.Default.Notifications)
    object Profile : BottomNavItem("profile", "Profil", Icons.Default.Person)
}

@Composable
fun PlaceholderScreen(title: String, description: String, icon: ImageVector) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF1976D2).copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, color = Color.Gray, fontSize = 16.sp)
        }
    }
}

@Composable
fun HistoryScreen() {
    PlaceholderScreen("To'lovlar Tarixi", "Sizning barcha GAP to'lovlaringiz shu yerda chiqadi", Icons.Default.History)
}

@Composable
fun NotificationsScreen() {
    PlaceholderScreen("Bildirishnomalar", "Yangi xabarlar va eslatmalar shu yerda bo'ladi", Icons.Default.Notifications)
}

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

    val bluePrimary = Color(0xFF1976D2)
    val lightGray = Color(0xFFF0F4F8)

    LaunchedEffect(updateState) {
        if (updateState is UpdateProfileState.Success) {
            showEditModal = false
            scope.launch { snackbarHostState.showSnackbar("Profil yangilandi") }
            viewModel.resetUpdateState()
        } else if (updateState is UpdateProfileState.Error) {
            scope.launch { snackbarHostState.showSnackbar((updateState as UpdateProfileState.Error).message) }
            viewModel.resetUpdateState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = lightGray
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(lightGray)) {
            when (val currentState = state) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = bluePrimary
                    )
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentState.message, color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchProfile() }) {
                            Text("Qayta urinish")
                        }
                    }
                }
                is ProfileState.Success -> {
                    val user = currentState.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { showEditModal = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Tahrirlash", tint = bluePrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Avatar Section
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.White, CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color(0xFFE3F2FD)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        user.name.take(1).uppercase(),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = bluePrimary
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Faol foydalanuvchi", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        // Info Cards
                        ProfileInfoRow(label = "Telefon raqam", value = user.phone, icon = Icons.Default.Phone)
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileInfoRow(label = "A'zo ID", value = "#${user.id.takeLast(6).uppercase()}", icon = Icons.Default.ContactPage)
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tizimdan chiqish", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Profilni tahrirlash", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Foydalanuvchi ismi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        onSave(name)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !isUpdating,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Saqlash", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            }
        }
    }
}
