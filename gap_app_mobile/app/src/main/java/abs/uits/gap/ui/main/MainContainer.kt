package abs.uits.gap.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import abs.uits.gap.GapApplication
import abs.uits.gap.ui.auth.AuthViewModel
import abs.uits.gap.ui.group.GroupListScreen
import abs.uits.gap.ui.group.GroupViewModel

@Composable
fun MainContainer(
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as GapApplication
    val profileFactory = remember { ProfileViewModelFactory(app.authRepository) }
    val profileViewModel: ProfileViewModel = viewModel(factory = profileFactory)

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.History,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )
    
    val bluePrimary = Color(0xFF1976D2)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = bluePrimary
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = bluePrimary,
                            selectedTextColor = bluePrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFE3F2FD)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedItem) {
                0 -> GroupListScreen(
                    authViewModel = authViewModel,
                    groupViewModel = groupViewModel,
                    onNavigateToDetail = onNavigateToDetail
                )
                1 -> HistoryScreen()
                2 -> NotificationsScreen()
                3 -> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
