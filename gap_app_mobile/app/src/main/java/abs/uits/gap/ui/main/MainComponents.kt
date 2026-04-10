package abs.uits.gap.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Asosiy", Icons.Default.Group)
    object Profile : BottomNavItem("profile", "Profil", Icons.Default.Person)
}

@Composable
fun PlaceholderScreen(title: String, description: String, icon: ImageVector) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F7)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF8E8E93).copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, color = Color(0xFF8E8E93), fontSize = 15.sp)
        }
    }
}