package abs.uits.gap.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import abs.uits.gap.GapApplication
import abs.uits.gap.ui.auth.AuthViewModel
import abs.uits.gap.ui.group.GroupListScreen
import abs.uits.gap.ui.group.GroupViewModel
import abs.uits.gap.ui.profile.ProfileScreen
import abs.uits.gap.ui.create.CreateScreen
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size

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
    var isBottomBarVisible by remember { mutableStateOf(true) }

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Groups,
        BottomNavItem.Create,
        BottomNavItem.Profile
    )
    
    // Theme Colors
    val themeBeige = Color(0xFFF2EEE4)
    val themeRust = Color(0xFFB24F2C)
    val themeInactive = Color(0xFF8E8E93)

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = themeBeige,
                    tonalElevation = 0.dp // Flat premium look
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = selectedItem == index
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    item.icon, 
                                    contentDescription = item.title,
                                    modifier = Modifier.size(26.dp)
                                ) 
                            },
                            label = { 
                                Text(
                                    item.title, 
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ) 
                            },
                            selected = isSelected,
                            onClick = { 
                                selectedItem = index
                                isBottomBarVisible = true 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = themeRust,
                                selectedTextColor = themeRust,
                                unselectedIconColor = themeInactive,
                                unselectedTextColor = themeInactive,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        },
        containerColor = themeBeige
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(if (isBottomBarVisible) padding else PaddingValues(0.dp))) {
            when (selectedItem) {
                0 -> PlaceholderScreen(
                    title = "Xush kelibsiz",
                    description = "Bugungi kuningiz qanday o'tmoqda?",
                    icon = BottomNavItem.Home.icon
                )
                1 -> GroupListScreen(
                    authViewModel = authViewModel,
                    groupViewModel = groupViewModel,
                    onNavigateToDetail = onNavigateToDetail
                )
                2 -> CreateScreen(
                    viewModel = groupViewModel,
                    onNavigateBack = { 
                        selectedItem = 1
                        isBottomBarVisible = true
                    },
                    onViewChange = { view ->
                        isBottomBarVisible = view == "selection"
                    }
                )
                3 -> ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = onLogout
                )
            }
        }
    }
}
