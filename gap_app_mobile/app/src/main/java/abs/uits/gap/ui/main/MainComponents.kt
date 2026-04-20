package abs.uits.gap.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Home
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
    object Home : BottomNavItem("home", "Asosiy", Icons.Default.Home)
    object Contacts : BottomNavItem("contacts", "Kontaktlar", Icons.Default.Contacts)
    object Groups : BottomNavItem("groups", "Gaplar", Icons.Default.Chat)
    object Profile : BottomNavItem("profile", "Profil", Icons.Default.Person)
}

@Composable
fun PlaceholderScreen(title: String, description: String, icon: ImageVector) {
    val themeBeige = Color(0xFFF2EEE4)
    val themeRust = Color(0xFFB24F2C)
    val themeSecondaryText = Color(0xFF757575)

    Box(
        modifier = Modifier.fillMaxSize().background(themeBeige),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = themeRust.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = themeRust
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                color = themeSecondaryText,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}